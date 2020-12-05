package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.asm.hooks.EntityRendererHook;
import com.confusinguser.confusingaddons.gui.BazaarGui;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class EventListener {

    private final ConfusingAddons main;

    public int speedBridgeSecurity;
    public int leftCPS;
    public int rightCPS;

    Minecraft mc = Minecraft.getMinecraft();

    public int tickCounter = 0;

    private GuiScreen guiToOpen;

    public EventListener(ConfusingAddons main) {
        this.main = main;
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

    @SubscribeEvent
    public void onJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        if (ConfusingAddons.getInstance().getRuntimeInfo().shouldSendUpdateNotification()) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] §l§bA new update is available!"));
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] New Version: §d" + main.getRuntimeInfo().getVersion() + "§b   Current Version: §d" + ConfusingAddons.VERSION));
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] §6--------------------------------------------"));
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                    new ChatComponentText("[Download + Changelog]")
                            .setChatStyle(new ChatStyle()
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§l§eClick!")))
                                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, main.getRuntimeInfo().getDownloadURL())))


                            .appendSibling(
                                    new ChatComponentText(" ")
                                            .appendSibling(new ChatComponentText("[Direct Download]")
                                                    .setChatStyle(new ChatStyle()
                                                            .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§l§eClick!")))
                                                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, main.getRuntimeInfo().getDirectDownloadURL()))))));
            main.getRuntimeInfo().setSendUpdateNotification(false);
        }
    }

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest && ((GuiChest) event.gui).inventorySlots instanceof ContainerChest && 
                ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory().getName().startsWith("Bazaar")) {
            event.gui = new BazaarGui(mc.thePlayer.inventory, ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory());
        }
    }

    public void setGuiToOpen(GuiScreen guiToOpen) {
        this.guiToOpen = guiToOpen;
    }
}