package com.flexi.quiz_service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class Hashing {

    private Hashing() {}   // static utility

    // ---------- Cryptographic digests ----------

    public static String sha256Hex(String input) {
        return hex(digest("SHA-256", input.getBytes(StandardCharsets.UTF_8)));
    }

    public static String sha256Hex(byte[] input) {
        return hex(digest("SHA-256", input));
    }

    // ---------- Constant-time comparison ----------

    /** Use this to compare hashes, NOT String.equals (timing leak). */
    public static boolean safeEquals(String a, String b) {
        if (a == null || b == null) return a == b;
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }

    // ---------- Internals ----------

    private static byte[] digest(String alg, byte[] input) {
        return messageDigest(alg).digest(input);
    }

    private static MessageDigest messageDigest(String alg) {
        try {
            return MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(alg + " not available on this JVM", e);
        }
    }

    private static String hex(byte[] bytes) {
        return HexFormat.of().formatHex(bytes); 
    }
}