package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class GuiSelectWorldTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("func_146615_e")) { // No dev-friendly name for this one
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, Opcodes.GETFIELD, "net/minecraft/client/gui/GuiSelectWorld", "mc", "Lnet/minecraft/client/Minecraft;") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ACONST_NULL) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.CHECKCAST, "net/minecraft/client/gui/GuiScreen") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "displayGuiScreen", "(Lnet/minecraft/client/gui/GuiScreen;)V", false)) {
                    AbstractInsnNode temp = insn.getPrevious(); // Because next and prev become null when the insn is removed
                    method.instructions.remove(insn);
                    for (int i = 0; i < 3; i++) {
                        insn = temp;
                        temp = temp.getPrevious();
                        if (temp.getOpcode() == -1) {
                            temp = temp.getPrevious();
                            if (temp.getOpcode() == -1) temp = temp.getPrevious();
                        }
                        method.instructions.remove(insn);
                    }

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiSelectWorld";
    }
}
