package com.dr5hx.agent.asm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * 性能数据收集器
 */
public class TimingCollector {
    // 存储每个方法的总执行时间
    private static final ConcurrentHashMap<String, LongAdder> totalTimes = new ConcurrentHashMap<>();
    
    // 存储每个方法的调用次数
    private static final ConcurrentHashMap<String, LongAdder> callCounts = new ConcurrentHashMap<>();
    
    // 使用ThreadLocal避免并发问题
    private static final ThreadLocal<Long> threadLocalStartTime = new ThreadLocal<>();
    
    /**
     * 记录方法开始执行
     */
    public static void enterMethod(String methodName) {
        threadLocalStartTime.set(System.nanoTime());
    }
    
    /**
     * 记录方法执行完成
     */
    public static void exitMethod(String methodName) {
        Long startTime = threadLocalStartTime.get();
        if (startTime != null) {
            long executionTime = System.nanoTime() - startTime;
            
            // 记录总执行时间（纳秒转毫秒）
            totalTimes.computeIfAbsent(methodName, k -> new LongAdder())
                     .add(executionTime / 1_000_000);
            
            // 记录调用次数
            callCounts.computeIfAbsent(methodName, k -> new LongAdder())
                     .increment();
            
            threadLocalStartTime.remove();
        }
    }
    
    /**
     * 打印性能统计报告
     */
    public static void printReport() {
        System.out.println("\n========== 方法执行时间统计报告 ==========");
        System.out.printf("%-50s %-15s %-15s %-15s%n", 
                         "方法名", "总执行时间(ms)", "调用次数", "平均时间(ms)");
        System.out.println("--------------------------------------------" +
                          "--------------------------------------------");
        
        totalTimes.forEach((methodName, totalTime) -> {
            long calls = callCounts.get(methodName).sum();
            long total = totalTime.sum();
            double average = calls > 0 ? (double) total / calls : 0;
            
            System.out.printf("%-50s %-15d %-15d %-15.2f%n", 
                             methodName, total, calls, average);
        });
        
        System.out.println("===========================================");
    }
    
    /**
     * 清空统计数据
     */
    public static void reset() {
        totalTimes.clear();
        callCounts.clear();
    }
}
