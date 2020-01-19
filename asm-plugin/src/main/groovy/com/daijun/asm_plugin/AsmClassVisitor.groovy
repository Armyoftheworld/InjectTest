package com.daijun.asm_plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author Army* @version V_1.0.0* @date 2020-01-19
 * @description
 */
class AsmClassVisitor extends ClassVisitor {
    AsmClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        def methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        println "name = $name, desc = $desc, signature = $signature"
        if ("onCreate" == name && desc == "(Landroid/os/Bundle;)V") {
            return new AsmOnCreateMethodVisitor(methodVisitor)
        }
        return methodVisitor
    }
}
