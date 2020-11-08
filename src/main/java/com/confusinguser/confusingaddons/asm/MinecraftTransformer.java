package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class MinecraftTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("runGameLoop") || methodName.equals("func_71411_J")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", "isEntityInsideOpaqueBlock", "()Z") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.IFEQ) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext().getNext().getNext() /* 2x opcode -1 for some reason */, Opcodes.ALOAD, 0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "gameSettings", "Lnet/minecraft/client/settings/GameSettings;") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ICONST_0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.PUTFIELD, "net/minecraft/client/settings/GameSettings", "thirdPersonView", "I")) {
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
        return "net.minecraft.client.Minecraft";
    }
}
