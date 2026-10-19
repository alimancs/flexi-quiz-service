# flexi-quiz-service
Quiz Service with endpoints for question bank management, starting/completing attempts, grading submissions, and leaderboard aggregation. Include per-topic analytics for post-exam insight.

## Entity model

```
Topic ──< Question ──< QuestionOption
              │
              └──< ExamQuestion >── Exam ──< Attempt ──< AttemptAnswer
                                              │
                                              └── (student_id, token_hash)
```

**Topic**
- `id`, `name` (unique), `description`

**Question** (MCQ)
- `id`, `topic_id`, `text`, `difficulty` (optional), `active` (soft delete), `created_by`, `created_at`, `updated_at`

**QuestionOption**
- `id`, `question_id`, `label` (A/B/C/D), `text`, `is_correct`
- Constraint: exactly one correct option per question, enforced in the service layer (or with a partial unique index on `question_id WHERE is_correct`).

**Exam**
- `id`, `title`, `duration_seconds`, `opens_at`, `closes_at`, `max_attempts`, `negative_marking` (boolean), `penalty_per_wrong` (decimal), `status` (`DRAFT`, `PUBLISHED`, `CLOSED`), `created_by`

**ExamQuestion** (join table)
- `exam_id`, `question_id`, `position`, `marks` (default 1)
- Unique on `(exam_id, question_id)`.

**Attempt**
- `id`, `exam_id`, `student_id`
- `token_hash` (SHA-256 of the issued token, unique)
- `status` (`IN_PROGRESS`, `SUBMITTED`, `EXPIRED`)
- `started_at`, `expires_at`, `submitted_at`
- `score`, `max_score`, `correct_count`, `wrong_count`, `unanswered_count`
- `version` (`@Version` for optimistic locking)
- Partial unique index: `UNIQUE (exam_id, student_id) WHERE status = 'IN_PROGRESS'`, so a student can't have two live attempts for one exam.

**AttemptAnswer**
- `id`, `attempt_id`, `question_id`, `selected_option_id` (nullable), `is_correct`, `marks_awarded`
- Unique on `(attempt_id, question_id)`.
- Snapshotting `is_correct` and `marks_awarded` at grading time means editing a question later won't change history.


## Endpoint list

Base path: `/api/v1`. Roles: `LECTURER` for management, `STUDENT` for taking exams.

**Question bank (lecturer)**

| Method | Path | Purpose |
|---|---|---|
| POST | `/topics` | Create topic |
| GET | `/topics` | List topics |
| POST | `/questions` | Create MCQ with options |
| GET | `/questions?topicId=&q=&page=` | Search/paginate |
| GET | `/questions/{id}` | Get one |
| PUT | `/questions/{id}` | Update |
| DELETE | `/questions/{id}` | Soft delete (set `active=false`) |

**Exam management (lecturer)**

| Method | Path | Purpose |
|---|---|---|
| POST | `/exams` | Create exam (settings, window, negative marking) |
| PUT | `/exams/{id}/questions` | Set question list and marks |
| POST | `/exams/{id}/publish` | Lock and publish |
| GET | `/exams` / `/exams/{id}` | List / detail |

**Attempts (student)**

| Method | Path | Purpose |
|---|---|---|
| POST | `/exams/{examId}/attempts` | Start attempt. Returns `attemptId`, `token`, `expiresAt`, and the questions **without** `is_correct` |
| GET | `/attempts/{id}` | Resume or inspect state, including remaining seconds |
| PUT | `/attempts/{id}/answers` | Optional autosave of partial answers |
| POST | `/attempts/{id}/submit` | Submit final answers, grade, and return the result |
| GET | `/attempts/{id}/result` | Result with per-question breakdown, only after submission |
| GET | `/students/me/attempts` | Attempt history |

The token goes in a header such as `X-Attempt-Token`, alongside the normal auth credentials.

**Leaderboard and analytics**

| Method | Path | Purpose |
|---|---|---|
| GET | `/exams/{id}/leaderboard?limit=&page=` | Ranked results |
| GET | `/exams/{id}/analytics/topics` | Per-topic accuracy across all attempts (lecturer) |
| GET | `/attempts/{id}/analytics/topics` | Topic breakdown for one student's attempt |

## Behaviour rules per endpoint

**Start attempt**
- Check the exam is `PUBLISHED` and `now` is inside `[opens_at, closes_at]`.
- Check the student hasn't exceeded `max_attempts`.
- Generate 32 random bytes, base64url-encode them as the token, store only the hash, and return the raw token once.
- Set `expires_at = min(started_at + duration, closes_at)`.

**Submit**
1. Load the attempt by id and verify the token hash and student match, otherwise 403.
2. If status is `SUBMITTED`, return the **same stored result** with 200, or 409 if you prefer strict behaviour. Pick one and keep it consistent.
3. If `now > expires_at` (plus an optional few seconds of grace), mark it `EXPIRED` and return **410**.
4. Validate the payload and return **422** for unknown question ids, duplicate answers, options that don't belong to the question, or a missing body.
5. Grade inside one transaction and flip the status to `SUBMITTED`. `@Version` makes a concurrent second submit fail with an `OptimisticLockException`, which you map to the "already submitted" response.

**Grading**
- Correct: `+marks`. Wrong: `-penalty_per_wrong` if `negative_marking` is on, else 0. Unanswered: 0.
- Clamp the total at 0 if you don't want negative scores.

**Leaderboard**
- Rank by `score DESC`, then time taken `ASC` (`submitted_at - started_at`), using `DENSE_RANK()` or `RANK()`.
- For multiple attempts, rank each student's **best** attempt using `DISTINCT ON (student_id)` in Postgres.

**Topic analytics query shape**
```sql
SELECT t.name,
       COUNT(*)                                   AS total,
       COUNT(*) FILTER (WHERE aa.is_correct)      AS correct,
       ROUND(100.0 * COUNT(*) FILTER (WHERE aa.is_correct) / COUNT(*), 1) AS accuracy_pct
FROM attempt_answer aa
JOIN question q ON q.id = aa.question_id
JOIN topic t    ON t.id = q.topic_id
JOIN attempt a  ON a.id = aa.attempt_id
WHERE a.exam_id = :examId AND a.status = 'SUBMITTED'
GROUP BY t.name;
```
