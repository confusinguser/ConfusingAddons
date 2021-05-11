package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class MinecraftTransformer implements ITransformer {
    private int methodsFound = 0;

    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        if (methodName.equals("runGameLoop") || methodName.equals("func_71411_J")) { // MC-128 3rd person view reverts to 1st person view if head inside a block (corner a one block wide tunnel in a minecart, ride on a horse through a block, piston...).
            ASMUtils.deleteLines(method.instructions, 1116, 1118);
            methodsFound++;
        } else if (methodName.equals("launchIntegratedServer") || methodName.equals("func_71371_a")) { // MC-185 Creating or loading a singleplayer world shows the main menu for a brief second.
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 2335)) {
                    insn = insn.getNext().getNext().getNext().getNext();
                    methodsFound++;
                    insn = ASMUtils.removeInstructionsBefore(method.instructions, insn, 3, false);

                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/client/gui/GuiScreenWorking"));
                    insnList.add(new InsnNode(Opcodes.DUP));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/gui/GuiScreenWorking", "<init>", "()V", false));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "displayGuiScreen", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
                    method.instructions.insert(insn, insnList);

                    break;
                }
            }
        } /*else if (methodName.equals("func_71353_a") || methodName.equals("loadWorld")) {
            boolean found = false;
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            AbstractInsnNode insn = null;
            while (iterator.hasNext()) {
                insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 2402)) {
                    insn = insn.getNext().getNext().getNext();
                    found = true;
                    methodsFound++;
                    methodTransformed = true;
                    break;
                }
            }

            if (found && insn != null) {
                method.instructions.remove(insn.getNext());
                method.instructions.insert(insn, new InsnNode(Opcodes.POP2));
            }
        }*/
        return methodsFound >= 2;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }
}
