package com.confusinguser.confusingaddons.asm.core;

import com.confusinguser.confusingaddons.asm.transformers.clazz.GuiConfirmOpenLinkTransformerClass;
import com.confusinguser.confusingaddons.asm.transformers.method.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.logging.Logger;

public class ClassTransformer implements IClassTransformer {

    private static final ITransformerMethod[] methodTransformers = {
            new ItemStackTransformerMethod(),
            new EntityRendererTransformerMethod(),
            new GuiSelectWorldTransformerMethod(),
            new MinecraftTransformerMethod(),
            new GuiNewChatTransformerMethod(),
            new LayerHeldItemTransformerMethod(),
            new BlockTransformerMethod(),
            new GuiIngameTransformerMethod(),
            new NetHandlerPlayClientTransformerMethod(),
            new RenderPlayerTransformerMethod(),
            new GuiConfirmOpenLinkTransformerMethod()
    };

    private static final ITransformerClass[] classTransformers = {
            new GuiConfirmOpenLinkTransformerClass()
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        byte[] methodTransformed = methodTransform(name, transformedName, basicClass);
        return classTransform(name, transformedName, methodTransformed);
    }

    private byte[] methodTransform(String name, String transformedName, byte[] basicClass) {
        for (ITransformerMethod transformer : methodTransformers) {
            if (transformer.getTargetClassName().equals(transformedName)) {
                Logger.getLogger("ConfusingAddons").fine("Started transforming " + transformer.getTargetClassName());
                ClassReader reader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, ClassReader.SKIP_FRAMES);
                for (MethodNode method : classNode.methods) {
                    String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(classNode.name, method.name, method.desc);
                    String methodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(method.desc);
                    if (transformer.transformMethod(method, methodName, methodDesc)) {
                        Logger.getLogger("ConfusingAddons").fine("Transformed successfully: " + transformer.getTargetClassName() + "#" + methodName);
                    }
                }
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                return writer.toByteArray();
            }
        }
        return basicClass;
    }

    private byte[] classTransform(String name, String transformedName, byte[] basicClass) {
        for (ITransformerClass transformer : classTransformers) {
            if (transformer.getTargetClassName().equals(transformedName)) {
                Logger.getLogger("ConfusingAddons").fine("Started transforming " + transformer.getTargetClassName());
                ClassReader reader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                reader.accept(classNode, ClassReader.SKIP_FRAMES);
                if (transformer.transformClass(classNode)) {
                    Logger.getLogger("ConfusingAddons").fine("Transformed successfully: " + transformer.getTargetClassName());
                }
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                return writer.toByteArray();
            }
        }
        return basicClass;
    }
}