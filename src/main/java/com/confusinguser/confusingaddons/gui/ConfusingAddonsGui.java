package com.confusinguser.confusingaddons.gui;

import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class ConfusingAddonsGui extends GuiScreen {

    public static final int BUTTON_SPACING_H = 20;
    public static final int BUTTON_SPACING_V = 20;
    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 20;
    public static final int LEFT_MARGIN = 130;
    public static final int RIGHT_MARGIN = 130;
    public static final int TOP_MARGIN = 50;
    public static final int BOTTOM_MARGIN = 30;

    @Override
    public void initGui() {
        int maxFeatureNameWidth = 0;
        for (Feature feature : Feature.values()) {
            maxFeatureNameWidth = Math.max(maxFeatureNameWidth, mc.fontRendererObj.getStringWidth(feature.getName()));
        }
        this.buttonList.clear();
        int cols = Math.max(1, (width - LEFT_MARGIN - RIGHT_MARGIN) / (BUTTON_WIDTH + BUTTON_SPACING_H + maxFeatureNameWidth));
        int maxRows = Math.max(1, (height - TOP_MARGIN - BOTTOM_MARGIN) / (BUTTON_HEIGHT + BUTTON_SPACING_V)); // Crash if dividing by zero when calculating amount of pages
        int rows = Math.max(maxRows, Feature.values().length / cols);
        int margin = (width - cols * (BUTTON_SPACING_H + BUTTON_WIDTH) + BUTTON_SPACING_H) / 2;
        Feature[] values = Feature.values();
        for (int featureIndex = 0; featureIndex < values.length; featureIndex++) {
            Feature feature = values[featureIndex];
            if (!feature.showInMenu) continue;
            int xPos = featureIndex % cols * (BUTTON_SPACING_H + BUTTON_WIDTH + maxFeatureNameWidth);
            xPos += margin;
            int yPos = TOP_MARGIN + featureIndex / cols % rows * (BUTTON_SPACING_V + BUTTON_HEIGHT);
            this.buttonList.add(new FeatureButton(featureIndex, xPos, yPos, BUTTON_WIDTH, BUTTON_HEIGHT, feature));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(mc.fontRendererObj, "§7Confusing§bAddons", width / 2, 30, 0x000000);
        int maxFeatureNameWidth = 0;
        for (Feature feature : Feature.values()) {
            maxFeatureNameWidth = Math.max(maxFeatureNameWidth, mc.fontRendererObj.getStringWidth(feature.getName()));
        }
        Feature[] values = Feature.values();
        int cols = Math.max(1, (width - LEFT_MARGIN - RIGHT_MARGIN) / (BUTTON_WIDTH + BUTTON_SPACING_H + maxFeatureNameWidth));
        int maxRows = Math.max(1, (height - TOP_MARGIN - BOTTOM_MARGIN) / (BUTTON_HEIGHT + BUTTON_SPACING_V)); // Crash if dividing by zero when calculating amount of pages
        int rows = Math.max(maxRows, Feature.values().length / cols);
        for (int featureIndex = 0; featureIndex < values.length; featureIndex++) {
            Feature feature = values[featureIndex];
            int xPos = featureIndex % cols * (BUTTON_SPACING_H + BUTTON_WIDTH);
            int yPos = TOP_MARGIN + featureIndex / cols % rows * (BUTTON_SPACING_V + BUTTON_HEIGHT);
            drawCenteredString(mc.fontRendererObj, feature.getName(), xPos, yPos, 0xFFFFFFF);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button instanceof FeatureButton) {
            ((FeatureButton) button).feature.setStatus(!((FeatureButton) button).feature.isEnabled());
        }
    }
}
