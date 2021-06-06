package com.confusinguser.confusingaddons;

import com.confusinguser.confusingaddons.commands.*;
import com.confusinguser.confusingaddons.core.ConfigValues;
import com.confusinguser.confusingaddons.core.LiveGcConnectionManager;
import com.confusinguser.confusingaddons.core.RuntimeInfo;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.listeners.ChatMessageListener;
import com.confusinguser.confusingaddons.listeners.EventListener;
import com.confusinguser.confusingaddons.utils.ApiUtil;
import com.confusinguser.confusingaddons.utils.ConfusingHypixelAPI;
import com.confusinguser.confusingaddons.utils.bazaar.BazaarCalculator;
import com.google.gson.JsonObject;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

@Mod(modid = ConfusingAddons.MODID, version = ConfusingAddons.VERSION, name = ConfusingAddons.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class ConfusingAddons {
    /*
        TODO list
        apikey in gui
        rainbow rank/plus for self. ss faker, like matdoes.dev one
        Tooltip category? Better category system
        Hide mc useless scoreboard values if val == place on scoreboard and on hypixel
        Stop burning after dying
        What if you remove sorting of tab complete?
        Add antitokenlogger to it, add option to let NEU in
        Init packet to sbg bot
        Reduce FPS when unfocused
        Fix hide lobby spam
        DONE Desc tooltips, shorter feature names
        For some reason back button goes 2 wrong page when clicking on diff parts
    */
    public static final String MODID = "confusingaddons";
    public static final String VERSION = "1.0";
    public static final String MOD_NAME = "ConfusingAddons";
    private static ConfusingAddons instance;

    private EventListener eventListener;
    private final ApiUtil apiUtil = new ApiUtil(this);
    public Logger logger = LogManager.getLogger(MODID);
    private ConfusingHypixelAPI API;
    private ConfigValues configValues;
    private String apiKey = "";
    private RuntimeInfo runtimeInfo = new RuntimeInfo();
    private final BazaarCalculator bazaarCalculator = new BazaarCalculator(this);
    private ChatMessageListener chatMessageListener;
    private LiveGcConnectionManager liveGcConnectionManager = new LiveGcConnectionManager();

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
        WarpCommand warpCommand;
        MsgCommand msgCommand;
        ClientCommandHandler.instance.registerCommand(new ConfusingAddonsCommand(this));
        ClientCommandHandler.instance.registerCommand(new GChatCommand());
        ClientCommandHandler.instance.registerCommand(warpCommand = new WarpCommand());
        ClientCommandHandler.instance.registerCommand(msgCommand = new MsgCommand());
        ClientCommandHandler.instance.registerCommand(new WAliasCommand(warpCommand, msgCommand));

        Thread updateCheckerThread = new Thread(() -> {
            JsonObject runtimeInfoJSON = apiUtil.getRuntimeInfo();
            runtimeInfo = new RuntimeInfo(runtimeInfoJSON.get("downloadURL").getAsString(),
                    runtimeInfoJSON.get("liveGCIP").getAsString(),
//                    "127.0.0.1",
                    runtimeInfoJSON.get("latestVersion").getAsString(),
                    runtimeInfoJSON.get("directDownloadURL").getAsString());
            bazaarCalculator.reload();
            MinecraftForge.EVENT_BUS.register(chatMessageListener = new ChatMessageListener());
        });
        updateCheckerThread.start();

        MinecraftForge.EVENT_BUS.register(eventListener = new EventListener(this));

        Feature.initFeatures();
        configValues.loadConfig();
    }

    public EventListener getEventListener() {
        return eventListener;
    }

    public ChatMessageListener getChatMessageListener() {
        if (chatMessageListener == null) { // Reload cache
            synchronized (this) {
                return chatMessageListener;
            }
        }
        return chatMessageListener;
    }

    public ApiUtil getApiUtil() {
        return apiUtil;
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
        getApiUtil().clearCaches();
    }

    public void resetAPIKey() {
        API = null;
    }

    public RuntimeInfo getRuntimeInfo() {
        if (runtimeInfo == null) { // Reload cache
            synchronized (this) {
                return runtimeInfo;
            }
        }
        return runtimeInfo;
    }

    public BazaarCalculator getBazaarCalculator() {
        return bazaarCalculator;
    }

    public LiveGcConnectionManager getLiveGcConnectionManager() {
        return liveGcConnectionManager;
    }

    public void resetLiveGcConnectionManager() {
        try {
            if (liveGcConnectionManager.liveGCSocket != null) liveGcConnectionManager.liveGCSocket.close();
            if (liveGcConnectionManager.dataInputStream != null) liveGcConnectionManager.dataInputStream.close();
            liveGcConnectionManager.terminateThread.set(true);
        } catch (IOException ignored) {}
        liveGcConnectionManager = new LiveGcConnectionManager();
    }
}