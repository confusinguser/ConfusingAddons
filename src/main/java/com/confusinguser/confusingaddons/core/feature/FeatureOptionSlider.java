package com.confusinguser.confusingaddons.core.feature;

public class FeatureOptionSlider extends FeatureOption {
    private double value;
    private final double minVal;
    private final double maxVal;

    public FeatureOptionSlider(String name, double minVal, double maxVal) {
        super(name);
        this.minVal = minVal;
        this.maxVal = maxVal;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMinVal() {
        return minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }
}
