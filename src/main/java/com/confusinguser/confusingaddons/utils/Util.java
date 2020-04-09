package com.confusinguser.confusingaddons.utils;

import java.util.regex.Pattern;

import com.confusinguser.confusingaddons.ConfusingAddons;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;

public class Util {

	private ConfusingAddons main;
	
	private static final Pattern mvpPlusJoinMsgRegex = Pattern.compile("§b\\[MVP§[a-f1-9]\\+§b] \\w{3,16}§f§6 joined the lobby!"); // §b[MVP§9+§b] ConfusingUser§f§6 joined the lobby!
	private static final Pattern mvpPlusPlusJoinMsgRegex = Pattern.compile(" §b>§c>§a> §[0-9a-f]\\[MVP§[0-9a-f]\\+\\+§[0-9a-f]\\] \\w{3,16}§f§6 joined the lobby! §a<§c<§b<"); //  §b>§c>§a> §6[MVP§9++§6] ConfusingUser§f§6 joined the lobby! §a<§c<§b<
	private static final Pattern playerFoundItemInMysteryBoxRegex = Pattern.compile("§b\\[Mystery Box\\] (?:|§b)§f§[0-9a-f]\\w{3,16} §ffound a §[0-9a-f].*§f!"); // [Mystery Box] §f§aConfusingUser §ffound a §6Legendary Easter Egg Cloak§f!
	private static final Pattern playerFoundMysteryBoxRegex = Pattern.compile("§[0-9a-f]\\w{3,16} §ffound a §e.{4}(?:§7|). §bMystery Box§f!"); // §7ConfusingUser §ffound a §e????? §bMystery Box§f!
	private static final Pattern joinLeaveMessageRegex = Pattern.compile("§e\\w{3,16} (?:left|joined)\\."); // §eConfusingUser left.
	private static final Pattern gameAdRegex = Pattern.compile("§b. (?:A|An) §[0-9a-f]§l[a-zA-Z0-9() ]+§[0-9a-f] game is (?:available to join|starting in 30 seconds)! §[0-9a-f]§lCLICK HERE§b to join!"); // §b? A §e§lGalaxy Wars§b game is available to join! §6§lCLICK HERE§b to join!
	
	public Util(ConfusingAddons main) {
		this.main = main;
	}
	
	public boolean interpretBooleanString(String input) {
		input = input.replace("on", "true").replace("off", "false");
		return Boolean.valueOf(input);
	}
	
	public void sendMessageToPlayer(String message) {
		Minecraft.getMinecraft().getNetHandler().handleChat(new S02PacketChat(new ChatComponentText(message)));
	}
	
	public boolean isLobbySpam(String message) {
		message = message.replace("§r", "");
		return mvpPlusJoinMsgRegex.matcher(message).matches() || 
				mvpPlusPlusJoinMsgRegex.matcher(message).matches() || 
				playerFoundItemInMysteryBoxRegex.matcher(message).matches() || 
				playerFoundMysteryBoxRegex.matcher(message).matches() ||
				gameAdRegex.matcher(message).matches();
	}
	
	public boolean isJoinLeaveMessage(String message) {
		message = message.replace("§r", "");
		return joinLeaveMessageRegex.matcher(message).matches();
	}
}
