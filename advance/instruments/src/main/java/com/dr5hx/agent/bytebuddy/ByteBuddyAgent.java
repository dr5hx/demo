package com.dr5hx.agent.bytebuddy;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

/**
 * 使用ByteBuddy实现的Agent
 */
public class ByteBuddyAgent {
    
    /**
     * 预启动入口点 - JVM启动时加载
     */
    public static void premain(String arguments, Instrumentation instrumentation) {
        System.out.println("======== ByteBuddy Agent已启动 ========");
        installAgent(arguments, instrumentation);
    }
    
    /**
     * 动态加载入口点 - 运行时加载
     */
    public static void agentmain(String arguments, Instrumentation instrumentation) {
        System.out.println("======== ByteBuddy Agent已动态加载 ========");
        installAgent(arguments, instrumentation);
    }
    
    /**
     * 安装Agent
     */
    private static void installAgent(String arguments, Instrumentation instrumentation) {
        System.out.println("ByteBuddy Agent参数: " + arguments);

        // 解析包含的包名
        String targetPackage = "com.dr5hx";
        if (arguments != null && !arguments.isEmpty()) {
            // 简单解析，只取第一个包名
            for (String arg : arguments.split(",")) {
                if (arg.startsWith("include=")) {
                    targetPackage = arg.substring("include=".length()).split(":")[0];
                    break;
                }
            }
        }

        System.out.println("监控包: " + targetPackage);

        // 设置关闭钩子，打印统计信息
        Runtime.getRuntime().addShutdownHook(new Thread(MethodInterceptor::printStatistics));

        // 使用ByteBuddy构建转换器
        new AgentBuilder.Default()
                // 指定要拦截的类，同时排除Agent自身的类
                .type(ElementMatchers.nameStartsWith(targetPackage)
                        .and(ElementMatchers.not(ElementMatchers.nameStartsWith("com.dr5hx.agent"))))
                // 指定转换
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.any()) // 拦截所有方法
                                .intercept(MethodDelegation.to(MethodInterceptor.class)) // 委托给拦截器
                )
                // 安装到Instrumentation
                .installOn(instrumentation);

        System.out.println("ByteBuddy转换器已安装");
    }

}
