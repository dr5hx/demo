package com.dr5hx.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class BasicAgent {
    // JVM启动时调用此方法
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("BasicAgent已启动，参数: " + agentArgs);
        
        // 注册类转换器
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className != null && className.startsWith("com/dr5hx/app")) {
                    System.out.println("加载类: " + className);
                }
                return null; // 返回null表示不修改字节码
            }
        }, false);
    }
}
