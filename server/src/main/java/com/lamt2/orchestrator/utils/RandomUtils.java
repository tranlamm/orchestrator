package com.lamt2.orchestrator.utils;

import java.util.UUID;

public class RandomUtils {
    public static String getRandomModelId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }
}
