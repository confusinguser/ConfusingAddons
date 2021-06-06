package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RenderPlayerTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("renderRightArm") || methodName.equals("func_177138_b")) { // MC-1349 While riding a pig, horse or minecart and using F5, the hand of your character is misplaced
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 170)) {
                    insn = insn.getPrevious();
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 3));
                    insnList.add(new InsnNode(Opcodes.ICONST_0));
                    insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/client/model/ModelPlayer", "isRiding", "Z"));
                    method.instructions.insertBefore(insn, insnList);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.RenderPlayer";
    }
}
