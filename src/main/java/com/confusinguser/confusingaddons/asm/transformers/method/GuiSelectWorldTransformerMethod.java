package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.MethodNode;

public class GuiSelectWorldTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        ASMUtils.deleteLines(method.instructions, 182); // TODO Test if this is needed for // MC-185 Creating or loading a singleplayer world shows the main menu for a brief second.
        return false;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.gui.GuiSelectWorld";
    }
}
