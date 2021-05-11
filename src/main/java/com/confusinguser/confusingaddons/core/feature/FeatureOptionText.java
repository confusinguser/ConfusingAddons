package com.confusinguser.confusingaddons.core.feature;

public class FeatureOptionText extends FeatureOption {

    private String value = "";

    FeatureOptionText(String name) {
        super(name);
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
