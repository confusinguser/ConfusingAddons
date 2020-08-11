package com.confusinguser.confusingaddons.listeners;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.ChatMessage;
import com.confusinguser.confusingaddons.utils.Feature;
import com.confusinguser.confusingaddons.utils.ReflectionUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.Slot;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventListener {

    private final ConfusingAddons main;
    private final ClickEvent.Action[] allowedActions = {Action.RUN_COMMAND, Action.SUGGEST_COMMAND};
    private final boolean[] keyBindingsDownLastTick;

    public int speedBridgeSecurity;
    public int leftCPS;
    public int rightCPS;

    Minecraft mc = Minecraft.getMinecraft();

    public int tickCounter = 0;

    private int autoClickerTickCounter = 0;
    private boolean releaseUseItemButtonNextTick;
    private boolean releaseAttackButtonNextTick;
    private final List<ChatMessage> rightsideMessages = new ArrayList<>();

    GuiScreen guiToOpen;

    public EventListener(ConfusingAddons main) {
        this.main = main;
        keyBindingsDownLastTick = new boolean[main.keyBindings.length];
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (event.type != 2 && main.getUtils().showMessageOnRightSide(event.message)) {
            rightsideMessages.add(new ChatMessage(event.message));
            if (rightsideMessages.size() > 3)
                rightsideMessages.remove(0);
        }

        if (event.type == 0) {
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
        } else if (mc.gameSettings.keyBindSneak.isKeyDown() && !main.getUtils().isKeyOrMouseButtonDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
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

        if ((main.keyBindings[2].isKeyDown() && !keyBindingsDownLastTick[2]) || (main.keyBindings[3].isKeyDown() && !keyBindingsDownLastTick[3])) { // Key was pressed this tick
            autoClickerTickCounter = 0;
        }

        if (releaseAttackButtonNextTick) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            releaseAttackButtonNextTick = false;
        }

        if (releaseUseItemButtonNextTick) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            releaseUseItemButtonNextTick = false;
        }

        if (Keyboard.isKeyDown(main.keyBindings[2].getKeyCode()) && autoClickerTickCounter % (20d / leftCPS) == 0) {
            if (mc.currentScreen instanceof GuiContainer) {
                int x = Mouse.getX() * mc.currentScreen.width / mc.displayWidth;
                int y = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height / mc.displayHeight - 1;
                try {
                    ReflectionUtils.getMethod(mc.currentScreen.getClass(), "mouseClicked").invoke(mc.currentScreen, x, y, 0);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                ((GuiContainer) mc.currentScreen).mouseReleased(x, y, 0);
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                releaseAttackButtonNextTick = true;
                mc.clickMouse();
            }
        }

        if (!Keyboard.isKeyDown(main.keyBindings[2].getKeyCode()) && Keyboard.isKeyDown(main.keyBindings[3].getKeyCode()) && autoClickerTickCounter % (20d / rightCPS) == 0) {
            if (mc.currentScreen instanceof GuiContainer) {
                int x = Mouse.getX() * mc.currentScreen.width / mc.displayWidth;
                int y = mc.currentScreen.height - Mouse.getY() * mc.currentScreen.height / mc.displayHeight - 1;
                ((GuiContainer) mc.currentScreen).mouseReleased(x, y, 0);
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                releaseUseItemButtonNextTick = true;
                mc.rightClickMouse();
            }
        }

        autoClickerTickCounter++;

        tickCounter++;

        for (int i = 0; i < main.keyBindings.length; i++) {
            try {
                keyBindingsDownLastTick[i] = main.keyBindings[i].isKeyDown();
            } catch (IndexOutOfBoundsException ignored) {
                break;
            }
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
        }*/
    }

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

    @SubscribeEvent
    @SuppressWarnings("rawtypes")
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
/*        NetworkManager manager = event.manager;
        ChannelPipeline pipeline = manager.channel().pipeline();

        if (manager.isLocalChannel()) {
            pipeline.addLast("confusingaddons_packet_logger_splitter", new SimpleChannelInboundHandler<Packet>() {
                final String prefix = (manager.getDirection() == EnumPacketDirection.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
                    PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                    msg.writePacketData(buf);
                    main.getUtils().sendMessageToPlayer(String.format("%s %s:\n%s", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf)), EnumChatFormatting.WHITE);
                }
            });
            pipeline.addLast("confusingaddons_packet_logger_prepender", new ChannelOutboundHandlerAdapter() {
                final String prefix = (manager.getDirection() == EnumPacketDirection.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    if (msg instanceof Packet) {
                        PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                        ((Packet) msg).writePacketData(buf);
                        main.getUtils().sendMessageToPlayer(String.format("%s %s:\n%s", prefix, msg.getClass().getSimpleName(), ByteBufUtils.getContentDump(buf)), EnumChatFormatting.WHITE);
                    }
                }
            });
        } else {
            pipeline.addLast("confusingaddons_packet_logger_splitter", new MessageDeserializer2() {
                final String prefix = (manager.getDirection() == EnumPacketDirection.SERVERBOUND ? "SERVER: C->S" : "CLIENT: S->C");

                @Override
                protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) {
                    for (Object o : output) {
                        ByteBuf pkt = (ByteBuf) o;
                        main.getUtils().sendMessageToPlayer(String.format("%s:\n%s", prefix, ByteBufUtils.getContentDump(pkt)), EnumChatFormatting.WHITE);
                    }
                }
            });
            pipeline.addLast("confusingaddons_packet_logger_prepender", new MessageSerializer2() {
                final String prefix = (manager.getDirection() == EnumPacketDirection.SERVERBOUND ? "SERVER: S->C" : "CLIENT: C->S");

                @Override
                protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) {
                    main.getUtils().sendMessageToPlayer(String.format("%s:\n%s", prefix, ByteBufUtils.getContentDump(input)), EnumChatFormatting.WHITE);
                }
            });
        }*/
    }
}