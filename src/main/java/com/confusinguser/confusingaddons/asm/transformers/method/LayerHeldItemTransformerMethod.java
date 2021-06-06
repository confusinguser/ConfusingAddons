package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class LayerHeldItemTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("doRenderLayer") || methodName.equals("func_177141_a")) { // MC-30481 Enchanted fishing rod doesn't glow when fishing
            ASMUtils.deleteLines(method.instructions, 49);
            Iterator<AbstractInsnNode> it = method.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode insn = it.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 49)) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 9));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "copy", "()Lnet/minecraft/item/ItemStack;", false));
                    insnList.add(new InsnNode(Opcodes.DUP));
                    insnList.add(new InsnNode(Opcodes.ICONST_0));
                    insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/item/ItemStack", "stackSize", "I"));
                    insnList.add(new VarInsnNode(Opcodes.ASTORE, 9));
                    method.instructions.insert(insn, insnList);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.renderer.entity.layers.LayerHeldItem";
    }
}
