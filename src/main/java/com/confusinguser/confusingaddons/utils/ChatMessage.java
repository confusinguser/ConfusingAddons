package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import net.minecraft.util.IChatComponent;

public class ChatMessage {
    private final IChatComponent chatComponent;
    private final int timeSinceCreation;

    public ChatMessage(IChatComponent chatComponent) {
        this.chatComponent = chatComponent;
        timeSinceCreation = ConfusingAddons.getInstance().getPlayerListener().tickCounter;
    }

    public IChatComponent getChatComponent() {
        return chatComponent;
    }

    public int getCreationTime() {
        return timeSinceCreation;
    }
}
