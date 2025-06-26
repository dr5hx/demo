
package com.dr5hx.agent.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * ByteBuddy方法拦截器
 * 用于记录方法执行信息
 */
public class MethodInterceptor {
    
    // 方法调用计数
    private static final ConcurrentHashMap<String, LongAdder> INVOCATION_COUNT = 
        new ConcurrentHashMap<>();
    
    // 方法执行时间（毫秒）
    private static final ConcurrentHashMap<String, LongAdder> EXECUTION_TIME = 
        new ConcurrentHashMap<>();
    
    /**
     * 拦截方法并记录执行信息
     * 
     * @param method 被拦截的方法
     * @param args 方法参数
     * @param callable 原始方法调用
     * @param instance 方法所属实例
     * @return 原始方法的返回值
     * @throws Exception 如果调用原始方法发生异常
     */
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                  @AllArguments Object[] args,
                                  @SuperCall Callable<?> callable,
                                  @This(optional = true) Object instance) throws Exception {
        
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String methodId = className + "." + methodName;
        
        // 记录调用次数
        INVOCATION_COUNT.computeIfAbsent(methodId, k -> new LongAdder())
                       .increment();
        
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            // 调用原始方法
            return callable.call();
        } finally {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 累加执行时间
            EXECUTION_TIME.computeIfAbsent(methodId, k -> new LongAdder())
                         .add(executionTime);
            
            // 打印详细日志
            System.out.printf("[ByteBuddy] 方法: %s, 执行时间: %dms%n", 
                             methodId, executionTime);
        }
    }
    
    /**
     * 打印方法执行统计信息
     */
    public static void printStatistics() {
        System.out.println("\n========== ByteBuddy方法执行统计 ==========");
        System.out.printf("%-50s %-15s %-15s %-15s%n", 
                         "方法", "调用次数", "总执行时间(ms)", "平均时间(ms)");
        for (int i = 0; i < 100; i++) {
            System.out.print("-");
        }
        
        INVOCATION_COUNT.forEach((methodId, count) -> {
            long calls = count.sum();
            long totalTime = EXECUTION_TIME.getOrDefault(methodId, new LongAdder()).sum();
            double avgTime = calls > 0 ? (double) totalTime / calls : 0;
            
            System.out.printf("%-50s %-15d %-15d %-15.2f%n", 
                             methodId, calls, totalTime, avgTime);
        });
        
        System.out.println("=========================================");
    }
}
