package com.dr5hx.fundamental;

import java.util.ArrayList;

/**
 * fundamental.JavaFundamentalsDemo
 * Desc: Demonstrates integer overflow, collections, and thread synchronization
 * Date:2023/11/21 16:17
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class JavaFundamentalsDemo {
    private final int anInt;
    private final static Object LOCK = new Object();

    public JavaFundamentalsDemo(int anInt) {
        this.anInt = anInt;
    }

    public int getAnInt() {
        return anInt;
    }

    public static void main(String[] args) throws InterruptedException {
        JavaFundamentalsDemo javaFundamentalsDemo = new JavaFundamentalsDemo(1);
        System.out.println(javaFundamentalsDemo.getAnInt());
        long a = Integer.MAX_VALUE + 1;
        ArrayList<Object> objects = new ArrayList<>();
        System.out.println(objects.size());
        System.out.println(a);
        javaFundamentalsDemo.testLock();

    }

    private void testLock() throws InterruptedException {
        new Thread(() -> {
            while (true) {
                synchronized (LOCK) {
                    System.out.println("2");
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
        }).start();
        Thread.sleep(10000);
        while (true) {
            synchronized (LOCK) {
                LOCK.notify();
                System.out.println("1");
//                Thread.sleep(5000);
                return;
            }

        }
    }
}