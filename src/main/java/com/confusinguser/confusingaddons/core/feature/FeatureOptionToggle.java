package com.confusinguser.confusingaddons.core.feature;

public class FeatureOptionToggle extends FeatureOption {
    private boolean state;

    FeatureOptionToggle(String name, boolean state) {
        super(name);
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
