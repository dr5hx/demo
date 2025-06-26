package com.dr5hx.test;

/**
 * 测试应用，用于验证性能监控功能
 */
public class TestApplication {

    public static void main(String[] args) {
        System.out.println("测试应用已启动");

        TestApplication app = new TestApplication();

        // 测试不同的方法
        for (; ; ) {
            app.fastMethod();
            app.slowMethod();
            app.recursiveMethod(1000);
        }

//        System.out.println("测试应用执行完成");
    }

    public void fastMethod() {
        // 快速方法
        int sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += i;
        }
        System.out.println("快速方法执行完成，结果: " + sum);
    }

    public void slowMethod() {
        // 慢速方法
        try {
            Thread.sleep(100); // 模拟耗时操作
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("慢速方法执行完成");
    }

    public int recursiveMethod(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * recursiveMethod(n - 1);
    }
}
