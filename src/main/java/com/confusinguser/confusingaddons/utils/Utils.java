package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {

    private static final Pattern mvpPlusJoinMsgRegex = Pattern.compile("§b\\[MVP§[a-f1-9]\\+§b] \\w{3,16}§f§6 joined the lobby!"); // §b[MVP§9+§b] ConfusingUser§f§6 joined the lobby!
    private static final Pattern mvpPlusPlusJoinMsgRegex = Pattern.compile(" §b>§c>§a> §[0-9a-f]\\[MVP§[0-9a-f]\\+\\+§[0-9a-f]] \\w{3,16}§f§6 joined the lobby! §a<§c<§b<"); //  §b>§c>§a> §6[MVP§9++§6] ConfusingUser§f§6 joined the lobby! §a<§c<§b<
    private static final Pattern playerFoundItemInMysteryBoxRegex = Pattern.compile("§b\\[Mystery Box] (?:|§b)§f§[0-9a-f]\\w{3,16} §ffound a §[0-9a-f].*§f!"); // §b[Mystery Box] §f§aConfusingUser §ffound a §6Legendary Easter Egg Cloak§f!
    private static final Pattern playerFoundMysteryBoxRegex = Pattern.compile("§[0-9a-f]\\w{3,16} §ffound a §e.{4}(?:§7|). §bMystery Box§f!"); // §7ConfusingUser §ffound a §e????? §bMystery Box§f!
    private static final Pattern joinLeaveMessageRegex = Pattern.compile("(?:§2Guild|§aFriend) > §[0-9a-f]\\w{3,16} (?:§e|)(?:left|joined)\\."); // §aFriend > §aConfusingUser §eleft. || §2Guild > §aConfusingUser §eleft.
    private static final Pattern gameAdRegex = Pattern.compile("§b. (?:A|An) §[0-9a-f]§l[a-zA-Z0-9() ]+§[0-9a-f] game is (?:available to join|starting in 30 seconds)! §[0-9a-f]§lCLICK HERE§b to join!"); // §b? A §e§lGalaxy Wars§b game is available to join! §6§lCLICK HERE§b to join!
    private static final Pattern queueTitleRegex = Pattern.compile("");
    private static final Pattern megaLobbyRegex = Pattern.compile("\\d\\d/\\d\\d/\\d\\d (?:M|mega)\\d{1,4}\\w");
    private static final Pattern apiKeyUpdateRegex = Pattern.compile("§aYour new API key is §b[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}"); // §aYour new API key is §b77c1b199-e508-4038-a5cd-35a6069906fa
    private static final Pattern timestampAddZeroToHoursRegex = Pattern.compile(" \\d:");
    private static final Pattern stripWeirdCharsRegex = Pattern.compile("[^a-zA-Z-0-9/ ]");
    private static final String batphoneButtonMessage = "§2§l[OPEN MENU]";
    private static final String slayerBossSlainMessage = "§6§lNICE! SLAYER BOSS SLAIN!";

    // private ConfusingAddons main;
    private final String USER_AGENT = "Mozilla/5.0";
    private final Map<Integer, Map.Entry<Runnable, Integer>> scheduleQueue = new HashMap<>();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy KK:mm aa");
    DateFormat dateFormatOutput = new SimpleDateFormat("dd MMM yy 'at' KK:mm aa");
    private int scheduleId = 0;
    /*private Field keyDownBuffer_ref;
    private Field readBuffer_ref;*/

    public Utils(ConfusingAddons main) {
        // this.main = main;
    }

    public boolean interpretBooleanString(String input) {
        input = input.replace("on", "true").replace("off", "false");
        return Boolean.parseBoolean(input);
    }

    public void sendMessageToPlayer(String message, EnumChatFormatting color) {
        Minecraft.getMinecraft().getNetHandler().handleChat(new S02PacketChat(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(color))));
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

    public boolean isQueueTitle(String title) {
        return true;
    }

    public long calculateETA(int queuePos, long start, long end) {
        long timeForOneQueue = end - start;
        return timeForOneQueue * queuePos;
    }

    public String makeETAString(long etaMillis) {
        return null;
    }

    public boolean isApiKeyUpdateMessage(String message) {
        return apiKeyUpdateRegex.matcher(message).matches();
    }

    public Map.Entry<String, UUID> getUUIDByUsername(String username) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            return null;
        }
        try {
            JsonObject responseJson = new JsonParser().parse(response.toString()).getAsJsonObject();
            return new AbstractMap.SimpleImmutableEntry<>(responseJson.get("name").getAsString(), UUID.fromString(responseJson.get("id").getAsString().replaceAll("([0-9a-f]{8})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]+)", "$1-$2-$3-$4-$5")));
        } catch (JsonParseException | IllegalStateException e) {
            return null;
        }
    }

    public MovingObjectPosition rayTraceWithLiquid(Entity entity, double blockReachDistance, float partialTicks) {
        Vec3 vec3 = entity.getPositionEyes(partialTicks);
        Vec3 vec31 = entity.getLook(partialTicks);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return entity.worldObj.rayTraceBlocks(vec3, vec32, true, false, true);
    }

    public double distanceToCenterPlaneBlock(BlockPos blockPos, Vec3 to) {
        double d0 = (double) blockPos.getX() + 0.5D - to.xCoord;
        double d2 = (double) blockPos.getZ() + 0.5D - to.zCoord;
        return Math.sqrt(d0 * d0 + d2 * d2);
    }

    public void delayRunnableForXTicks(Runnable runnable, int ticks) {
        for (Map.Entry<Integer, Map.Entry<Runnable, Integer>> entry : scheduleQueue.entrySet()) {
            if (entry.getValue().getKey().equals(runnable)) {
                return;
            }
        }
        scheduleQueue.put(++scheduleId, new AbstractMap.SimpleEntry<>(runnable, ticks));
    }

    public void tickQueue() {
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Map.Entry<Runnable, Integer>> entry : scheduleQueue.entrySet()) {
            entry.getValue().setValue(entry.getValue().getValue() - 1);
            scheduleQueue.put(entry.getKey(), entry.getValue());
            if (entry.getValue().getValue() <= 0) {
                toRemove.add(entry.getKey());
                entry.getValue().getKey().run();
            }
        }
        for (Integer id : toRemove)
            scheduleQueue.remove(id);
    }

    public boolean isSlayerBossSlainMessage(String message) {
        return message.contains(slayerBossSlainMessage);
    }

    public String getCommandFromBatphoneMessage(IChatComponent message) {
        if (message.getFormattedText().replace("§r", "").contains(batphoneButtonMessage)) {
            List<IChatComponent> siblings = new ArrayList<>();
            siblings.add(message);
            siblings.addAll(message.getSiblings());

            for (IChatComponent sibling : siblings) {
                if (sibling.getChatStyle().getChatClickEvent() != null && sibling.getChatStyle().getChatClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND)
                    return sibling.getChatStyle().getChatClickEvent().getValue();
            }
        }
        return null;
    }

    public boolean playerCurrentlyFightingBoss() {
        return Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveNames().stream().anyMatch(objective -> objective.contains("Slay the boss!"));
    }

    public String formatTimestamp(String timestamp) {
        try {
            return dateFormatOutput.format(dateFormat.parse(timestamp));
        } catch (ParseException ignored) {
            return null;
        }
    }

    public void handleInvalidApiKey() {
        sendMessageToPlayer("Your API key was invalid and was removed\n§cTo generate a new one, type §b'/api new'", EnumChatFormatting.RED);
        ConfusingAddons.getInstance().resetAPIKey();
    }

    public String getSystemClipboardContents() {
        try {
            Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            return clipboard == null ? "" : (String) clipboard.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return "";
        }
    }

    public boolean isKeyOrMouseButtonDown(int keyOrButtonCode) {
        try {
            return Keyboard.isKeyDown(keyOrButtonCode);
        } catch (IndexOutOfBoundsException ex) {
            try {
                return Mouse.isButtonDown(keyOrButtonCode);
            } catch (IndexOutOfBoundsException exception) {
                ConfusingAddons.getInstance().logger.error(exception);
                return false;
            }
        }
    }

    public boolean isInAPrivateMega() {
        Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebarObjective != null) {
            for (Score score : scoreboard.getSortedScores(sidebarObjective)) {
                String playerName = score.getPlayerName();
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(playerName);
                System.out.println(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName()));
                if (megaLobbyRegex.matcher(stripWeirdChars(StringUtils.stripControlCodes(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName())))).matches()) {
                    if (Minecraft.getMinecraft().theWorld.playerEntities.stream().filter(player -> player.getUniqueID().version() == 4).count() < 20) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInAnEmptyLobby() {
        return Minecraft.getMinecraft().theWorld.playerEntities.stream().filter(player -> player.getUniqueID().version() == 4).count() < 10;
    }

    public String stripWeirdChars(String input) {
        return stripWeirdCharsRegex.matcher(input).replaceAll("");
    }

    public boolean showMessageOnRightSide(IChatComponent message) {
        return true;
    }

    public boolean isRairityLine(String line) {
        return line.contains("COMMON") || line.contains("UNCOMMON") || line.contains("RARE") || line.contains("EPIC") || line.contains("LEGENDARY") || line.contains("COMMON");
    }

    /*public void setKeyState(int keyCode, boolean keyState) {
        if (keyDownBuffer_ref == null) {
            try {
                keyDownBuffer_ref = Keyboard.class.getDeclaredField("keyDownBuffer");
                keyDownBuffer_ref.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (readBuffer_ref == null) {
            try {
                readBuffer_ref = Keyboard.class.getDeclaredField("readBuffer");
                readBuffer_ref.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            ByteBuffer keyDownBuffer = (ByteBuffer) keyDownBuffer_ref.get(null);
            int old_position = keyDownBuffer.position();
            keyDownBuffer.put(keyCode, keyState ? (byte) 1 : (byte) 0);
            keyDownBuffer.position(old_position);
        } catch (IllegalAccessException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        try {
            ByteBuffer readBuffer = (ByteBuffer) readBuffer_ref.get(null);
            ByteBuffer tmp_event = BufferUtils.createByteBuffer(18);
            tmp_event.putInt(keyCode).put(keyState ? (byte) 1 : (byte) 0).putInt(keyCode).putLong(100 * 1000000).put((byte) 0);
            tmp_event.flip();
            readBuffer.clear();
            readBuffer.put(tmp_event);
        } catch (IllegalAccessException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }*/
}
