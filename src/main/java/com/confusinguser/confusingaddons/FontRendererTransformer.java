/*package com.confusinguser.confusingaddons;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class FontRendererTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("renderDefaultChar") || methodName.equals("func_78266_a")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 234)) {
                    insn = insn.getPrevious().getPrevious().getPrevious();
                    InsnList insnList = new InsnList();
                    method.instructions.insertBefore(insn, insnList);

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return null;
    }
}
*/