package com.dr5hx.agent.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * 方法访问适配器，用于插入性能监控代码
 */
public class PerformanceMethodAdapter extends AdviceAdapter {
    private final String className;
    private final String methodName;
    private final String methodId;
    
    public PerformanceMethodAdapter(MethodVisitor mv, int access, 
                                  String name, String descriptor, 
                                  String className) {
        super(Opcodes.ASM9, mv, access, name, descriptor);
        this.className = className;
        this.methodName = name;
        this.methodId = className.replace('/', '.') + "." + name;
    }
    
    @Override
    protected void onMethodEnter() {
        // 插入方法入口计时代码
        // 相当于调用: TimingCollector.enterMethod(methodId);
        
        // 加载方法ID常量到栈
        mv.visitLdcInsn(methodId);
        
        // 调用静态方法 TimingCollector.enterMethod
        mv.visitMethodInsn(INVOKESTATIC, 
                          "com/dr5hx/agent/asm/TimingCollector",
                          "enterMethod", 
                          "(Ljava/lang/String;)V", 
                          false);
    }
    
    @Override
    protected void onMethodExit(int opcode) {
        // 插入方法出口计时代码
        // 相当于调用: TimingCollector.exitMethod(methodId);
        
        // 加载方法ID常量到栈
        mv.visitLdcInsn(methodId);
        
        // 调用静态方法 TimingCollector.exitMethod
        mv.visitMethodInsn(INVOKESTATIC, 
                          "com/dr5hx/agent/asm/TimingCollector",
                          "exitMethod", 
                          "(Ljava/lang/String;)V", 
                          false);
    }
}
