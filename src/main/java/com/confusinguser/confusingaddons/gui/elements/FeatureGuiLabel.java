package com.confusinguser.confusingaddons.gui.elements;

import com.confusinguser.confusingaddons.core.feature.Feature;

public class FeatureGuiLabel extends BetterGuiLabel {
    private final Feature feature;

    public FeatureGuiLabel(int x, int y, int color, Feature feature) {
        super(feature.getName(), x, y, color);
        this.feature = feature;
    }

    public Feature getFeature() {
        return feature;
    }
}
