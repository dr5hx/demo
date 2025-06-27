package com.dr5hx.agent.basic;

import java.lang.instrument.Instrumentation;

public class BasicAgent {
    // JVM启动时调用此方法
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("BasicAgent已启动，参数: " + agentArgs);
        
        // 注册类转换器
        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            System.out.println("加载类: " + className);
//                if (className != null) {
//                    System.out.println("加载类: " + className);
//                    System.out.flush(); // 强制刷新输出
//                }
            return null; // 返回null表示不修改字节码
        }, false);
    }
}
