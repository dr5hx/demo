package com.dr5hx.agent.redefine;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 热修复Agent，用于动态更新已加载类的实现
 */
public class HotfixAgent {
    
    // 已处理文件的时间戳缓存
    private static final Map<String, Long> processedFiles = new HashMap<>();
    
    /**
     * Agent入口点 - JVM启动时
     */
    public static void premain(String args, Instrumentation inst) {
        System.out.println("======== 热修复Agent已启动 ========");
        setupHotfixMonitor(args, inst);
    }
    
    /**
     * Agent入口点 - 动态加载时
     */
    public static void agentmain(String args, Instrumentation inst) {
        System.out.println("======== 热修复Agent已动态加载 ========");
        setupHotfixMonitor(args, inst);
    }
    
    /**
     * 设置热修复监控器
     */
    private static void setupHotfixMonitor(String args, Instrumentation inst) {
        System.out.println("热修复Agent参数: " + args);
        
        // 从参数中获取热修复目录
        String hotfixDir = "hotfix";
        if (args != null && !args.isEmpty()) {
            for (String arg : args.split(",")) {
                if (arg.startsWith("dir=")) {
                    hotfixDir = arg.substring("dir=".length());
                    break;
                }
            }
        }
        
        // 创建热修复目录
        Path dirPath = Paths.get(hotfixDir);
        try {
            String canonicalPath = dirPath.normalize().toAbsolutePath().toString();
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("创建热修复目录: " + canonicalPath);
            } else {
                System.out.println("使用热修复目录: " + canonicalPath);
            }
        } catch (IOException e) {
            System.err.println("创建热修复目录失败: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // 启动定时检查
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkForHotfixes(dirPath.toFile(), inst);
                } catch (Exception e) {
                    System.err.println("检查热修复时出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, 1000, 2000); // 每2秒检查一次
        String canonicalPath = dirPath.normalize().toAbsolutePath().toString();

        System.out.println("热修复监视器已启动，监视目录: " + canonicalPath);
        System.out.println("将编译后的.class文件放入此目录以热修复");
    }
    
    /**
     * 检查并应用热修复
     */
    private static void checkForHotfixes(File dir, Instrumentation inst) {
        File[] files = dir.listFiles((d, name) -> name.endsWith(".class"));
        if (files == null || files.length == 0) {
            return;
        }
        
        for (File file : files) {
            long lastModified = file.lastModified();
            Long lastProcessed = processedFiles.get(file.getName());
            
            // 跳过已处理的文件（除非被修改）
            if (lastProcessed != null && lastProcessed >= lastModified) {
                continue;
            }
            
            try {
                // 读取类文件字节码
                byte[] newClassBytes = Files.readAllBytes(file.toPath());
                
                // 从文件名获取类名
                String className = getClassNameFromFile(file);
                
                // 查找已加载的类
                Class<?> targetClass = findLoadedClass(className, inst);
                if (targetClass != null) {
                    try {
                        // 重定义类
                        ClassDefinition def = new ClassDefinition(targetClass, newClassBytes);
                        inst.redefineClasses(def);
                        
                        System.out.println("成功热修复类: " + targetClass.getName());
                        processedFiles.put(file.getName(), lastModified);
                        
                        // 成功后移动或重命名文件
                        Files.move(file.toPath(), 
                                  file.toPath().resolveSibling(file.getName() + ".applied"));
                    } catch (Exception e) {
                        System.err.println("重定义类时出错: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("未找到已加载的类: " + className);
                }
            } catch (IOException e) {
                System.err.println("读取类文件时出错: " + file.getName());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 从文件名获取类名
     */
    private static String getClassNameFromFile(File file) {
        String fileName = file.getName();
        String baseName = fileName.substring(0, fileName.length() - 6); // 去掉.class
        
        // 尝试智能解析，处理分隔符
        // 先检查是否含有包名标记（$表示内部类）
        if (baseName.contains("$")) {
            return baseName.replace('$', '.');
        }
        
        // 如果文件名中包含下划线，可能是包路径的分隔符
        if (baseName.contains("_")) {
            return baseName.replace('_', '.');
        }
        
        // 默认返回，假设是顶级类
        return baseName;
    }
    
    /**
     * 查找已加载的类
     */
    private static Class<?> findLoadedClass(String className, Instrumentation inst) {
        Class<?>[] loadedClasses = inst.getAllLoadedClasses();
        
        // 完全匹配
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getName().equals(className)) {
                return clazz;
            }
        }
        
        // 部分匹配（简单类名）
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getSimpleName().equals(className)) {
                return clazz;
            }
        }
        
        // 尝试匹配类名结尾
        for (Class<?> clazz : loadedClasses) {
            if (clazz.getName().endsWith("." + className)) {
                return clazz;
            }
        }
        
        return null;
    }
}
