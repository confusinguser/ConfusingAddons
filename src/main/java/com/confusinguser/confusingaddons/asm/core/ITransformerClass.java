package com.confusinguser.confusingaddons.asm.core;

import org.objectweb.asm.tree.ClassNode;

public interface ITransformerClass {
    boolean transformClass(ClassNode clazz);

    String getTargetClassName();
}
