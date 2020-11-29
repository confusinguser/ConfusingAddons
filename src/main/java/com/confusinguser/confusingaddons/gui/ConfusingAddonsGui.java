package com.confusinguser.confusingaddons.gui;

import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfusingAddonsGui extends GuiScreen {

    public static final int BUTTON_WIDTH = 40;
    public static final int BUTTON_SPACING_V = 12;
    public static final int BUTTON_HEIGHT = 20;
    public static final int MID_MARGIN = 10;
    public static final int LEFT_MARGIN = 50;
    public static final int TOP_MARGIN = 50;
    public static final int BOTTOM_MARGIN = 30;

    @Override
    public void initGui() {
        this.buttonList.clear();
        List<Feature> features = Arrays.stream(Feature.values()).filter(feature -> feature.showInMenu).collect(Collectors.toList());
        for (int featureIndex = 0; featureIndex < features.size(); featureIndex++) {
            Feature feature = features.get(featureIndex);
            int xPos = (width + MID_MARGIN) / 2;
            xPos += LEFT_MARGIN;
            int yPos = TOP_MARGIN + featureIndex * (BUTTON_SPACING_V + BUTTON_HEIGHT);
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
        List<FeatureButton> buttons = buttonList.stream()
                .filter(b -> b instanceof FeatureButton)
                .map(b -> (FeatureButton) b)
                .filter(featureButton -> featureButton.feature.showInMenu)
                .collect(Collectors.toList());

        for (int featureIndex = 0; featureIndex < buttons.size(); featureIndex++) {
            Feature feature = buttons.get(featureIndex).feature;
            int xPos = (width - MID_MARGIN) / 2 - mc.fontRendererObj.getStringWidth(feature.getName());
            xPos += LEFT_MARGIN;
            int yPos = TOP_MARGIN + 5 /* so text is centered in Y */ + featureIndex * (BUTTON_SPACING_V + BUTTON_HEIGHT);
            mc.fontRendererObj.drawString(feature.getName(), xPos, yPos, 0xFFFFFF, true);
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
