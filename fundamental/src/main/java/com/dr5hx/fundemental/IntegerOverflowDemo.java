package com.dr5hx.fundemental;

/**
 * fundemental.IntegerOverflowDemo
 * Desc: Demonstrates integer overflow and basic class structure
 * Date:2023/11/21 16:17
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class IntegerOverflowDemo {
    private final int anInt;

    public IntegerOverflowDemo(int anInt) {
        this.anInt = anInt;
    }

    public int getAnInt() {
        return anInt;
    }

    public static void main(String[] args) {
        long a = Integer.MAX_VALUE + 1;
        System.out.println(a);
    }
}