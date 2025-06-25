package com.dr5hx.lock;

/**
 * ThreadSleepDemo
 * Desc: Demonstrates and compares the behavior of Thread.sleep() and Object.wait() methods
 * Date:2025/6/25 17:10
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ThreadSleepDemo {
    public synchronized void sleepMethod() {
        System.out.println("Sleep start-----");
        try {
            Thread.sleep(1000); // Thread.sleep() doesn't release the lock
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sleep end-----");
    }

    public synchronized void waitMethod() {
        System.out.println("Wait start-----");
        synchronized (this) {
            try {
                wait(1000); // Object.wait() releases the lock until notified or timeout
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Wait end-----");
    }

    public static void main(String[] args) {
        final ThreadSleepDemo test1 = new ThreadSleepDemo();

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test1.sleepMethod();
                }
            }).start();
        }

        try {
            Thread.sleep(10000); // Pause for ten seconds to let the above threads complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----分割线-----");

        final ThreadSleepDemo test2 = new ThreadSleepDemo();

        for (int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test2.waitMethod();
                }
            }).start();
        }
    }
}