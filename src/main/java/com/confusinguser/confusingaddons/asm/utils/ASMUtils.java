package com.confusinguser.confusingaddons.asm.utils;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ASMUtils {

    public static boolean insnQueryMatch(AbstractInsnNode insn, int opcode, Object... data) {
        if (insn == null || insn.getOpcode() != opcode) return false;
        if (data.length == 0) return true; // Nothing more to validate
        if (insn instanceof MethodInsnNode && data.length >= 3) {
            return methodInsnQueryMatch((MethodInsnNode) insn, data[0].toString(), data[1].toString(), data[2].toString());
        } else if (insn instanceof VarInsnNode) {
            return varInsnQueryMatch((VarInsnNode) insn, (Integer) data[0]);
        } else if (insn instanceof FieldInsnNode && data.length >= 3) {
            return fieldInsnQueryMatch((FieldInsnNode) insn, data[0].toString(), data[1].toString(), data[2].toString());
        } else if (insn instanceof TypeInsnNode) {
            return typeInsnQueryMatch((TypeInsnNode) insn, data[0].toString());
        } else if (insn instanceof LineNumberNode) {
            return lineNumberInsnQueryMatch((LineNumberNode) insn, (Integer) data[0]);
        } else {
            return false;
        }
    }

    private static boolean methodInsnQueryMatch(MethodInsnNode insn, String owner, String name, String desc) {
        return insn.owner.equals(owner) &&
                insn.name.equals(name) &&
                insn.desc.equals(desc);
    }

    private static boolean varInsnQueryMatch(VarInsnNode insn, int var) {
        return insn.var == var;
    }

    private static boolean fieldInsnQueryMatch(FieldInsnNode insn, String owner, String name, String desc) {
        return insn.owner.equals(owner) &&
                insn.name.equals(name) &&
                insn.desc.equals(desc);
    }

    private static boolean typeInsnQueryMatch(TypeInsnNode insn, String desc) {
        return insn.desc.equals(desc);
    }

    private static boolean lineNumberInsnQueryMatch(LineNumberNode insn, int line) {
        return insn.line == line;
    }

    public static String getBytecodeFromMethodNode(MethodNode method) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < method.instructions.size(); i++) {
            AbstractInsnNode insn;
            insn = method.instructions.get(i);
            if (insn.getOpcode() != -1 && !(insn instanceof InsnNode)) output.append(ASMConstants.OPCODES[insn.getOpcode()]).append(' ');
            else if (insn.getOpcode() != -1) output.append(ASMConstants.OPCODES[insn.getOpcode()]);
            if (insn instanceof LabelNode) {
                output.append("L").append(getLabelIndex(method, ((LabelNode) insn).getLabel()));
            }
            else if (insn instanceof LineNumberNode) output.append("LINENUMBER ").append(((LineNumberNode) insn).line).append(" L").append(getLabelIndex(method, ((LineNumberNode) insn).start.getLabel()));
            else if (insn instanceof FieldInsnNode)
                output.append(((FieldInsnNode) insn).owner).append('.').append(((FieldInsnNode) insn).name).append(" : ").append(((FieldInsnNode) insn).desc);
            else if (insn instanceof MethodInsnNode)
                output.append(((MethodInsnNode) insn).owner).append('.').append(((MethodInsnNode) insn).name).append(" : ").append(((MethodInsnNode) insn).desc);
            else if (insn instanceof VarInsnNode) output.append(((VarInsnNode) insn).var);
            else if (insn instanceof JumpInsnNode) output.append('L').append(getLabelIndex(method, ((JumpInsnNode) insn).label.getLabel()));
            else if (insn instanceof LdcInsnNode) output.append(((LdcInsnNode) insn).cst.toString());
            else if (insn instanceof TypeInsnNode) output.append(((TypeInsnNode) insn).desc);
            output.append('\n');
        }
        return output.toString();
    }

    private static int getLabelIndex(MethodNode method, Label label) {
        return Arrays.stream(method.instructions.toArray())
                .filter(abstractInsnNode -> abstractInsnNode instanceof LabelNode)
                .map(abstractInsnNode -> ((LabelNode) abstractInsnNode).getLabel().toString())
                .collect(Collectors.toList()).indexOf((label.toString()));
    }
}
