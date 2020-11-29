package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GuiConfirmOpenLinkTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("<init>")) {
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/GuiConfirmOpenLinkHook", "GuiConfirmOpenLink", "(Ljava/lang/String;)Ljava/lang/String;", false));
            insnList.add(new InsnNode(Opcodes.DUP));
            LabelNode ifNull = new LabelNode(new Label());
            insnList.add(new JumpInsnNode(Opcodes.IFNULL, ifNull));
            insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/gui/GuiConfirmOpenLink", "openLinkWarning", "Ljava/lang/String;"));
            LabelNode skipIfNull = new LabelNode(new Label());
            insnList.add(new JumpInsnNode(Opcodes.GOTO, skipIfNull));
            insnList.add(ifNull);
            insnList.add(new InsnNode(Opcodes.POP2));
            insnList.add(skipIfNull);
            method.instructions.insertBefore(method.instructions.getLast().getPrevious(), insnList);
            return true;
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiConfirmOpenLink";
    }
}
