package com.confusinguser.confusingaddons.core;

public enum HypixelRank {
    NONE("§7"),
    VIP("§a[VIP] "),
    VIP_PLUS("§a[VIP+] "),
    MVP("§b[MVP] "),
    MVP_PLUS("§b[MVP+] "),
    SUPERSTAR("§6[MVP++] "),
    YOUTUBER("§c[§fYOUTUBE§c] "),
    HELPER("§1[HELPER]"),
    MOD("§2[MOD] "),
    ADMIN("§c[ADMIN] ");

    private final String prefix;

    HypixelRank(String prefix) {
        this.prefix = prefix;
    }

    public static HypixelRank getHypixelRankFromName(String name) {
        for (HypixelRank rank : values()) {
            if (rank.name().equalsIgnoreCase(name)) {
                return rank;
            }
        }
        return NONE;
    }

    public String getPrefix() {
        return prefix;
    }
}
