package com.dr5hx.agent.dynamic;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * 动态加载的Agent实现
 */
public class DynamicAgent {
    
    /**
     * 动态Agent入口点，当通过Attach API加载Agent时调用
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("======== 动态Agent已启动 ========");
        System.out.println("动态Agent参数: " + agentArgs);
        
        // 创建动态转换器
        DynamicTransformer transformer = new DynamicTransformer();
        
        try {
            // 添加转换器并设置为可重转换
            inst.addTransformer(transformer, true);
            
            // 获取所有已加载的类
            Class<?>[] loadedClasses = inst.getAllLoadedClasses();
            System.out.println("已加载类总数: " + loadedClasses.length);
            
            // 过滤需要重转换的类
            int transformedCount = 0;
            for (Class<?> clazz : loadedClasses) {
                if (shouldTransform(clazz)) {
                    try {
                        System.out.println("重转换类: " + clazz.getName());
                        inst.retransformClasses(clazz);
                        transformedCount++;
                    } catch (UnmodifiableClassException e) {
                        System.err.println("无法转换类: " + clazz.getName() + ", 原因: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("已成功重转换 " + transformedCount + " 个类");
            
        } catch (Exception e) {
            System.err.println("动态Agent执行错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 判断类是否需要转换
     */
    private static boolean shouldTransform(Class<?> clazz) {
        String className = clazz.getName();
        
        // 排除Java核心类和Agent自身类
        if (className.startsWith("java.") || 
            className.startsWith("javax.") ||
            className.startsWith("sun.") ||
            className.startsWith("com.dr5hx.agent")) {
            return false;
        }
        
        // 只处理目标包下的类
        return className.startsWith("com.dr5hx.test") || 
               className.startsWith("com.dr5hx.fundamental");
    }
}
