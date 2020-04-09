package com.confusinguser.confusingaddons.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.logging.log4j.Logger;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import scala.actors.threadpool.Arrays;

public class ConfigValues {

	private static final int CONFIG_VERSION = 1;

	private ConfusingAddons main;

	private File settingsConfigFile;
	private JsonObject settingsConfig = new JsonObject();

	public ConfigValues(ConfusingAddons main, File settingsConfigFile) {
		this.main = main;
		this.settingsConfigFile = settingsConfigFile;
	}

	public void loadConfig() {
		main.logger.info("loadconfig()");
		if (settingsConfigFile.exists()) {
			main.logger.info("file exisits");
			try {
				JsonElement fileElement;
				try (FileReader reader = new FileReader(settingsConfigFile)) {
					fileElement = new JsonParser().parse(reader);
				}

				if (fileElement == null || fileElement.isJsonNull()) {
					throw new JsonParseException("File is null!");
				}
				settingsConfig = fileElement.getAsJsonObject();
				main.logger.info("Setting features");
				for (Feature feature : Feature.values()) {
					if (settingsConfig.has(feature.getId())) {
						feature.setStatus(settingsConfig.get(feature.getId()).getAsBoolean());
					}
					main.logger.info("Feature: " + feature.toString() + ", enabled: " + feature.getId());
				}

			} catch (JsonParseException | IllegalStateException | IOException ex) {
				main.logger.warn("There was an error loading the config. Resetting all settings to default.");
				main.logger.warn(Arrays.toString(ex.getStackTrace()));
				saveConfig();
			}
		} else {
			saveConfig();
		}
	}

	public void saveConfig() {
		settingsConfig = new JsonObject();
		JsonArray status = new JsonArray();

		try {
			settingsConfigFile.createNewFile();

			for (Feature feature: Feature.values()) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty(feature.getId(), feature.getStatus());
				status.add(jsonObject);
			}

			settingsConfig.add("status", status);
			settingsConfig.addProperty("configVersion", CONFIG_VERSION);

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