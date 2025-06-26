package com.dr5hx.agent.dynamic;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * 动态Attach工具，用于在运行时附加Agent到JVM
 */
public class AgentAttacher {
    
    public static void main(String[] args) {
        try {
            // 列出所有运行中的JVM进程
            System.out.println("====== 正在查找运行中的Java进程 ======");
            List<VirtualMachineDescriptor> vms = VirtualMachine.list();
            
            if (vms.isEmpty()) {
                System.out.println("未找到运行中的Java进程");
                return;
            }
            
            System.out.println("找到以下Java进程:");
            for (int i = 0; i < vms.size(); i++) {
                VirtualMachineDescriptor vm = vms.get(i);
                System.out.printf("%d. PID: %s - %s%n", i + 1, vm.id(), vm.displayName());
            }
            
            // 确定目标进程
            String targetPid = determineTargetPid(args, vms);
            if (targetPid == null) {
                return;
            }
            
            // 获取Agent JAR路径
            String agentPath = getAgentJarPath();
            System.out.println("使用Agent JAR: " + agentPath);
            
            // 附加到目标进程
            System.out.println("正在附加到进程 " + targetPid + "...");
            VirtualMachine vm = VirtualMachine.attach(targetPid);
            
            // 加载Agent
            System.out.println("正在加载Agent...");
            vm.loadAgent(agentPath, "include=com.dr5hx.test,verbose=true");
            
            // 分离
            vm.detach();
            System.out.println("Agent已成功加载，已与目标JVM分离");
            
        } catch (Exception e) {
            System.err.println("附加Agent时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 确定目标进程ID
     */
    private static String determineTargetPid(String[] args, List<VirtualMachineDescriptor> vms) {
        // 如果命令行参数提供了PID，直接使用
        if (args.length > 0) {
            return args[0];
        }
        
        // 否则提示用户选择
        System.out.print("请输入要附加的进程序号 (1-" + vms.size() + "): ");
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            if (choice < 1 || choice > vms.size()) {
                System.out.println("无效的选择");
                return null;
            }
            return vms.get(choice - 1).id();
        } catch (Exception e) {
            System.out.println("输入错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取Agent JAR路径
     */
    private static String getAgentJarPath() {
        try {
            // 首先尝试从target目录获取
            File jarFile = new File("target/dynamic.jar");
            if (jarFile.exists()) {
                return jarFile.getAbsolutePath();
            }
            
            // 如果不存在，尝试从当前目录获取
            jarFile = new File("dynamic.jar");
            if (jarFile.exists()) {
                return jarFile.getAbsolutePath();
            }
            
            // 如果都不存在，使用完整路径
            return new File("advance/instruments/target/dynamic.jar").getAbsolutePath();
            
        } catch (Exception e) {
            System.err.println("获取Agent路径时出错: " + e.getMessage());
            e.printStackTrace();
            // 返回默认路径
            return "target/dynamic.jar";
        }
    }
}
