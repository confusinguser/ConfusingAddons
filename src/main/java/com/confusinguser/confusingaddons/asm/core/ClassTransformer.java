package com.confusinguser.confusingaddons.asm.core;

import com.confusinguser.confusingaddons.asm.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {

    private static final ITransformer[] transformers = {
            new ItemStackTransformer(),
            new EntityRendererTransformer(),
            new GuiScreenTransformer(),
            new GuiSelectWorldTransformer(),
            new MinecraftTransformer(),
            new GuiConfirmOpenLinkTransformer(),
            new ChunkRenderWorkerTransformer(),
            new ModelBipedTransformer()
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for (ITransformer transformer : transformers) {
            if (transformer.getTargetClassName().equals(transformedName)) {
                System.out.println("Started transforming " + transformer.getTargetClassName());
                ClassReader reader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, ClassReader.SKIP_FRAMES);
                for (MethodNode method : classNode.methods) {
                    String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                    String methodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);
                    if (transformer.transformMethod(method, methodName, methodDesc)) {
                        System.out.println("Transformed successfully: " + transformer.getTargetClassName());
                    }
                }
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                return writer.toByteArray();
            }
        }
        return basicClass;
    }
}