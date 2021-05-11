package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class GuiNewChatTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) { // TODO Extensive testing
        if (methodName.equals("printChatMessageWithOptionalDeletion") || methodName.equals("func_146234_a")) { // Fix chat formatting from some mods disappearing on new line
            AbstractInsnNode insn = method.instructions.getFirst().getNext();
            InsnList insnList = new InsnList();
            insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/GuiNewChatHook",
                    "printChatMessageWithOptionalDeletion", "(Lnet/minecraft/util/IChatComponent;)Lnet/minecraft/util/IChatComponent;", false));
            method.instructions.insertBefore(insn, insnList);
            return true;
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiNewChatTransformer";
    }
}
