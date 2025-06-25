package com.dr5hx.lock;

/**
 * ThreadJoinDemo
 * Desc: Demonstrates the usage of Thread.join() method
 * Date:2025/6/25 17:24
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ThreadJoinDemo implements Runnable {
    @Override
    public void run() {

        try {
            System.out.println(Thread.currentThread().getName() + " start-----");
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " end------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Thread test = new Thread(new ThreadJoinDemo());
            test.start();
            try {
                test.join(); // Wait for this thread to complete before starting the next one
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Finished~~~");
    }
}