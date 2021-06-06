package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GuiConfirmOpenLinkTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("drawScreen") || methodName.equals("func_73863_a")) { // Link Preview
            AbstractInsnNode insn = method.instructions.getLast().getPrevious().getPrevious();
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insnList.add(new InsnNode(Opcodes.DUP));
            insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiConfirmOpenLink", "linkText", "Ljava/lang/String;"));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/GuiConfirmOpenLinkHook", "drawScreen", "(Lnet/minecraft/client/gui/GuiConfirmOpenLink;Ljava/lang/String;)V", false));
//            insnList.add(new InsnNode(Opcodes.RETURN));
            method.instructions.insert(insn, insnList);
            return true;
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiConfirmOpenLink";
    }
}
