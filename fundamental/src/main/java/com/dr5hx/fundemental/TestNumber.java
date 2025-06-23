package com.dr5hx.fundemental;

import java.math.BigDecimal;

/**
 * fundemental.TestNumber
 * Desc:
 * Date:2024/5/27 10:51
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class TestNumber {
    public static void main(String[] args) {
        double a = 0.0000;
        BigDecimal b = new BigDecimal(a);
        System.out.println(a == 0);
        System.out.println(b.equals(0));
        System.out.println(b.compareTo(BigDecimal.ZERO));
        long bigNumber = 9007199254740993L; // 超过 double 精度范围
        double d = bigNumber;
        System.out.println(bigNumber == d);

    }
}