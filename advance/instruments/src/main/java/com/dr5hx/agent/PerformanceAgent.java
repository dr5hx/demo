package com.dr5hx.agent;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceAgent {
    // 存储方法性能数据
    private static final ConcurrentHashMap<String, AtomicLong> methodTimes = new ConcurrentHashMap<>();
    
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new PerformanceTransformer());
        
        // 添加JVM关闭钩子，打印统计结果
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n=== 方法执行时间统计 ===");
            methodTimes.forEach((method, time) -> 
                System.out.printf("%s: %d ms\n", method, time.get()));
        }));
    }
    
    // 记录方法执行时间的工具方法（会被插入到转换后的字节码中调用）
    public static void enterMethod(String methodName) {
        // 保存方法开始时间到ThreadLocal
        threadLocalTimes.set(System.currentTimeMillis());
    }
    
    public static void exitMethod(String methodName) {
        long startTime = threadLocalTimes.get();
        long executionTime = System.currentTimeMillis() - startTime;
        // 累加执行时间
        methodTimes.computeIfAbsent(methodName, k -> new AtomicLong())
                  .addAndGet(executionTime);
    }
    
    // 使用ThreadLocal避免并发问题
    private static final ThreadLocal<Long> threadLocalTimes = new ThreadLocal<>();
    
    static class PerformanceTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, 
                              Class<?> classBeingRedefined,
                              ProtectionDomain protectionDomain, 
                              byte[] classfileBuffer) {
            
            // 只处理目标包下的类
            if (className == null || !className.startsWith("com/dr5hx")) {
                return null;
            }
            
            try {
                ClassReader reader = new ClassReader(classfileBuffer);
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
                
                // 使用ClassVisitor访问和修改类
                ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, 
                                                  String descriptor, 
                                                  String signature, 
                                                  String[] exceptions) {
                        
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, 
                                                         signature, exceptions);
                        
                        // 忽略构造方法和静态初始化方法
                        if (mv != null && !name.equals("<init>") && !name.equals("<clinit>")) {
                            String methodId = className.replace('/', '.') + "." + name;
                            return new PerformanceMethodAdapter(mv, access, name, 
                                                           descriptor, methodId);
                        }
                        return mv;
                    }
                };
                
                reader.accept(visitor, ClassReader.EXPAND_FRAMES);
                return writer.toByteArray();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    // 方法适配器，用于插入计时代码
    static class PerformanceMethodAdapter extends AdviceAdapter {
        private final String methodId;
        
        protected PerformanceMethodAdapter(MethodVisitor mv, int access, 
                                       String name, String descriptor, 
                                       String methodId) {
            super(Opcodes.ASM9, mv, access, name, descriptor);
            this.methodId = methodId;
        }
        
        @Override
        protected void onMethodEnter() {
            // 插入方法入口计时代码
            mv.visitLdcInsn(methodId);
            mv.visitMethodInsn(INVOKESTATIC, 
                           "com/dr5hx/agent/PerformanceAgent", 
                           "enterMethod", 
                           "(Ljava/lang/String;)V", 
                           false);
        }
        
        @Override
        protected void onMethodExit(int opcode) {
            // 插入方法出口计时代码
            mv.visitLdcInsn(methodId);
            mv.visitMethodInsn(INVOKESTATIC, 
                           "com/dr5hx/agent/PerformanceAgent", 
                           "exitMethod", 
                           "(Ljava/lang/String;)V", 
                           false);
        }
    }
}
