package com.dr5hx.fundamental.number;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * BigDecimalDemo
 * Desc:
 * Date:2025/8/14 11:06
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class BigDecimalDemo {
    public static void main(String[] args) {
//        test_decimal();
//        test_round_regular("1.3259999901599999999999999", 10);
//        test_round_regular("-1.3259999901599999999999999", 10);
//        test_round_regular("-1.3259999901699999999999999", 10);
//        test_round_regular("-1.3259999901499999999999999", 10);
        BigDecimal bigDecimal = new BigDecimal("-1.25");
        System.out.println(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP));
        System.out.println(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_DOWN));
        System.out.println(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_EVEN));
        BigDecimal bigDecimal1 = new BigDecimal("-1.04");
        System.out.println(bigDecimal1.setScale(1, BigDecimal.ROUND_HALF_UP));
        System.out.println(bigDecimal1.setScale(1, BigDecimal.ROUND_HALF_DOWN));
        System.out.println(bigDecimal1.setScale(1, BigDecimal.ROUND_HALF_EVEN));
    }

    private static void test_decimal() {
        String val = "1.32999999999999999999999999";
        BigDecimal bigDecimal = new BigDecimal(val, MathContext.UNLIMITED);
        BigDecimal bigDecimal1 = new BigDecimal(val, MathContext.DECIMAL32);
        BigDecimal bigDecimal2 = new BigDecimal(val, MathContext.DECIMAL64);
        BigDecimal bigDecimal3 = new BigDecimal(val, MathContext.DECIMAL128);
        System.out.println(bigDecimal);
        System.out.println(bigDecimal1);
        System.out.println(bigDecimal2);
        System.out.println(bigDecimal3);
    }

    private static void test_round_regular(String val, int newScale) {
        BigDecimal bigDecimal = new BigDecimal(val, MathContext.UNLIMITED);
        System.out.println("ROUND_UP:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_UP));
        System.out.println("ROUND_DOWN:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_DOWN));
        System.out.println("ROUND_CEILING:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_CEILING));
        System.out.println("ROUND_FLOOR:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_FLOOR));
        System.out.println("ROUND_HALF_UP:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_UP));
        System.out.println("ROUND_HALF_DOWN:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_DOWN));
        System.out.println("ROUND_HALF_EVEN:" + bigDecimal.setScale(newScale, BigDecimal.ROUND_HALF_EVEN));
//        System.out.println("ROUND_UNNECESSARY:"+bigDecimal.setScale(newScale, BigDecimal.ROUND_UNNECESSARY));
    }
}
