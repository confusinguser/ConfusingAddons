package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;

public enum Feature {
    HIDE_LOBBY_SPAM("Hide lobby \"spam\""),
    SHOW_CLICK_COMMANDS("Show click commands"),
    HIDE_JOIN_LEAVE_MESSAGES("Hide join and leave messages"),
    COPY_NBT("Copy NBT", false, false),
    AUTO_OPEN_MADDOX_GUI("Auto open batphone GUI"),
    SWITCH_TO_BATPHONE_WHEN_SLAYER_DONE("Switch to batphone when slayer done"),
    HURT_EFFECT_FIX("Angle \"screen tilt\" when hurt to the side you were hit from"),
    SKYBLOCK_TOOLTIP_FEATURES("Show when and by who (only dragon gear) skyblock item was obtained"),
    DUNGEON_GEAR_TOOLTIP("Show what floor and what score dungeon gear has"),
    DISABLE_SECURITY_WARNING("Disable trust warning when opening a link", false);

    protected boolean status;

    public boolean showInMenu;
    private final String name;
    private boolean defaultStatus;

    Feature(String name) {
        this.name = name;
        this.status = true;
        this.defaultStatus = true;
        this.showInMenu = true;
    }

    Feature(String name, boolean defaultStatus) {
        this.name = name;
        this.status = defaultStatus;
        this.defaultStatus = defaultStatus;
        this.showInMenu = true;
    }

    Feature(String name, boolean defaultStatus, boolean showInMenu) {
        this.name = name;
        this.status = defaultStatus;
        this.defaultStatus = defaultStatus;
        this.showInMenu = showInMenu;
    }

    public static Feature getFeatureById(int id) {
        for (Feature feature : values()) {
            if (feature.getId() == id) return feature;
        }
        return null;
    }

    public static Feature getFeatureById(String id) {
        for (Feature feature : values()) {
            if (feature.getIdString().contentEquals(id)) return feature;
        }
        return null;
    }

    public boolean isEnabled() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        ConfusingAddons.getInstance().getConfigValues().saveConfig();
    }

    public int getId() {
        int index = 0;
        for (Feature feature : values()) {
            if (feature.equals(this)) return index;
            index++;
        }
        return -1;
    }

    public String getName() {
        return name;
    }

    public String getIdString() {
        return String.valueOf(getId());
    }

    public boolean getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(boolean defaultStatus) {
        this.defaultStatus = defaultStatus;
    }
}
