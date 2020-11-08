package com.confusinguser.confusingaddons.gui;

import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class ConfusingAddonsGui extends GuiScreen {

    public static final int BUTTON_SPACING_H = 20;
    public static final int BUTTON_SPACING_V = 20;
    public static final int BUTTON_WIDTH = 150;
    public static final int SIDE_MARGINS = 130;
    public static final int TOP_MARGIN = 30;

    @Override
    public void initGui() {
        this.buttonList.clear();
        GuiButton featureButton;
        for (Feature feature : Feature.values()) {
            if (!feature.showInMenu) continue;
            this.buttonList.add(new FeatureButton(feature.getId(), (width - (width - SIDE_MARGINS * 2) / (BUTTON_SPACING_H + BUTTON_WIDTH) * BUTTON_SPACING_H - width / (BUTTON_SPACING_H + BUTTON_WIDTH) * BUTTON_WIDTH + BUTTON_SPACING_H) / 2 + feature.getId() % ((width - SIDE_MARGINS * 2) / (BUTTON_SPACING_H + BUTTON_WIDTH)) * (BUTTON_SPACING_H + BUTTON_WIDTH), TOP_MARGIN + BUTTON_SPACING_H * (feature.getId() / (width / (BUTTON_SPACING_V + BUTTON_WIDTH))), BUTTON_WIDTH, 110, feature.getName(), feature));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(mc.fontRendererObj, "§7Confusing§bAddons", width/2, 30, 0x000000);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof FeatureButton) {
            ((FeatureButton) button).feature.setStatus(!((FeatureButton) button).feature.isEnabled());
        }
    }
}
