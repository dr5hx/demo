package com.dr5hx.fundemental;

/**
 * fundemental.Demo
 * Desc:
 * Date:2023/11/21 16:17
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class Demo {
    private final int anInt;

    public Demo(int anInt) {
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
