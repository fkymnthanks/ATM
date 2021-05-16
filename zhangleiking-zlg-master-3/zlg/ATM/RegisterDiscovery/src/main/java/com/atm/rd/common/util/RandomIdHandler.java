package com.atm.rd.common.util;

/**
 * 产生随机randomId
 */
public class RandomIdHandler {

    public String getRandomId() {
        return System.currentTimeMillis() + SaltGenerator.getSalt();
    }
}
