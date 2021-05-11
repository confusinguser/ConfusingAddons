package com.confusinguser.confusingaddons.core.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class FeatureTextField extends GuiTextField {
    private final FeatureOption featureOption;

    public FeatureTextField(int componentId, int x, int y, int width, int height, FeatureOption featureOption) {
        super(componentId, Minecraft.getMinecraft().fontRendererObj, x, y, width, height);
        this.featureOption = featureOption;
    }

    public FeatureOption getFeatureOption() {
        return featureOption;
    }
}
