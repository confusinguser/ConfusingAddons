package com.confusinguser.confusingaddons.listeners;

import java.util.ArrayList;
import java.util.Arrays;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerListener {

	ConfusingAddons main;

	ClickEvent.Action[] allowedActions = {Action.RUN_COMMAND, Action.SUGGEST_COMMAND};

	public PlayerListener(ConfusingAddons main) {
		this.main = main;
	}

	@SubscribeEvent
	public void onChatMessage(ClientChatReceivedEvent event) {
		if (Feature.SHOW_CLICK_COMMANDS.getStatus()) {
			@SuppressWarnings("unchecked")
			ArrayList<IChatComponent> siblings = (ArrayList<IChatComponent>) ((ArrayList<IChatComponent>) event.message.getSiblings()).clone();
			siblings.add(event.message);

			for (IChatComponent sibling : siblings) {
				HoverEvent hoverEvent = sibling.getChatStyle().getChatHoverEvent();
				ClickEvent clickEvent = sibling.getChatStyle().getChatClickEvent();
				if (clickEvent != null && Arrays.asList(allowedActions).contains(clickEvent.getAction())) {
					if (hoverEvent == null) {
						sibling.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§8" + clickEvent.getValue())));
					} else {
						sibling.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEvent.getValue().appendText("\n§8" + clickEvent.getValue())));
					}
				}
			}
		}
		if (Feature.HIDE_LOBBY_SPAM.getStatus() && main.getUtil().isLobbySpam(event.message.getFormattedText()) || 
				Feature.HIDE_JOIN_LEAVE_MESSAGES.getStatus() && main.getUtil().isJoinLeaveMessage(event.message.getFormattedText())) {
			event.setCanceled(true);
		}
		main.logger.info(event.message.getFormattedText().replace("§r", ""));
	}
}