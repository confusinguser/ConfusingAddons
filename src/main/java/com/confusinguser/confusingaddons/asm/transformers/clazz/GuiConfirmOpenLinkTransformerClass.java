package com.confusinguser.confusingaddons.asm.transformers.clazz;

import com.confusinguser.confusingaddons.asm.core.ITransformerClass;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class GuiConfirmOpenLinkTransformerClass implements ITransformerClass {
    @Override
    public boolean transformClass(ClassNode clazz) {
        MethodVisitor mv = clazz.visitMethod(Opcodes.ACC_PUBLIC, "onGuiClosed", "()V", null, null);
        mv.visitCode();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/GuiConfirmOpenLinkHook", "onGuiClosed", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        return true;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiConfirmOpenLink";
    }
}
