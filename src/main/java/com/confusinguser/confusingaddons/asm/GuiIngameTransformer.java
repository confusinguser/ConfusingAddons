package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class GuiIngameTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("renderBossHealth") || methodName.equals("func_73828_d")) { // MC-4474 Boss Health Bar Temporarily Viewable after Respawn and Loading Another World
            Iterator<AbstractInsnNode> it = method.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode insn = it.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 891)) {
                    insn = insn.getNext().getNext(); // IFNULL L1
                    if (!(insn instanceof JumpInsnNode)) return false; // To be able to cast insn
                    LabelNode l1 = ((JumpInsnNode) insn).label;

                    insn = insn.getNext(); // GETSTATIC
                    method.instructions.remove(insn.getNext()); // IFLE L1
                    InsnList insnList = new InsnList();
                    insnList.add(new LdcInsnNode(100)); // These 3 lines equal to statusBarTime == 100
                    insnList.add(new InsnNode(Opcodes.ISUB));
                    insnList.add(new JumpInsnNode(Opcodes.IFEQ, l1));
                    method.instructions.insert(insn, insnList);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiIngame";
    }
}
