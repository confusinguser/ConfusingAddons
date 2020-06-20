package com.confusinguser.confusingaddons;

import com.confusinguser.confusingaddons.commands.ConfusingAddonsCommand;
import com.confusinguser.confusingaddons.commands.SlayerCommand;
import com.confusinguser.confusingaddons.listeners.EventListener;
import com.confusinguser.confusingaddons.utils.ApiUtils;
import com.confusinguser.confusingaddons.utils.ConfigValues;
import com.confusinguser.confusingaddons.utils.ConfusingHypixelAPI;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

@Mod(modid = ConfusingAddons.MODID, version = ConfusingAddons.VERSION, name = ConfusingAddons.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class ConfusingAddons {
    static final String MODID = "ConfusingAddons";
    static final String VERSION = "1.0";
    static final String MOD_NAME = "ConfusingAddons";
    private static ConfusingAddons instance;
    private final EventListener eventListener = new EventListener(this);
    private final Utils utils = new Utils(this);
    private final ApiUtils apiUtils = new ApiUtils(this);
    public Logger logger = LogManager.getLogger(MODID);
    public KeyBinding[] keyBindings;
    private ConfusingHypixelAPI API;
    private ConfigValues configValues;
    private String apiKey = "";

    public static ConfusingAddons getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        instance = this;
        configValues = new ConfigValues(this, e.getSuggestedConfigurationFile());
        configValues.loadConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(eventListener);
        ClientCommandHandler.instance.registerCommand(new SlayerCommand(this));
        ClientCommandHandler.instance.registerCommand(new ConfusingAddonsCommand(this));

        keyBindings = new KeyBinding[]{
                new KeyBinding("MLG Water Bucket", Keyboard.KEY_TAB, ConfusingAddons.MOD_NAME),
                new KeyBinding("Speedbridge", Keyboard.KEY_Z, ConfusingAddons.MOD_NAME),
                new KeyBinding("Left Autoclicker", Keyboard.KEY_M, ConfusingAddons.MOD_NAME),
                new KeyBinding("Right Autoclicker", Keyboard.KEY_NONE, ConfusingAddons.MOD_NAME)
        };
        for (KeyBinding keyBinding : keyBindings)
            ClientRegistry.registerKeyBinding(keyBinding);
    }

    public EventListener getPlayerListener() {
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

    public void disableHypixelAPI() {
        API = null;
    }
}