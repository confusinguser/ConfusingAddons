package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.Opcodes;

import java.util.Iterator;

public class EntityRendererTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("hurtCameraEffect") || methodName.equals("func_78482_e")) { // MC-26678 Damage wobble no longer shows direction of incoming damage
            ASMUtils.deleteLines(method.instructions, 579, 580, 581, 582);
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 579)) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    insnList.add(new VarInsnNode(Opcodes.FLOAD, 3));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/EntityRendererHook",
                            "hurtCameraEffect", "(Lnet/minecraft/entity/EntityLivingBase;F)V", false));
                    method.instructions.insert(insn, insnList);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.EntityRenderer";
    }
}
