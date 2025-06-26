package com.dr5hx.agent.asm;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 性能监控Agent主类
 */
public class PerformanceAgent {
    private static Set<String> includePackages = new HashSet<>();
    private static Set<String> excludePackages = new HashSet<>();
    private static boolean enableReport = true;
    
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("性能监控Agent已启动");
        System.out.println("Agent参数: " + agentArgs);
        
        // 解析Agent参数
        parseAgentArgs(agentArgs);
        
        // 注册字节码转换器，传递配置
        inst.addTransformer(new PerformanceTransformer(includePackages, excludePackages));
        
        if (enableReport) {
            Runtime.getRuntime().addShutdownHook(new Thread(TimingCollector::printReport));
        }
        
        System.out.println("性能监控转换器已注册");
    }
    
    /**
     * 动态Agent入口（支持运行时附加）
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("动态性能监控Agent已启动");
        premain(agentArgs, inst);
    }
    
    private static void parseAgentArgs(String agentArgs) {
        if (agentArgs == null || agentArgs.trim().isEmpty()) {
            // 默认配置
            includePackages.add("com.dr5hx");
            return;
        }
        
        // 解析参数格式: include=com.dr5hx.test,exclude=com.dr5hx.agent,report=true
        String[] args = agentArgs.split(",");
        for (String arg : args) {
            String[] keyValue = arg.split("=");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                
                switch (key) {
                    case "include":
                        includePackages.addAll(Arrays.asList(value.split(":")));
                        break;
                    case "exclude":
                        excludePackages.addAll(Arrays.asList(value.split(":")));
                        break;
                    case "report":
                        enableReport = Boolean.parseBoolean(value);
                        break;
                }
            }
        }
        
        System.out.println("包含包: " + includePackages);
        System.out.println("排除包: " + excludePackages);
        System.out.println("启用报告: " + enableReport);
    }
}
