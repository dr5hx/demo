
package com.dr5hx.agent.dynamic;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * 动态类转换器，用于在运行时修改类字节码
 */
public class DynamicTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
//        System.out.printf("classloader : %s className : %s classBeingRedefined : %s protectionDomain : %s classfileBuffer.length %d\n",
//                loader, className, classBeingRedefined, protectionDomain, classfileBuffer.length);
        // 过滤条件：只处理目标包下的类
        if (className == null ||
                (!className.startsWith("com/dr5hx/test") &&
                        !className.startsWith("com/dr5hx/fundamental")) ||
                className.contains("agent")) {
            return null;
        }

        try {
            System.out.println("动态转换类: " + className);

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
                        return new DynamicMethodAdapter(mv, access, name,
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

    /**
     * 判断是否需要对方法进行插桩
     */
    private boolean shouldInstrument(String methodName, int access) {
        System.out.printf("methodName: %s access: %d\n", methodName, access);
        // 排除构造方法、静态初始化方法
        if ("<init>".equals(methodName) || "<clinit>".equals(methodName)) {
            return false;
        }

        // 排除抽象方法和native方法
        if ((access & Opcodes.ACC_ABSTRACT) != 0 ||
                (access & Opcodes.ACC_NATIVE) != 0) {
            return false;
        }

        return true;
    }
}
