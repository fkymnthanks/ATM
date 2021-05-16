package com.atm.rd.common.util;

/**
 * 产生随机randomId
 */
public class RandomIdGenerator {

    public static String getRandomId() {
        return System.currentTimeMillis() + SaltGenerator.getSalt();
    }
}
