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
        boolean found = false;
        if (methodName.equals("runGameLoop") || methodName.equals("func_71411_J")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, Opcodes.INVOKEVIRTUAL, "net/minecraft/client/entity/EntityPlayerSP", "isEntityInsideOpaqueBlock", "()Z") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.IFEQ) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext().getNext().getNext() /* 2x opcode -1 for some reason */, Opcodes.ALOAD, 0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.GETFIELD, "net/minecraft/client/Minecraft", "gameSettings", "Lnet/minecraft/client/settings/GameSettings;") &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.ICONST_0) &&
                        ASMUtils.insnQueryMatch(insn = insn.getNext(), Opcodes.PUTFIELD, "net/minecraft/client/settings/GameSettings", "thirdPersonView", "I")) {
                    AbstractInsnNode temp = insn.getPrevious(); // Because next and prev become null when the insn is removed
                    method.instructions.remove(insn);
                    for (int i = 0; i < 3; i++) {
                        insn = temp;
                        temp = temp.getPrevious();
                        if (temp.getOpcode() == -1) {
                            temp = temp.getPrevious();
                            if (temp.getOpcode() == -1) temp = temp.getPrevious();
                        }
                        method.instructions.remove(insn);
                    }

                    methodsFound++;
                }
            }
        } else if (methodName.equals("launchIntegratedServer")) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            AbstractInsnNode insn = null;
            while (iterator.hasNext()) {
                insn = iterator.next();
                if (ASMUtils.insnQueryMatch(insn, -1, 2335)) {
                    insn = insn.getNext().getNext().getNext().getNext();
                    found = true;
                    methodsFound++;
                    break;
                }
            }
            if (found && insn != null) {
                AbstractInsnNode temp = insn.getPrevious(); // Because next and prev become null when the insn is removed
                method.instructions.remove(insn);
                for (int i = 0; i < 3; i++) {
                    insn = temp;
                    temp = temp.getPrevious();
                    if (temp.getOpcode() == -1) {
                        temp = temp.getPrevious();
                        if (temp.getOpcode() == -1) temp = temp.getPrevious();
                    }
                    method.instructions.remove(insn);
                }

                InsnList insnList = new InsnList();
                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insnList.add(new TypeInsnNode(Opcodes.NEW, "net/minecraft/client/gui/GuiScreenWorking"));
                insnList.add(new InsnNode(Opcodes.DUP));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "net/minecraft/client/gui/GuiScreenWorking", "<init>", "()V", false));
                insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/Minecraft", "displayGuiScreen", "(Lnet/minecraft/client/gui/GuiScreen;)V", false));
                method.instructions.insert(temp.getNext().getNext(), insnList);
                System.out.println("\n" + ASMUtils.getBytecodeFromMethodNode(method));
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
        return methodsFound >= 3 && found;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.Minecraft";
    }
}
