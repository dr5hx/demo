package com.dr5hx.fundamental;

/**
 * fundamental.ShutdownHookDemo
 * Desc: Demonstrates the use of shutdown hooks in Java
 * Date:2024/2/22 16:28
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ShutdownHookDemo {
    public static void main(String[] args) {
        try {
            System.out.println("1111");
            System.out.println("1111");
            System.out.println("1111");
            System.out.println("1111");
            Thread hook = new Thread(() -> {
                System.out.println("333333");
            });
            Runtime.getRuntime().addShutdownHook(hook);
            while (true) {

            }

        } finally {
            System.out.println("222222222222");
        }

    }
}