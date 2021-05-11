package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.utils.RegexUtil;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatMessageListener {

    private static final ClickEvent.Action[] allowedActions = {ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND};

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConfusingAddons main = ConfusingAddons.getInstance();

    @SubscribeEvent(priority = EventPriority.LOWEST) // Go around stuff modifying the message w/ ex timestamps
    public void onChatMessageLowPrio(ClientChatReceivedEvent event) {
        if (RegexUtil.getMatcher("§2Guild > .+", event.message.getFormattedText()).find()) {
            main.getLiveGcConnectionManager().sendQueue.offer(event);
            if (RegexUtil.stringMatches("§2Guild > (?:§[0-9a-f]\\[[\\w§]+\\] |)\\w{3,16}(?: §3\\[\\w*\\]|)§f: Discord > .*: .*", event.message.getFormattedText())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type != 2) {
            if (Feature.isEnabled("SHOW_CLICK_COMMANDS")) {
                @SuppressWarnings("unchecked")
                List<IChatComponent> siblings = (ArrayList<IChatComponent>) ((ArrayList<IChatComponent>) event.message.getSiblings()).clone();
                siblings.add(event.message);

                for (IChatComponent sibling : siblings) {
                    HoverEvent hoverEvent = sibling.getChatStyle().getChatHoverEvent();
                    ClickEvent clickEvent = sibling.getChatStyle().getChatClickEvent();
                    if (clickEvent != null && Arrays.asList(allowedActions).contains(clickEvent.getAction())) {
                        if (hoverEvent == null) {
                            sibling.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§8" + clickEvent.getValue())));
                        } else if (!hoverEvent.getValue().getUnformattedText().contains(clickEvent.getValue())) {
                            sibling.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEvent.getValue().appendText("\n§8" + clickEvent.getValue())));
                        }
                    }
                }
            }

            if (Feature.isEnabled("HIDE_LOBBY_SPAM") && Utils.isLobbySpam(event.message.getFormattedText()) ||
                    Feature.isEnabled("HIDE_JOIN_LEAVE_MESSAGES") && Utils.isJoinLeaveMessage(event.message.getFormattedText())) {
                event.setCanceled(true);
            }

            // If api key updated in game
            if (Utils.isApiKeyUpdateMessage(event.message.getFormattedText().replace("§r", ""))) {
                main.setApiKey(event.message.getFormattedText().replace("§r", "").substring(24));
            }

            main.logger.info(event.message.getFormattedText().replace("§r", ""));
        }
    }
}