package com.confusinguser.confusingaddons;

import org.apache.logging.log4j.Logger;

import com.confusinguser.confusingaddons.commands.HideJoinLeaveMessages;
import com.confusinguser.confusingaddons.commands.HideLobbySpam;
import com.confusinguser.confusingaddons.commands.ShowClickCommandsCommand;
import com.confusinguser.confusingaddons.listeners.PlayerListener;
import com.confusinguser.confusingaddons.utils.ConfigValues;
import com.confusinguser.confusingaddons.utils.Util;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ConfusingAddons.MODID, version = ConfusingAddons.VERSION, name = ConfusingAddons.MOD_NAME, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class ConfusingAddons {
	public static final String MODID = "confusingaddons";
	public static final String VERSION = "1.0";
	public static final String MOD_NAME = "ConfusingAddons";

	private PlayerListener playerListener = new PlayerListener(this);
	private Util util = new Util(this);
	private ConfigValues configValues;
	public Logger logger;
	private static ConfusingAddons instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		instance = this;
		configValues = new ConfigValues(this, e.getSuggestedConfigurationFile());
		logger = e.getModLog();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(playerListener);
		ClientCommandHandler.instance.registerCommand(new ShowClickCommandsCommand(this));
		ClientCommandHandler.instance.registerCommand(new HideLobbySpam(this));
		ClientCommandHandler.instance.registerCommand(new HideJoinLeaveMessages(this));
	}

	public static ConfusingAddons getInstance() {
		return instance;
	}

	public PlayerListener getPlayerListener() {
		return playerListener;
	}

	public Util getUtil() {
		return util;
	}

	public ConfigValues getConfigValues() {
		return configValues;
	}
}