package com.confusinguser.confusingaddons.asm.core;

import org.objectweb.asm.tree.MethodNode;

public interface ITransformerMethod {
    boolean transformMethod(MethodNode method, String methodName, String methodDesc);

    String getTargetClassName();
}
