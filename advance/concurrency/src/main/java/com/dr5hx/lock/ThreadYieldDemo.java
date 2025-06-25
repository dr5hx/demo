package com.dr5hx.lock;

/**
 * ThreadYieldDemo
 * Desc: Demonstrates the usage of Thread.yield() method for cooperative scheduling
 * Date:2025/6/25 17:13
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ThreadYieldDemo implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            Thread.yield(); // Suggests that the current thread is willing to yield its current use of the CPU
        }
    }

    public static void main(String[] args) {
        ThreadYieldDemo runnable = new ThreadYieldDemo();
        Thread t1 = new Thread(runnable, "FirstThread");
        Thread t2 = new Thread(runnable, "SecondThread");

        t1.start();
        t2.start();
    }
}