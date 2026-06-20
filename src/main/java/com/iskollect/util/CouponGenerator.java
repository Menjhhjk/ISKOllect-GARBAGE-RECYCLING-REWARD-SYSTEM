package com.iskollect.util;

import java.util.UUID;

public final class CouponGenerator {
    private CouponGenerator() {
    }

    //generates a random code
    public static String generate() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);
    }
}
