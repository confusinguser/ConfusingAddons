package com.confusinguser.confusingaddons;

import com.confusinguser.confusingaddons.commands.ConfusingAddonsCommand;
import com.confusinguser.confusingaddons.commands.GChatCommand;
import com.confusinguser.confusingaddons.commands.WarpCommand;
import com.confusinguser.confusingaddons.core.RuntimeInfo;
import com.confusinguser.confusingaddons.listeners.ChatMessageListener;
import com.confusinguser.confusingaddons.listeners.EventListener;
import com.confusinguser.confusingaddons.utils.*;
import com.google.gson.JsonObject;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

@Mod(modid = ConfusingAddons.MODID, version = ConfusingAddons.VERSION, name = ConfusingAddons.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class ConfusingAddons {
    public static final String MODID = "ConfusingAddons";
    public static final String VERSION = "1.0-duheuty";
    public static final String MOD_NAME = "ConfusingAddons";
    private static ConfusingAddons instance;
    private EventListener eventListener;
    private final Utils utils = new Utils(this);
    private final LangUtils langUtils = new LangUtils(this);
    private final ApiUtils apiUtils = new ApiUtils(this);
    public Logger logger = LogManager.getLogger(MODID);
    private ConfusingHypixelAPI API;
    private ConfigValues configValues;
    private String apiKey = "";
    private RuntimeInfo runtimeInfo = new RuntimeInfo();

    public static ConfusingAddons getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        instance = this;
        configValues = new ConfigValues(this, e.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        ClientCommandHandler.instance.registerCommand(new ConfusingAddonsCommand(this));
        ClientCommandHandler.instance.registerCommand(new GChatCommand());
        ClientCommandHandler.instance.registerCommand(new WarpCommand());

        eventListener = new EventListener(this);

        MinecraftForge.EVENT_BUS.register(new ChatMessageListener());
        MinecraftForge.EVENT_BUS.register(eventListener);
        configValues.loadConfig();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        Thread updateCheckerThread = new Thread(() -> {
            JsonObject runtimeInfoJSON = apiUtils.getRuntimeInfo();
                runtimeInfo = new RuntimeInfo(runtimeInfoJSON.get("downloadURL").getAsString(),
                        runtimeInfoJSON.get("liveGCIP").getAsString(),
                        !VERSION.equals(runtimeInfoJSON.get("latestVersion").getAsString()),
                        runtimeInfoJSON.get("latestVersion").getAsString(),
                        runtimeInfoJSON.get("directDownloadURL").getAsString());
        });
        updateCheckerThread.start();
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public Utils getUtils() {
        return utils;
    }

    public ApiUtils getApiUtils() {
        return apiUtils;
    }

    public ConfigValues getConfigValues() {
        return configValues;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) throws IllegalArgumentException {
        setApiKey(apiKey, true);
    }

    public ConfusingHypixelAPI getAPI() {
        return API;
    }

    public void setApiKey(String apiKey, boolean saveConfig) throws IllegalArgumentException {
        this.apiKey = apiKey;
        API = new ConfusingHypixelAPI(UUID.fromString(this.apiKey));
        if (saveConfig) getConfigValues().saveConfig();
        getApiUtils().clearCaches();
    }

    public void resetAPIKey() {
        API = null;
    }

    public LangUtils getLangUtils() {
        return langUtils;
    }

    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }
}