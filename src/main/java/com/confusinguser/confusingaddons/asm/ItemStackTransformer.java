package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ItemStackTransformer implements ITransformer {

    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("getTooltip") || methodName.equals("func_82840_a")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (insn.getOpcode() == Opcodes.ARETURN) {
                    InsnList insnList = new InsnList();
                    insnList.add(new InsnNode(Opcodes.POP));
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // The list with the tooltip (List<String>)
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/ItemStackHook",
                            "getToolTip", "(Lnet/minecraft/item/ItemStack;Ljava/util/List;)Ljava/util/List;", false));
                    insnList.add(new InsnNode(Opcodes.ARETURN));
                    method.instructions.insertBefore(insn, insnList);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.item.ItemStack";
    }
}