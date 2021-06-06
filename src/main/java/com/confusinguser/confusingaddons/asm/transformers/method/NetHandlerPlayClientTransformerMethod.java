package com.confusinguser.confusingaddons.asm.transformers.method;

import com.confusinguser.confusingaddons.asm.core.ITransformerMethod;
import com.confusinguser.confusingaddons.asm.utils.ASMUtils;
import org.objectweb.asm.tree.MethodNode;

public class NetHandlerPlayClientTransformerMethod implements ITransformerMethod {
    @Override
    public boolean transformMethod(MethodNode method, String methodName, String methodDesc) {
        ASMUtils.deleteLines(method.instructions, 1259); // MC-3564 Debug message "Unable to locate Sign at (x, y, z)" left inside Minecraft Client
        return true;
    }

    @Override
    public String getTargetClassName() {
        return "net.minecraft.client.network.NetHandlerPlayClient";
    }
}
