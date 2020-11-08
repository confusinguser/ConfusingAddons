package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;

public enum Feature {
    HIDE_LOBBY_SPAM("Hide Lobby \"Spam\""),
    SHOW_CLICK_COMMANDS("Show Click Commands"),
    HIDE_JOIN_LEAVE_MESSAGES("Hide Join And Leave Messages"),
//    CLEANER_KICK_ERROR_MESSAGES,
//    SHOW_QUEUE_ESIMATE(),
    COPY_NBT(false, "Copy NBT"),
    AUTO_OPEN_MADDOX_GUI("Auto Open Batphone GUI"),
    SWITCH_TO_BATPHONE_ON_SLAYER_DONE("Switch To Batphone When Slayer Killed");
//    SHOW_PACKETS_IN_CHAT;

    protected boolean status;

    public boolean showInMenu;
    String name;

    Feature(String name) {
        this.showInMenu = false;
        this.name = name;
    }

    Feature(boolean showInMenu, String name) {
        this.showInMenu = showInMenu;
        this.name = name;
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
}
