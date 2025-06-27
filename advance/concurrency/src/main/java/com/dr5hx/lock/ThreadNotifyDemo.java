package com.dr5hx.lock;

/**
 * ThreadNotifyDemo
 * Desc: Demonstrates the usage of wait(), notify(), and notifyAll() methods for thread synchronization
 * Date:2025/6/25 17:01
 * Author:zhouchang
 * Email:zhouchang@asiainfo.com
 */
public class ThreadNotifyDemo {
    public synchronized void testWait() {
        System.out.println(Thread.currentThread().getName() + " Start-----");
        try {
            wait(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " End-------");
    }

    public static void main(String[] args) throws InterruptedException {
        final ThreadNotifyDemo test = new ThreadNotifyDemo();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    test.testWait();
                }
            }).start();
        }
       
        synchronized (test) {
            System.out.println("notify one thread");
            test.notify(); // Wake up one waiting thread
        }
        Thread.sleep(3000);
        System.out.println("-----------分割线-------------");
// If the code below is not executed, the JVM will not terminate because remaining threads are still waiting for notification
//        synchronized (test) {
//            test.notifyAll(); // Wake up all waiting threads
//        }
    }
}