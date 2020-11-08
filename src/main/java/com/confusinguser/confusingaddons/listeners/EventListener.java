package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.asm.hooks.EntityRendererHook;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventListener {

    private final ConfusingAddons main;
    private final ClickEvent.Action[] allowedActions = {Action.RUN_COMMAND, Action.SUGGEST_COMMAND};

    public int speedBridgeSecurity;
    public int leftCPS;
    public int rightCPS;

    Minecraft mc = Minecraft.getMinecraft();

    public int tickCounter = 0;

    private boolean releaseUseItemButtonNextTick;
    private boolean releaseAttackButtonNextTick;

    GuiScreen guiToOpen;

    public EventListener(ConfusingAddons main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
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

    @SubscribeEvent
    public void onGuiClick(GuiScreenEvent.KeyboardInputEvent event) {
        if (Feature.COPY_NBT.isEnabled() && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.getEventKey() == Keyboard.KEY_RCONTROL && event.gui instanceof GuiContainer) {
            Slot theSlot = ((GuiContainer) event.gui).getSlotUnderMouse();
            if (theSlot != null && theSlot.getHasStack() && ((GuiContainer) event.gui).draggedStack == null && !main.getUtils().getSystemClipboardContents().equals(theSlot.getStack().serializeNBT().toString())) {
                StringSelection newClipboard = new StringSelection(theSlot.getStack().serializeNBT().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(newClipboard, newClipboard);
                main.getUtils().sendMessageToPlayer(String.format("Copied NBT of item §r%s§r§a to clipboard", theSlot.getStack().getChatComponent().getFormattedText()), EnumChatFormatting.GREEN);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityRendererHook.lastEntityThatAttacked = event.source.getEntity();
        }
    }


    /*@SubscribeEvent
    public void onRenderChat(RenderGameOverlayEvent.Chat event) {
        /*for (int i = rightsideMessages.size(); i > 0; i--) {
            ChatMessage chatMessage = rightsideMessages.get(rightsideMessages.size() - i);
            long messageAge = tickCounter - chatMessage.getCreationTime();

            int xOffset = 0;
            if (messageAge <= 30) {
                xOffset = (int) (Math.sin(Math.toRadians(messageAge / 30d * 90 + 90)) * mc.fontRendererObj.getStringWidth(chatMessage.getChatComponent().getUnformattedText()) * 2 + 20);
            }

            int x = mc.displayWidth - mc.fontRendererObj.getStringWidth(chatMessage.getChatComponent().getUnformattedText()) * 2 - 20 + xOffset;
            int y = mc.displayHeight - 20 - (30 * i);
            mc.fontRendererObj.drawString(chatMessage.getChatComponent().getFormattedText(), x / 2f, y / 2f, 0xFFFFFF, false);
        }
    }*/

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }
    }

    public void setGuiToOpen(GuiScreen guiToOpen) {
        this.guiToOpen = guiToOpen;
    }
}