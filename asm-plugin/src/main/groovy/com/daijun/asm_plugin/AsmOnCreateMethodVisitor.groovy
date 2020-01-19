package com.daijun.asm_plugin

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author Army* @version V_1.0.0* @date 2020-01-19
 * @description
 */
class AsmOnCreateMethodVisitor extends MethodVisitor {
    AsmOnCreateMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv)
    }

    @Override
    void visitCode() {
        // 方法的第一行插入代码
        super.visitCode()
    }

    /**
     * L8
     *     LINENUMBER 23 L8
     *     ALOAD 0
     *     GETFIELD com/daijun/inject/MainActivity.text : Landroid/widget/TextView;
     *     DUP
     *     IFNONNULL L9
     *     LDC "text"
     *     INVOKESTATIC kotlin/jvm/internal/Intrinsics.throwUninitializedPropertyAccessException (Ljava/lang/String;)V
     *    L9
     *     INVOKEVIRTUAL android/widget/TextView.getText ()Ljava/lang/CharSequence;
     *     DUP
     *     LDC "text.text"
     *     INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkExpressionValueIsNotNull (Ljava/lang/Object;Ljava/lang/String;)V
     *     INVOKESTATIC com/daijun/test/PrintUtilsKt.simplePrint (Ljava/lang/CharSequence;)V
     *    L10
     * @param opcode
     */

    @Override
    void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            // 方法的最后一行插入代码
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(Opcodes.GETFIELD, "com/daijun/inject/MainActivity", "text",
                    "Landroid/widget/TextView;")
            mv.visitInsn(Opcodes.DUP)
            def label9 = new Label()
            mv.visitJumpInsn(Opcodes.IFNONNULL, label9)
            def label10 = new Label()
            mv.visitLabel(label10)
            mv.visitLdcInsn("text")
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "kotlin/jvm/internal/Intrinsics",
                    "throwUninitializedPropertyAccessException", "(Ljava/lang/String;)V", false)
            mv.visitLabel(label9)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/TextView", "getText",
                    "()Ljava/lang/CharSequence;", false)
            mv.visitInsn(Opcodes.DUP)
            mv.visitLdcInsn("text.text")
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "kotlin/jvm/internal/Intrinsics",
                    "checkExpressionValueIsNotNull", "(Ljava/lang/Object;Ljava/lang/String;)V", false)
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    "com/daijun/test/PrintUtilsKt",
                    "simplePrint",
                    "(Ljava/lang/CharSequence;)V",
                    false)
        }
        super.visitInsn(opcode)
    }
}
