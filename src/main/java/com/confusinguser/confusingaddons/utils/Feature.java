package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;

public enum Feature {
    HIDE_LOBBY_SPAM,
    SHOW_CLICK_COMMANDS,
    HIDE_JOIN_LEAVE_MESSAGES,
    CLEANER_KICK_ERROR_MESSAGES,
    SHOW_QUEUE_ESIMATE,
    MULTITHREADING,
    COPY_NBT,
    AUTO_OPEN_MADDOX_GUI,
    SWITCH_TO_BATPHONE_ON_SLAYER_DONE,
    SHOW_PACKETS_IN_CHAT;

    protected boolean status;

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

    public String getIdString() {
        return String.valueOf(getId());
    }
}
