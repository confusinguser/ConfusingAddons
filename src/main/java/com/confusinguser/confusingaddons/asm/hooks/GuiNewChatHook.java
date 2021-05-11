package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.util.IChatComponent;

public class GuiNewChatHook {

    public static IChatComponent printChatMessageWithOptionalDeletion(IChatComponent component) { // Fix mods text formatting resetting on 2nd line
        return Utils.fixChatComponentColors(component);
    }
}
