package com.lamt2.orchestrator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class MathUtils {
    public static String getRandomModelId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public static float roundFloat(float value) {
        return new BigDecimal(Float.toString(value))
                .setScale(5, RoundingMode.HALF_UP)
                .floatValue();
    }
}
