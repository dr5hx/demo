package com.dr5hx.agent.dynamic;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.Type;

/**
 * 动态方法适配器，用于动态Agent中插入方法监控代码
 */
public class DynamicMethodAdapter extends AdviceAdapter {
    private final String className;
    private final String methodName;
    private final String methodId;
    
    // 定义局部变量索引
    private int timeVarIndex;
    
    public DynamicMethodAdapter(MethodVisitor mv, int access, 
                             String name, String descriptor, 
                             String className) {
        super(Opcodes.ASM9, mv, access, name, descriptor);
        this.className = className;
        this.methodName = name;
        this.methodId = className.replace('/', '.') + "." + name;
    }
    
    @Override
    protected void onMethodEnter() {
        // 分配局部变量用于存储时间（long类型占用2个槽位）
        timeVarIndex = newLocal(Type.LONG_TYPE);
        
        // 打印动态拦截信息
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("[动态Agent] 进入方法: " + methodId);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        
        // 记录开始时间
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LSTORE, timeVarIndex); // 存储开始时间到局部变量
    }
    
    @Override
    protected void onMethodExit(int opcode) {
        // 计算执行时间
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(LLOAD, timeVarIndex); // 加载开始时间
        mv.visitInsn(LSUB); // 计算差值
        
        // 将执行时间存储到一个临时变量
        int durationVarIndex = newLocal(Type.LONG_TYPE);
        mv.visitVarInsn(LSTORE, durationVarIndex);
        
        // 打印执行时间
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitLdcInsn("[动态Agent] 方法执行完成: " + methodId + ", 耗时(ms): ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(LLOAD, durationVarIndex); // 加载执行时间
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}
