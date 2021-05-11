package com.confusinguser.confusingaddons.core;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.core.feature.FeatureOption;
import com.confusinguser.confusingaddons.core.feature.FeatureOptionText;
import com.google.gson.*;

import java.io.*;
import java.util.Arrays;

public class ConfigValues {

    private static final int CONFIG_VERSION = 1;

    private final ConfusingAddons main;

    private final File settingsConfigFile;
    private JsonObject settingsConfig = new JsonObject();

    public ConfigValues(ConfusingAddons main, File settingsConfigFile) {
        this.main = main;
        this.settingsConfigFile = settingsConfigFile;
    }

    public void loadConfig() {
        if (settingsConfigFile.exists()) {
            try {
                JsonElement fileElement;
                try (FileReader reader = new FileReader(settingsConfigFile)) {
                    fileElement = new JsonParser().parse(reader);
                }

                if (fileElement == null || fileElement.isJsonNull()) {
                    throw new JsonParseException("File is null!");
                }
                settingsConfig = fileElement.getAsJsonObject();

                JsonObject features = settingsConfig.getAsJsonObject("features");
                for (Feature feature : Feature.features) {

                    if (!features.has(feature.getId())) continue;
                    JsonObject featureData = features.getAsJsonObject(feature.getId());
                    feature.setStatus(featureData.get("enabled").getAsBoolean(), false);

                    if (!Arrays.asList(feature.getOptions()).isEmpty()) {
                        JsonObject featureOptions = featureData.getAsJsonObject("options");

                        for (FeatureOption featureOption : feature.getOptions()) {
                            if (!featureOptions.has(featureOption.getId())) continue;
                            JsonObject featureOptionData = featureOptions.getAsJsonObject(feature.getId());

                            if (featureOption instanceof FeatureOptionText) {
                                ((FeatureOptionText) featureOption).setValue(featureOptionData.get("value").getAsString());
                            }
                        }
                    }
                }

                try {
                    main.setApiKey(settingsConfig.get("apikey").getAsString(), false);
                } catch (IllegalArgumentException ex) {
                    main.logger.warn("API key is malformed, resetting it...");
                    main.resetAPIKey();
                    saveConfig();
                }
            } catch (IllegalStateException | IOException ex) {
                main.logger.warn("There was an error loading the config. Settings have been restored to default.");
                main.logger.warn(ex);
                setAllFeaturesToDefault();
                saveConfig();
            } catch (JsonParseException | NullPointerException ex) {
                main.logger.warn("Old version of mod detected, updating config");
                saveConfig();
            }
        } else {
            setAllFeaturesToDefault();
            saveConfig();
        }
    }

    private void setAllFeaturesToDefault() {
        for (Feature feature : Feature.features) feature.setStatus(feature.getDefaultStatus(), false);
    }

    public void saveConfig() {
        settingsConfig = new JsonObject();

        try {
            //noinspection ResultOfMethodCallIgnored
            settingsConfigFile.createNewFile();

            JsonObject features = new JsonObject();
            for (Feature feature : Feature.features) {
                JsonObject featureData = new JsonObject();
                featureData.addProperty("enabled", feature.isEnabled());
                JsonObject featureOptions = new JsonObject();
                for (FeatureOption featureOption : feature.getOptions()) {
                    JsonObject featureOptionData = new JsonObject();
                    if (featureOption instanceof FeatureOptionText) {
                        featureOptionData.addProperty("value", ((FeatureOptionText) featureOption).getValue()); // TODO load the stuff
                    }
                    featureOptions.add(featureOption.getId(), featureOptionData);
                }
                featureData.add("options", featureOptions);
                features.add(feature.getId(), featureData);
            }

            settingsConfig.add("features", features); // TODO TEST THIS METHOD

            settingsConfig.addProperty("configVersion", CONFIG_VERSION);
            settingsConfig.addProperty("apikey", main.getApiKey());

            try (FileWriter writer = new FileWriter(settingsConfigFile);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.write(settingsConfig.toString());
            }
        } catch (IOException ex) {
            main.logger.error("[ConfusingAddons] An error occurred while attempting to save the config!");
            main.logger.error(ex.getMessage(), ex);
        }
    }
}