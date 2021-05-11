package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static final Pattern joinLeaveMessageRegex = Pattern.compile("(?:§2Guild|§aFriend) > §[0-9a-f]\\w{3,16} (?:§e|)(?:left|joined)\\."); // §aFriend > §aConfusingUser §eleft. || §2Guild > §aConfusingUser §eleft.
    private static final Pattern megaLobbyRegex = Pattern.compile("\\d\\d/\\d\\d/\\d\\d (?:M|mega)\\d{1,4}\\w");
    private static final Pattern apiKeyUpdateRegex = Pattern.compile("§aYour new API key is §b[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}"); // §aYour new API key is §b77c1b199-e508-4038-a5cd-35a6069906fa
    private static final Pattern timestampAddZeroToHoursRegex = Pattern.compile(" \\d:");
    private static final Pattern stripWeirdCharsRegex = Pattern.compile("[^a-zA-Z-0-9/ ]");
    private static final String batphoneButtonMessage = "§2§l[OPEN MENU]";
    private static final String slayerBossSlainMessage = "§6§lNICE! SLAYER BOSS SLAIN!";
    //Atomic because needs to be accessed by another thread in real time by LiveGcConnectionManager
    private static AtomicBoolean onHypixel = new AtomicBoolean();

    private static final ConfusingAddons main = ConfusingAddons.getInstance();
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final Map<Integer, Map.Entry<Runnable, Integer>> scheduleQueue = new HashMap<>();
    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy KK:mm aa");
    private static final DateFormat dateFormatOutput = new SimpleDateFormat("dd MMM yyyy 'at' KK:mm aa");
    private static int scheduleId = 0;

    public static boolean interpretBooleanString(String input) {
        input = input.replace("on", "true").replace("off", "false");
        return Boolean.parseBoolean(input);
    }

    public static void sendMessageToPlayer(String message) {
        sendMessageToPlayer(new ChatComponentText(message));
    }

    public static void sendMessageToPlayer(IChatComponent message) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent((byte) 1, fixChatComponentColors(message));
        MinecraftForge.EVENT_BUS.post(event); // Let other mods pick up the new message
        if (!event.isCanceled()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(event.message);
        }
    }

    public static void sendMessageToPlayer(String message, EnumChatFormatting color) {
        sendMessageToPlayer(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(color)));
    }

    public static boolean isLobbySpam(String message) {
        message = message.replace("§r", "");
        return RegexUtil.stringMatches("§b\\[MVP§[a-f0-9]\\+§b] \\w{3,16}§f §6(?:joined|sled into) the lobby!", message) || // §b[MVP§9+§b] ConfusingUser§f §6joined the lobby!
                RegexUtil.stringMatches(" §b>§c>§a> §[0-9a-f]\\[MVP§[0-9a-f]\\+\\+§[0-9a-f]] \\w{3,16}§f §(?:joined|sled into) the lobby! §a<§c<§b<", message) || //  §b>§c>§a> §6[MVP§9++§6] ConfusingUser§f §6joined the lobby! §a<§c<§b<
                RegexUtil.stringMatches("§b\\[Mystery Box] (?:|§b)§f§[0-9a-f]\\w{3,16} §ffound a §[0-9a-f].*§f!", message) || // §b[Mystery Box] §f§aConfusingUser §ffound a §6Legendary Easter Egg Cloak§f!
                RegexUtil.stringMatches("§[0-9a-f]\\w{3,16} §ffound a §e.{4}(?:§7|). §bMystery Box§f!", message) || // §7ConfusingUser §ffound a §e????? §bMystery Box§f!
                RegexUtil.stringMatches("§b. (?:A|An) §[0-9a-f]§l[a-zA-Z0-9() ]+§[0-9a-f] game is (?:available to join|starting in 30 seconds)! §[0-9a-f]§lCLICK HERE§b to join!", message); // §b? A §e§lGalaxy Wars§b game is available to join! §6§lCLICK HERE§b to join!)
    }

    public static boolean isJoinLeaveMessage(String message) {
        return RegexUtil.stringMatches("(?:§2Guild|§aFriend) > §[0-9a-f]\\w{3,16} (?:§e|)(?:left|joined)\\.", message.replace("§r", "")); // §aFriend > §aConfusingUser §eleft. || §2Guild > §aConfusingUser §eleft.
    }

    public static boolean isApiKeyUpdateMessage(String message) {
        return apiKeyUpdateRegex.matcher(message).matches();
    }

    public static Map.Entry<String, UUID> getUUIDByUsername(String username) {
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

    public static MovingObjectPosition rayTraceWithLiquid(Entity entity, double blockReachDistance, float partialTicks) {
        Vec3 vec3 = entity.getPositionEyes(partialTicks);
        Vec3 vec31 = entity.getLook(partialTicks);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return entity.worldObj.rayTraceBlocks(vec3, vec32, true, false, true);
    }

    public static double distanceToCenterPlaneBlock(BlockPos blockPos, Vec3 to) {
        double d0 = (double) blockPos.getX() + 0.5D - to.xCoord;
        double d2 = (double) blockPos.getZ() + 0.5D - to.zCoord;
        return Math.sqrt(d0 * d0 + d2 * d2);
    }

    public static void delayRunnableForXTicks(Runnable runnable, int ticks) {
        for (Map.Entry<Integer, Map.Entry<Runnable, Integer>> entry : scheduleQueue.entrySet()) {
            if (entry.getValue().getKey().equals(runnable)) {
                return;
            }
        }
        scheduleQueue.put(++scheduleId, new AbstractMap.SimpleEntry<>(runnable, ticks));
    }

    public static void tickQueue() {
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

    public static boolean isSlayerBossSlainMessage(String message) {
        return message.contains(slayerBossSlainMessage);
    }

    public static String getCommandFromBatphoneMessage(IChatComponent message) {
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

    public static boolean playerCurrentlyFightingBoss() {
        return Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveNames().stream().anyMatch(objective -> objective.contains("Slay the boss!"));
    }

    public static String formatTimestamp(String timestamp) {
        try {
            return dateFormatOutput.format(dateFormat.parse(timestamp));
        } catch (ParseException ignored) {
            return null;
        }
    }

    public static void handleInvalidApiKey() {
        sendMessageToPlayer("Your API key is invalid and was removed\n§cTo generate a new one, type §b'/api new'", EnumChatFormatting.RED);
        main.resetAPIKey();
    }

    public static String getSystemClipboardContents() {
        try {
            Transferable clipboard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            return clipboard == null ? "" : (String) clipboard.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return "";
        }
    }

    public static boolean isKeyOrMouseButtonDown(int keyOrButtonCode) {
        try {
            return Keyboard.isKeyDown(keyOrButtonCode);
        } catch (IndexOutOfBoundsException ex) {
            try {
                return Mouse.isButtonDown(keyOrButtonCode);
            } catch (IndexOutOfBoundsException exception) {
                main.logger.error(exception);
                return false;
            }
        }
    }

    public static boolean isInAPrivateMega() {
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

    public static String stripWeirdChars(String input) {
        return stripWeirdCharsRegex.matcher(input).replaceAll("");
    }

    public static boolean showMessageOnRightSide(IChatComponent message) {
        return true;
    }

    public static boolean isRarityLine(String line) {
        return line.contains("COMMON") || line.contains("UNCOMMON") || line.contains("RARE") || line.contains("EPIC") || line.contains("LEGENDARY") || line.contains("MYTHIC") || line.contains("SUPREME") || line.contains("SPECIAL");
    }

    public static void sendUpdateNotification() {
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        sendMessageToPlayer(new ChatComponentText("§6-----------------------------------------------"));
        sendMessageToPlayer(new ChatComponentText("§f[§7Confusing§bAddons§f] §9§lA new update is available!"));
        sendMessageToPlayer(new ChatComponentText("§f[§7Confusing§bAddons§f] §aNew Version: §l" + main.getRuntimeInfo().getLatestVersion() + "§r§c   Current Version: §l" + ConfusingAddons.VERSION));
        sendMessageToPlayer(new ChatComponentText("§6-----------------------------------------------"));
        sendMessageToPlayer(
                new ChatComponentText("§a[Download + Changelog]")
                        .setChatStyle(new ChatStyle()
                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fixChatComponentColors(new ChatComponentText("§e§lClick!§r"))))
                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, main.getRuntimeInfo().getDownloadURL())))

                        .appendSibling(new ChatComponentText(" ").setChatStyle(new ChatStyle()))
                        .appendSibling(new ChatComponentText("§9[Direct Download]")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, fixChatComponentColors(new ChatComponentText("§e§lClick!§r"))))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, main.getRuntimeInfo().getDirectDownloadURL())))
                        )
        );
    }

    public static IChatComponent fixChatComponentColors(IChatComponent input) {
        List<IChatComponent> components = new ArrayList<>(input.getSiblings());
        components.add(0, input);
        IChatComponent output = new ChatComponentText("");
        for (IChatComponent componentParent : components) {
            EnumChatFormatting colorToUse = EnumChatFormatting.WHITE;
            if (!(componentParent instanceof ChatComponentText)) {
                output.appendSibling(componentParent);
                continue;
            }
            List<String> temp = new ArrayList<>(Arrays.asList((componentParent.getChatStyle().getFormattingCode() + componentParent.getUnformattedTextForChat()).split("\u00A7(?=[a-fA-F0-9lLkKmMnNoOrR])")));
            List<ChatComponentText> componentChildren = new ArrayList<>();
            if (!temp.isEmpty() && !temp.get(0).isEmpty()) componentChildren.add((ChatComponentText) new ChatComponentText(temp.get(0)).setChatStyle(componentParent.getChatStyle().createDeepCopy()));
            componentChildren.addAll(temp.subList(1, temp.size()).stream().map(component ->
                    (ChatComponentText) new ChatComponentText("\u00A7" + component).setChatStyle(componentParent.getChatStyle().createDeepCopy())).collect(Collectors.toList()));

            int prevStyleCodeIndex = 15;
            for (ChatComponentText componentChild : componentChildren) {
                boolean randomStyle = false;
                boolean boldStyle = false;
                boolean strikethroughStyle = false;
                boolean underlineStyle = false;
                boolean italicStyle = false;
                int styleCodeIndex;
                char[] charArray;
                if (componentChild.getFormattedText().endsWith("§r")) charArray = componentChild.getFormattedText().substring(0, componentChild.getFormattedText().length() - 2).toCharArray();
                else charArray = componentChild.getFormattedText().toCharArray();
                for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
                    char c = charArray[i];
                    if (c == 167 && i + 1 < componentChild.getFormattedText().length()) {
                        styleCodeIndex = "0123456789abcdefklmnor".indexOf(componentChild.getFormattedText().toLowerCase(Locale.ENGLISH).charAt(i + 1));

                        if (styleCodeIndex < 16) {
                            randomStyle = false;
                            boldStyle = false;
                            strikethroughStyle = false;
                            underlineStyle = false;
                            italicStyle = false;

                            if (styleCodeIndex < 0) {
                                styleCodeIndex = 15;
                            }

                        } else if (styleCodeIndex == 16) {
                            randomStyle = true;
                        } else if (styleCodeIndex == 17) {
                            boldStyle = true;
                        } else if (styleCodeIndex == 18) {
                            strikethroughStyle = true;
                        } else if (styleCodeIndex == 19) {
                            underlineStyle = true;
                        } else if (styleCodeIndex == 20) {
                            italicStyle = true;
                        } else {
                            randomStyle = false;
                            boldStyle = false;
                            strikethroughStyle = false;
                            underlineStyle = false;
                            italicStyle = false;
                            styleCodeIndex = 15;
                        }

                        for (EnumChatFormatting testColor : EnumChatFormatting.values()) {
                            if (testColor.getColorIndex() == styleCodeIndex) {
                                colorToUse = testColor;
                                break;
                            }
                        }
                    }
                }
                ChatStyle chatStyle = componentChild.getChatStyle()
                        .setColor(colorToUse)
                        .setBold(boldStyle)
                        .setItalic(italicStyle)
                        .setObfuscated(randomStyle)
                        .setStrikethrough(strikethroughStyle)
                        .setUnderlined(underlineStyle);
                IChatComponent lemp = output.appendSibling(new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(componentChild.getUnformattedText())).setChatStyle(chatStyle));
            }
        }
        return output;
    }

    public static double getBestOfferFromArray(JsonArray jsonArray, boolean isSellPrice) {
        int bestOfferIndex = 0;
        int currentIndex = 0;
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (isSellPrice) {
                if (jsonObject.get("pricePerUnit").getAsDouble() > jsonArray.get(bestOfferIndex).getAsJsonObject().get("pricePerUnit").getAsDouble()) {
                    bestOfferIndex = currentIndex;
                }
            } else {
                if (jsonObject.get("pricePerUnit").getAsDouble() < jsonArray.get(bestOfferIndex).getAsJsonObject().get("pricePerUnit").getAsDouble()) {
                    bestOfferIndex = currentIndex;
                }
            }
            currentIndex++;
        }
        if (jsonArray.size() == 0) {
            return 0;
        }
        return jsonArray.get(bestOfferIndex).getAsJsonObject().get("pricePerUnit").getAsDouble();
    }

    // From SkyblockAddons
    public static void refreshHypixelJoinment() {
        Minecraft mc = Minecraft.getMinecraft();

        if (!mc.isSingleplayer() && mc.thePlayer.getClientBrand() != null) {
            Matcher matcher = RegexUtil.getMatcher("(.+)(?= <-.+)", mc.thePlayer.getClientBrand());

            if (matcher.find()) {
                onHypixel.set(matcher.group(0).startsWith("Hypixel BungeeCord"));
                return;
            }
        }
        onHypixel.set(false);
    }

    public static boolean isOnHypixel() {
        return onHypixel.get();
    }

    public static double round(double input, int places) {
        double factor = Math.pow(10, places);
        return Math.round(input * factor) / factor;
    }

    public static boolean inBoundingBox(Vector2f pos, Vector2f bb1, Vector2f bb2) {
        return pos.x > bb1.x && pos.y > bb1.y && pos.x < bb2.x && pos.y < bb2.y;
    }
}
