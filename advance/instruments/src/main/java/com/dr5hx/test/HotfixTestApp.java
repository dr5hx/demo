package com.dr5hx.test;

/**
 * 测试热修复功能的应用
 */
public class HotfixTestApp {
    
    public static void main(String[] args) {
        System.out.println("=== 热修复测试应用已启动 ===");
        
        // 每5秒执行一次计算，以观察热修复效果
        while (true) {
            System.out.println("\n执行计算演示:");
            
            // 测试加法
            System.out.println("加法结果: " + Calculator.add(5, 3));
            
            // 测试减法
            System.out.println("减法结果: " + Calculator.subtract(10, 4));
            
            // 测试乘法（有bug的方法）
            System.out.println("乘法结果: " + Calculator.multiply(6, 7));
            
            System.out.println("计算完成，等待5秒...");
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
