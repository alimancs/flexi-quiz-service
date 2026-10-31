package com.flexi.quiz_service.util;

import java.util.UUID;

public final class Generating {

    // static
    public Generating(){};

    // generate unique token
    public static String uniqueToken() {
        return UUID.randomUUID().toString();
    }
}