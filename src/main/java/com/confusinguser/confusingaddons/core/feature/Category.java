package com.confusinguser.confusingaddons.core.feature;

public enum Category {
    MAIN(""),
    BUG_FIXES("Bug Fixes"),
    REMOTE_GUILD_CHAT("Remote Guild Chat");


    private final String friendlyName;

    Category(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
