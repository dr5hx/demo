package com.dr5hx.agent.asm;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Set;

/**
 * 性能监控字节码转换器
 */
public class PerformanceTransformer implements ClassFileTransformer {
    private final Set<String> includePackages;
    private final Set<String> excludePackages;
    
    public PerformanceTransformer() {
        this.includePackages = null;
        this.excludePackages = null;
    }
    
    public PerformanceTransformer(Set<String> includePackages, Set<String> excludePackages) {
        this.includePackages = includePackages;
        this.excludePackages = excludePackages;
    }
    
    @Override
    public byte[] transform(ClassLoader loader, String className, 
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, 
                          byte[] classfileBuffer) {
        
        // 过滤条件：只处理目标包下的类
        if (!shouldTransform(className)) {
            return null;
        }
        
        try {
            System.out.println("正在转换类: " + className);
            
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
            
            // 创建类访问器
            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
                @Override
                public MethodVisitor visitMethod(int access, String name, 
                                               String descriptor, 
                                               String signature, 
                                               String[] exceptions) {
                    
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, 
                                                        signature, exceptions);
                    
                    // 过滤不需要监控的方法
                    if (mv != null && shouldInstrument(name, access)) {
                        return new PerformanceMethodAdapter(mv, access, name, 
                                                          descriptor, className);
                    }
                    
                    return mv;
                }
            };
            
            reader.accept(visitor, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
            
        } catch (Exception e) {
            System.err.println("转换类时出错: " + className);
            e.printStackTrace();
            return null;
        }
    }


    private boolean shouldTransform(String className) {
        if (className == null) {
            return false;
        }

        // 转换为内部类名格式
        String internalClassName = className.replace('.', '/');

        // 排除Agent自身的类
        if (internalClassName.contains("com/dr5hx/agent")) {
            return false;
        }

        // 如果有包含配置，只检查包含列表
        if (hasIncludePackages()) {
            return matchesAnyPackage(internalClassName, includePackages);
        }

        // 如果有排除配置，检查是否被排除
        if (hasExcludePackages() && matchesAnyPackage(internalClassName, excludePackages)) {
            return false;
        }

        // 默认只处理 com/dr5hx 包下的类（排除agent包）
        return internalClassName.startsWith("com/dr5hx");
    }

    private boolean hasIncludePackages() {
        return includePackages != null && !includePackages.isEmpty();
    }

    private boolean hasExcludePackages() {
        return excludePackages != null && !excludePackages.isEmpty();
    }

    private boolean matchesAnyPackage(String className, Set<String> packages) {
        boolean b = false;
        for (String pkg : packages) {
            String replace = pkg.replace('.', '/');
            if (className.startsWith(replace)) {
                b = true;
                break;
            }
        }
        return b;
    }
    
    /**
     * 判断是否需要对方法进行插桩
     */
    private boolean shouldInstrument(String methodName, int access) {
        // 排除构造方法、静态初始化方法
        if ("<init>".equals(methodName) || "<clinit>".equals(methodName)) {
            return false;
        }
        
        // 排除抽象方法和native方法
        if ((access & Opcodes.ACC_ABSTRACT) != 0 || 
            (access & Opcodes.ACC_NATIVE) != 0) {
            return false;
        }
        
        // 可以添加更多过滤条件，如方法名模式匹配等
        return true;
    }
}
