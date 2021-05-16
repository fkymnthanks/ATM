package com.openmhealth.common.util;

import java.util.Random;

/**
 * 产生随机“盐”，随机使用了两个字母以及一个数字
 */
public class SaltGenerator {

    public static String getSalt() {
        //case 0: 小写字母；case 1：大写字母
        char[] ret = new char[2];
        Random random = new Random();
        for(int i=0; i<2; i++) {
            int pick = random.nextInt(2);
            switch (pick) {
                case 0:
                    ret[i] = (char)('a' + random.nextInt(26));
                    break;
                case 1:
                    ret[i] = (char)('A' + random.nextInt(26));
                    break;
            }
        }
        return String.valueOf(ret[0]+""+ret[1]) + random.nextInt(10);
    }

    public static void main(String[] args) {
        for(int i=0;i<10;i++)
            System.out.println(getSalt());
    }
}
