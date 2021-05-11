package com.confusinguser.confusingaddons.gui.elements;

import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.client.Minecraft;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BetterGuiLabel {
    private final String text;
    private final int x;
    private final int y;
    private final int color;

    private boolean hovering;

    public BetterGuiLabel(String text, int x, int y, int color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void drawLabel(int mouseX, int mouseY) {
        Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
        Vector2f[] boundingbox = getBoundingBox();
        hovering = Utils.inBoundingBox(new Vector2f(mouseX, mouseY), boundingbox[0], boundingbox[1]);
    }

    public Vector2f[] getBoundingBox() {
        List<Vector2f> output = new ArrayList<>();
        output.add(new Vector2f(x - 5, y - 5)); // Y is centered
        output.add(new Vector2f(x + getTextWidth() + 5, y + 15));
        return output.toArray(new Vector2f[0]);
    }

    public int getTextWidth() {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }

    public boolean isHovering() {
        return hovering;
    }
}
