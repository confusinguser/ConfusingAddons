package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class GuiScreenTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("handleComponentClick") || methodName.equals("func_175276_a")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            boolean found = false;
            AbstractInsnNode insn = null;
            while (iterator.hasNext()) {
                insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, Opcodes.ALOAD, 0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ALOAD, 3) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.PUTFIELD, "net/minecraft/client/gui/GuiScreen", "clickedLinkURI", "Ljava/net/URI;") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext().getNext().getNext(), Opcodes.ALOAD, 0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.GETFIELD, "net/minecraft/client/gui/GuiScreen", "mc", "Lnet/minecraft/client/Minecraft;") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.NEW, "net/minecraft/client/gui/GuiConfirmOpenLink") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.DUP) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ALOAD, 0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ALOAD, 2)) {
                    insn = insn.getNext().getNext().getNext().getNext().getNext();
                    found = true;
                    break;
                }
            }
            if (found && insn != null) {
                AbstractInsnNode temp = insn.getPrevious(); // Because next and prev become null when the insn is removed
                method.instructions.remove(insn);
                for (int i = 0; i < 10; i++) {
                    insn = temp;
                    temp = temp.getPrevious();
                    method.instructions.remove(insn);
                }
                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 2)); // clickevent
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // uri
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/GuiScreenHook", "handleComponentClick", "(Lnet/minecraft/client/gui/GuiScreen;Lnet/minecraft/event/ClickEvent;Ljava/net/URI;)V", false));
                method.instructions.insert(temp, insnList);
                return true;
            }
        }
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiScreen";
    }
}
