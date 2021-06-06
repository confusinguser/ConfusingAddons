package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.asm.hooks.EntityRendererHook;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.gui.BazaarGui;
import com.confusinguser.confusingaddons.utils.Multithreading;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.concurrent.TimeUnit;

import static com.confusinguser.confusingaddons.ConfusingAddons.VERSION;

public class EventListener {

    private final ConfusingAddons main;

    Minecraft mc = Minecraft.getMinecraft();

    public int tickCounter = 0;

    private GuiScreen guiToOpen;
    private boolean displayActiveLastFrame;
    private int realFpsLimit = mc.gameSettings.limitFramerate;
    private boolean updateNotifSent = false;

    public EventListener(ConfusingAddons main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onGuiClick(GuiScreenEvent.KeyboardInputEvent event) {
        if (Feature.isEnabled("COPY_NBT") && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) && Keyboard.getEventKey() == Keyboard.KEY_RCONTROL && event.gui instanceof GuiContainer) {
            Slot theSlot = ((GuiContainer) event.gui).getSlotUnderMouse();
            if (theSlot != null && theSlot.getHasStack() && ((GuiContainer) event.gui).draggedStack == null && !Utils.getSystemClipboardContents().equals(theSlot.getStack().serializeNBT().toString())) {
                StringSelection newClipboard = new StringSelection(theSlot.getStack().serializeNBT().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(newClipboard, newClipboard);
                Utils.sendMessageToPlayer(String.format("Copied NBT of item §r%s§r§a to clipboard", theSlot.getStack().getChatComponent().getFormattedText()), EnumChatFormatting.GREEN);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP) {
            EntityRendererHook.lastAttacker = event.source.getEntity();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (guiToOpen != null) {
            Minecraft.getMinecraft().displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }

//        if (tickCounter % 2 == 0) {
//            if (!Display.isActive() && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
//                if (displayActiveLastFrame) {
//                    realFpsLimit = mc.gameSettings.limitFramerate;
//                    mc.gameSettings.limitFramerate = 5;
//                }
//            } else {
//                if (!displayActiveLastFrame)
//                    mc.gameSettings.limitFramerate = realFpsLimit;
//            }
//            displayActiveLastFrame = Display.isActive();
//        }
//        tickCounter++;
    }

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.gui instanceof GuiChest && ((GuiChest) event.gui).inventorySlots instanceof ContainerChest &&
                ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory().getName().startsWith("Bazaar")) {
            event.gui = new BazaarGui(mc.thePlayer.inventory, ((ContainerChest) ((GuiChest) event.gui).inventorySlots).getLowerChestInventory());
        }
    }

    @SubscribeEvent
    public void onLeaveServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Multithreading.scheduleOnce(() -> {
            Utils.refreshHypixelJoinment();
            main.resetLiveGcConnectionManager();
        }, 2, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!VERSION.equals(main.getRuntimeInfo().getLatestVersion()) && !updateNotifSent) {
            Multithreading.scheduleOnce(Utils::sendUpdateNotification,
                    2, TimeUnit.SECONDS);
            updateNotifSent = true;
        }
        Multithreading.scheduleOnce(() -> {
            Utils.refreshHypixelJoinment();
            main.resetLiveGcConnectionManager();
        }, 5, TimeUnit.SECONDS);
    }

    @SubscribeEvent
    public void onPlayerUseItem(PlayerUseItemEvent.Start event) { // TODO test this
        BlockPos blockPos = mc.objectMouseOver.getBlockPos()
                .add(mc.thePlayer.getHorizontalFacing().getDirectionVec()).crossProduct(new Vec3i(-1, -1, -1));
        for (Entity entity : mc.theWorld.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(blockPos, blockPos))) {
            if (event.item.getItem() instanceof ItemFishingRod &&
                    (mc.objectMouseOver.entityHit instanceof EntityItemFrame || entity instanceof EntityItemFrame)) {
                event.setCanceled(true);
                Utils.sendMessageToPlayer("Fishing rod might break item frame!", EnumChatFormatting.RED); ///TODO colldownthis
                break;
            }
        }
    }

    public void setGuiToOpen(GuiScreen guiToOpen) {
        this.guiToOpen = guiToOpen;
    }
}