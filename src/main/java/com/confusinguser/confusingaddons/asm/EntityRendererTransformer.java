package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class EntityRendererTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("hurtCameraEffect") || methodName.equals("func_78482_e")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (insn instanceof FieldInsnNode && insn.getOpcode() == Opcodes.GETFIELD &&
                        ((FieldInsnNode) insn).owner.equals("net/minecraft/entity/EntityLivingBase") &&
                        ((FieldInsnNode) insn).name.equals("attackedAtYaw")) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 2));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/EntityRendererHook",
                            "hurtCameraEffect", "(Lnet/minecraft/entity/EntityLivingBase;)V", false));
                    method.instructions.insertBefore(insn, insnList);
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
