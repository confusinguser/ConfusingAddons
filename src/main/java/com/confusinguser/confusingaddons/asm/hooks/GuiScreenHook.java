package com.confusinguser.confusingaddons.asm.hooks;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;

import java.net.URI;

@SuppressWarnings("unused")
public class GuiScreenHook {
    private static final String[] trustedDomains = new String[] {"google.com", "hypixel.net"};

    public static void handleComponentClick(GuiScreen guiScreen, ClickEvent clickEvent, URI uri) {
        for (String trustedDomain : trustedDomains)
            if (uri.getHost().endsWith("." + trustedDomain) || uri.getHost().equals(trustedDomain)) {
                GuiConfirmOpenLink gui = new GuiConfirmOpenLink(guiScreen, clickEvent.getValue(), 31102009, true);
                gui.disableSecurityWarning();
                guiScreen.mc.displayGuiScreen(gui);
                return;
            }
        guiScreen.mc.displayGuiScreen(new GuiConfirmOpenLink(guiScreen, clickEvent.getValue(), 31102009, false));
    }
}
