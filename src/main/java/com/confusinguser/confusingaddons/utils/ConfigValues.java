package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.*;
import scala.actors.threadpool.Arrays;

import java.io.*;

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
                for (Feature feature : Feature.values()) {
                    feature.status = settingsConfig.get("status").getAsJsonArray().get(feature.getId()).getAsJsonObject().get(feature.getIdString()).getAsBoolean();
                }
                try {
                    main.setApiKey(settingsConfig.get("apikey").getAsString(), false);
                } catch (IllegalArgumentException ignored) {
                }

            } catch (IllegalStateException | IOException ex) {
                main.logger.warn("There was an error loading the config. Resetting all settings to default.");
                main.logger.warn(Arrays.toString(ex.getStackTrace()));
                setAllFeaturesToDefault();
                saveConfig();
            } catch (JsonParseException | IndexOutOfBoundsException ex) {
                main.logger.warn("Old version of mod detected, updating config");
                saveConfig();
            }
        } else {
            setAllFeaturesToDefault();
            saveConfig();
        }
    }

    private void setAllFeaturesToDefault() {
        for (Feature feature : Feature.values()) feature.status = false;
    }

    public void saveConfig() {
        settingsConfig = new JsonObject();
        JsonArray status = new JsonArray();

        try {
            settingsConfigFile.createNewFile();

            for (Feature feature : Feature.values()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(feature.getIdString(), feature.isEnabled());
                status.add(jsonObject);
            }

            settingsConfig.add("status", status);
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