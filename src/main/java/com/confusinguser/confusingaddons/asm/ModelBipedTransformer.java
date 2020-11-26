package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class ModelBipedTransformer implements ITransformer {
    private int methodsFound;

    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        boolean found = false;
        if (methodName.equals("setRotationAngles")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            iterator.next();
            AbstractInsnNode insn = null;
            while (iterator.hasNext()) {
                insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 133)) {
                    insn = insn.getPrevious();
                    found = true;
                    methodsFound++;
                    break;
                }
            }
            if (found) {
                AbstractInsnNode temp = insn.getPrevious(); // Because next and prev become null when the insn is removed
                for (int i = 0; i < 33; i++) {
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
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.ModelBiped";
    }
}
