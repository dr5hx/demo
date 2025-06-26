
package com.dr5hx.test;

/**
 * 计算器类 - 用于演示热修复功能
 */
public class Calculator {

    /**
     * 加法运算
     */
    public static int add(int a, int b) {
        System.out.println("执行加法运算: " + a + " + " + b);
        return a + b;
    }

    /**
     * 减法运算
     */
    public static int subtract(int a, int b) {
        System.out.println("执行减法运算: " + a + " - " + b);
        return a - b;
    }

    /**
     * 乘法运算
     */
    public static int multiply(int a, int b) {
        System.out.println("执行乘法运算: " + a + " * " + b);
        // 假设这里有一个bug：错误的实现为加法
        return a * b;  // 错误实现，应该是 a * b
    }
}
