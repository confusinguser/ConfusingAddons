package com.confusinguser.confusingaddons.gui.elements;

import com.confusinguser.confusingaddons.core.feature.FeatureOptionSlider;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraftforge.fml.client.config.GuiSlider;

public class FeatureGuiSlider extends GuiSlider {
    private final FeatureOptionSlider featureOption;

    public FeatureGuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, FeatureOptionSlider FeatureOption) {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr);
        this.featureOption = FeatureOption;
    }

    public FeatureGuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider par, FeatureOptionSlider FeatureOption) {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
        this.featureOption = FeatureOption;
    }

    public FeatureGuiSlider(int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, ISlider par, FeatureOptionSlider FeatureOption) {
        super(id, xPos, yPos, displayStr, minVal, maxVal, currentVal, par);
        this.featureOption = FeatureOption;
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        super.mouseReleased(par1, par2);
        featureOption.setValue(Utils.round(getValue(), 1));
    }
}
