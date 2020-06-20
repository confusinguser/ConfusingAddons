package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.Slot;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventListener {

    public final List<String> deleteLater = new ArrayList<>();

    private final ConfusingAddons main;
    private final ClickEvent.Action[] allowedActions = {Action.RUN_COMMAND, Action.SUGGEST_COMMAND};
    Minecraft mc = Minecraft.getMinecraft();
    int autoClickerTickCounter = 0;
    public int speedBridgeSecurity;
    public int leftCPS;
    public int rightCPS;


    public EventListener(ConfusingAddons main) {
        this.main = main;
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
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
                    } else {
                        sibling.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEvent.getValue().appendText("\n§8" + clickEvent.getValue())));
                    }
                }
            }
        }
        if (event.type == 0) {
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
                    mc.thePlayer.inventory.currentItem = batphoneSlotId > 0 && batphoneSlotId < 9 ? batphoneSlotId : mc.thePlayer.inventory.currentItem;
                }
            }

            if (Feature.AUTO_OPEN_MADDOX_GUI.isEnabled() && !main.getUtils().playerCurrentlyFightingBoss() && main.getUtils().getCommandFromBatphoneMessage(event.message) != null) {
                mc.thePlayer.sendChatMessage("/cb " + main.getUtils().getCommandFromBatphoneMessage(event.message).substring(4));
            }

            main.logger.info(event.message.getFormattedText().replace("§r", ""));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.theWorld == null || mc.thePlayer == null) return;

        Vec3 playerPos = mc.thePlayer.getPositionVector();
        if (main.keyBindings[1].isKeyDown() && (main.getUtils().distanceToCenterPlaneBlock(new BlockPos(playerPos).down(), playerPos) > (10 - speedBridgeSecurity) / 20d || mc.theWorld.getBlockState(new BlockPos(playerPos).down()).getBlock().getMaterial() == Material.air) && mc.currentScreen == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }

        if (main.keyBindings[0].isKeyDown()) {
            if (++autoClickerTickCounter % 2 == 0 && mc.thePlayer.getHeldItem() != null
                    && (mc.thePlayer.getHeldItem().getItem().getRegistryName().equals("minecraft:bucket") || mc.thePlayer.getHeldItem().getItem().getRegistryName().equals("minecraft:water_bucket"))) {
                Material blockInFrontOfLookingAtMaterialLiquid = mc.theWorld.getBlockState(main.getUtils().rayTraceWithLiquid(mc.thePlayer, mc.playerController.getBlockReachDistance(), 1).getBlockPos()
                        .add(mc.objectMouseOver.sideHit.getDirectionVec()))
                        .getBlock()
                        .getMaterial();

                Material blockLookingAtMaterialLiquid = mc.theWorld.getBlockState(main.getUtils().rayTraceWithLiquid(mc.thePlayer, mc.playerController.getBlockReachDistance(), 1).getBlockPos())
                        .getBlock()
                        .getMaterial();

                Material blockLookingAtMaterial = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()
                        .add(mc.objectMouseOver.sideHit.getDirectionVec()))
                        .getBlock()
                        .getMaterial();

                if (!mc.thePlayer.onGround && !mc.thePlayer.isInWater() && blockInFrontOfLookingAtMaterialLiquid == Material.air && mc.thePlayer.fallDistance >= 3) // Place water
                    mc.rightClickMouse();
                else if (mc.thePlayer.getHeldItem().getItem().getRegistryName().equals("minecraft:bucket") && mc.thePlayer.onGround && mc.thePlayer.isInWater() && blockLookingAtMaterialLiquid == Material.water) // Pick up water
                    mc.rightClickMouse();
            }
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown() && !Keyboard.isKeyDown(mc.gameSettings.keyBindUseItem.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        if (mc.gameSettings.keyBindAttack.isKeyDown() && !Keyboard.isKeyDown(mc.gameSettings.keyBindAttack.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }

        if (main.keyBindings[2].isKeyDown() && autoClickerTickCounter % (20d / leftCPS) == 0) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
            mc.clickMouse();
        }

        if (!main.keyBindings[2].isKeyDown() && main.keyBindings[3].isKeyDown() && autoClickerTickCounter % (20d / rightCPS) == 0) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            mc.rightClickMouse();
        }

        autoClickerTickCounter++;
    }

    @SubscribeEvent
    public void onGuiClick(GuiScreenEvent.MouseInputEvent event) {
        if (Feature.COPY_NBT.isEnabled() && Mouse.isButtonDown(0) && Mouse.getEventButton() == 0 && event.gui instanceof GuiContainer) {
            Slot theSlot = ((GuiContainer) event.gui).theSlot;
            if (theSlot != null && theSlot.getHasStack() && ((GuiContainer) event.gui).draggedStack == null && !main.getUtils().getSystemClipboardContents().equals(theSlot.getStack().serializeNBT().toString())) {
                StringSelection newClipboard = new StringSelection(theSlot.getStack().serializeNBT().toString());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(newClipboard, newClipboard);
                main.getUtils().sendMessageToPlayer(String.format("Copied NBT of item §r%s§r§a to clipboard", theSlot.getStack().getChatComponent().getFormattedText()), EnumChatFormatting.GREEN);
                event.setCanceled(true);
            }
        }
    }

/*    @SubscribeEvent
    public void onTitleMessage(RenderGameOverlayEvent event) {
        String title = "";
        try {
            title = (String) main.getReflectionUtils().getField(mc.ingameGUI.getClass(), "displayedTitle").get(mc.ingameGUI);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
            main.logger.catching(e);
        }

        if (main.getUtils().isQueueTitle(title)) {
            int queuePos = Integer.parseInt(title.substring(0, 0)) / 100; // Because it updates 100 positions at a time
            try {
                main.getReflectionUtils().getField(mc.ingameGUI.getClass(), "displayedSubTitle").set(mc.ingameGUI, "§7" + main.getUtils().makeETAString(main.getUtils().calculateETA(queuePos, 0, 0)));
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
                main.logger.catching(e);
            }
        }

        // main.getUtils().sendMessageToPlayer(title);
        // mc.ingameGUI.displayTitle(title, "yes", 1, 10, 1);
    }*/
}