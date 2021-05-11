package com.confusinguser.confusingaddons.asm;

import com.confusinguser.confusingaddons.asm.core.ITransformer;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.MethodNode;

public class BlockTransformer implements ITransformer {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        ASMUtils.deleteLines(method.instructions, 2656); // MC-2399 Transparent blocks visually use the brighter light level that they are next to
        return true;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.block.Block";
    }
}
