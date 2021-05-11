package com.confusinguser.confusingaddons.core.feature;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.LangUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Feature {
    public static List<Feature> features = new ArrayList<>();
    private static List<String> featureIds = new ArrayList<>();
    private boolean status;

    private final String id;
    private final String name;
    private final String desc;
    private final boolean defaultStatus;
    private final Category category;
    private final FeatureOption[] options;

    public Feature(String id) {
        this(id, null);
    }

    Feature(String id, Category category) {
        this(id, category, new FeatureOption[0]);
    }

    Feature(String id, Category category, FeatureOption[] options) {
        this(id, true, category, options);
    }

    Feature(String id, boolean defaultStatus, Category category) {
        this(id, defaultStatus, category, new FeatureOption[0]);
    }

    Feature(String id, boolean defaultStatus, Category category, FeatureOption[] options) {
        this.id = id;
        this.name = LangUtils.getTranslation("feature." + id.toLowerCase() + ".name");
        this.desc = LangUtils.getTranslation("feature." + id.toLowerCase() + ".desc");
        this.category = category;
        this.status = defaultStatus;
        this.defaultStatus = defaultStatus;
        this.options = options;
    }


    public boolean isEnabled() {
        return status;
    }

    public void setStatus(boolean status) {
        setStatus(status, true);
    }

    public void setStatus(boolean status, boolean save) {
        this.status = status;
        if (save) ConfusingAddons.getInstance().getConfigValues().saveConfig();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getDefaultStatus() {
        return defaultStatus;
    }

    public Category getFeatureType() {
        return category;
    }

    public FeatureOption[] getOptions() {
        return options;
    }

    public String getDescription() {
        return desc;
    }

    public static void initFeatures() {
        features.add(new Feature("HIDE_LOBBY_SPAM", Category.MAIN));
        features.add(new Feature("HIDE_JOIN_LEAVE_MESSAGES", Category.MAIN));
        features.add(new Feature("SHOW_CLICK_COMMANDS", Category.MAIN));
        features.add(new Feature("HURT_EFFECT_FIX", Category.MAIN, new FeatureOption[] {
                new FeatureOptionSlider("INTENSITY", 0d, 2d) // TODO lang file the id of featureoptions
        }));
        features.add(new Feature("DUNGEON_GEAR_TOOLTIP", Category.MAIN));
        features.add(new Feature("W_ALIAS_WARP", Category.MAIN));
        features.add(new Feature("DISABLE_SECURITY_WARNING", false, Category.MAIN));

        features.add(new Feature("SHOW_MESSAGES_FROM_DISCORD", Category.REMOTE_GUILD_CHAT));

        features.add(new Feature("TRANSPARENT_BLOCKS_LIGHT_FIX", Category.BUG_FIXES));
        features.add(new Feature("BOSS_BAR_FIX", Category.BUG_FIXES));
        features.add(new Feature("CLIENT_SIDE_MESSAGES_CHAT_FORMATTING_FIX", Category.BUG_FIXES));
        features.add(new Feature("ENCHANTED_FISHING_ROD_RENDERING_FIX", Category.BUG_FIXES));
        features.add(new Feature("PERSPECTIVE_RESET_WHEN_INSIDE_BLOCK", Category.BUG_FIXES));
        features.add(new Feature("MAIN_MENU_VISIBLE_WHEN_CREATING_WORLD", Category.BUG_FIXES));
        features.add(new Feature("DEBUG_MESSAGES_IN_CHAT_FIX", Category.BUG_FIXES));
        features.add(new Feature("HAND_PLACEMENT_FIX", Category.BUG_FIXES));

        features.add(new Feature("COPY_NBT", false, null));

        features = Collections.unmodifiableList(features);
        featureIds = Collections.unmodifiableList(features.stream().map(Feature::getId).collect(Collectors.toList()));
    }

    public static Feature getFeatureById(String id) {
        int index = featureIds.indexOf(id);
        if (index < 0) return new Feature(id); // Avoid NPE
        return features.get(index);
    }

    public static boolean isEnabled(String id) {
        Feature feature = getFeatureById(id);
        if (feature == null) return false;
        return feature.isEnabled();
    }
}
