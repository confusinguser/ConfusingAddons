package com.confusinguser.confusingaddons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.item.ItemStack")) {
            ClassReader reader = new ClassReader(basicClass);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, ClassReader.SKIP_FRAMES);
            for (MethodNode method : classNode.methods) {
                String mappedName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                String mappedDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);
                if (mappedName.equals("getTooltip") || mappedName.equals("func_82840_a")) {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode insn = iterator.next();
                        if (insn.getOpcode() == Opcodes.ARETURN) {
                            InsnList insnList = new InsnList();
                            insnList.add(new InsnNode(Opcodes.POP));
                            insnList.add(new VarInsnNode(Opcodes.ALOAD, 0)); // add 'this' on stack
                            insnList.add(new VarInsnNode(Opcodes.ALOAD, 3)); // The list with the tooltip (List<String>)
                            insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/confusinguser/confusingaddons/asm/hooks/ItemStackHook",
                                    "getToolTip", "(Lnet/minecraft/item/ItemStack;Ljava/util/List;)Ljava/util/List;", false));
                            insnList.add(new InsnNode(Opcodes.ARETURN));
                            method.instructions.insertBefore(insn, insnList);
                        }
                    }
                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    classNode.accept(writer);
                    return writer.toByteArray();
                }
            }
        }
        return basicClass;
    }
}