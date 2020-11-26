package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.asm.hooks.EntityRendererHook;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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

    public void setGuiToOpen(GuiScreen guiToOpen) {
        this.guiToOpen = guiToOpen;
    }
}