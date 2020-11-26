package com.confusinguser.confusingaddons.asm.hooks;

@SuppressWarnings("unused")
public class GuiConfirmOpenLinkHook {
    public static String GuiConfirmOpenLink(String link) {
        if (link.endsWith("dQw4w9WgXcQ")) { // Rick roll yt id
            return "§4§lWARNING§4: You are about to be rickrolled!";
        }
        return null;
    }
}
