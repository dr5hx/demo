package com.dr5hx.agent.redefine;

import com.dr5hx.agent.asm.PerformanceAgent;
import com.dr5hx.agent.bytebuddy.ByteBuddyAgent;
import com.dr5hx.agent.dynamic.DynamicAgent;

import java.lang.instrument.Instrumentation;

/**
 * Agent选择器，用于根据参数选择不同的Agent实现
 */
public class AgentSelector {

    /**
     * 预启动入口点
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        String agentType = getAgentType(agentArgs);
        System.out.println("AgentSelector: 选择Agent类型 - " + agentType);

        switch (agentType) {
            case "bytebuddy":
                ByteBuddyAgent.premain(getAgentOptions(agentArgs), inst);
                break;
            case "hotfix":
                HotfixAgent.premain(getAgentOptions(agentArgs), inst);
                break;
            case "performance":
            default:
                PerformanceAgent.premain(getAgentOptions(agentArgs), inst);
                break;
        }
    }

    /**
     * 动态加载入口点
     */
    public static void agentmain(String agentArgs, Instrumentation inst) {
        String agentType = getAgentType(agentArgs);
        System.out.println("AgentSelector: 动态选择Agent类型 - " + agentType);

        switch (agentType) {
            case "bytebuddy":
                ByteBuddyAgent.agentmain(getAgentOptions(agentArgs), inst);
                break;
            case "hotfix":
                HotfixAgent.agentmain(getAgentOptions(agentArgs), inst);
                break;
            case "dynamic":
                DynamicAgent.agentmain(getAgentOptions(agentArgs), inst);
                break;
            case "performance":
            default:
                PerformanceAgent.agentmain(getAgentOptions(agentArgs), inst);
                break;
        }
    }

    /**
     * 从参数中提取Agent类型
     */
    private static String getAgentType(String agentArgs) {
        if (agentArgs == null || agentArgs.isEmpty()) {
            return "performance";
        }

        for (String arg : agentArgs.split(",")) {
            if (arg.startsWith("type=")) {
                return arg.substring("type=".length());
            }
        }

        return "performance";
    }

    /**
     * 从参数中提取Agent选项
     */
    private static String getAgentOptions(String agentArgs) {
        if (agentArgs == null || agentArgs.isEmpty()) {
            return "";
        }

        StringBuilder options = new StringBuilder();
        for (String arg : agentArgs.split(",")) {
            if (!arg.startsWith("type=")) {
                if (options.length() > 0) {
                    options.append(",");
                }
                options.append(arg);
            }
        }

        return options.toString();
    }
}
