package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.*;
import net.hypixel.api.exceptions.HypixelAPIException;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ApiUtils {

    ConfusingAddons main;
    boolean busy = false;
    private Map<String, String> uuidToUsernameCache = new HashMap<>();

    public ApiUtils(ConfusingAddons main) {
        this.main = main;
    }

    public JsonElement getResponse(URL url) {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

        } catch (IOException exception) {
            try {
                if (connection != null) {
                    if (connection.getResponseCode() == 429) {
                        try {
                            Thread.sleep(17000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                        return getResponse(url);
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        try {
            return new JsonParser().parse(response.toString());
        } catch (JsonIOException e) {
            return null;
        }
    }



    public String getDisplayNameFromUUID(String uuid) {
        if (uuidToUsernameCache.containsKey(uuid)) {
            return uuidToUsernameCache.get(uuid);
        }

        if (busy) {
            return null;
        } else {
            Multithreading.runAsync(() -> {
                busy = true;
                try {
                    if (main.getAPI() == null) {
                        JsonElement nameHistory = getResponse(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names")).getAsJsonArray();
                        if (nameHistory == null) {
                            uuidToUsernameCache.put(uuid, "§cError");
                            return;
                        }
                        JsonArray nameHistoryArray = nameHistory.getAsJsonArray();
                        String name = nameHistoryArray.get(nameHistoryArray.size() - 1).getAsJsonObject().get("name").getAsString();
                        uuidToUsernameCache.put(uuid, name);
                    } else {
                        JsonObject playerData = main.getAPI().getPlayerByUuid(uuid).get().getPlayer();
                        HypixelRank rank = HypixelRank.NONE;
                        if (playerData.has("packageRank") && !playerData.get("packageRank").getAsString().equals("NONE"))
                            rank = HypixelRank.getHypixelRankFromName(playerData.get("packageRank").getAsString());
                        if (playerData.has("newPackageRank") && !playerData.get("newPackageRank").getAsString().equals("NONE"))
                            rank = HypixelRank.getHypixelRankFromName(playerData.get("newPackageRank").getAsString());
                        if (playerData.has("monthlyPackageRank") && !playerData.get("monthlyPackageRank").getAsString().equals("NONE"))
                            rank = HypixelRank.getHypixelRankFromName(playerData.get("monthlyPackageRank").getAsString());
                        if (playerData.has("rank") && !playerData.get("rank").getAsString().equals("NORMAL"))
                            rank = HypixelRank.getHypixelRankFromName(playerData.get("rank").getAsString());
                        String prefix = rank.getPrefix();
                        if (playerData.has("prefix")) prefix = playerData.get("prefix").getAsString() + " ";

                        String name = playerData.get("displayname").getAsString();
                        uuidToUsernameCache.put(uuid, prefix + name);
                    }
                    return;

                } catch (IOException | InterruptedException e) {
                    if (e instanceof IOException && e.getMessage().contains("Server returned HTTP response code: 400 for URL")) {
                        uuidToUsernameCache.put(uuid, "§cError");
                    } else {
                        e.printStackTrace();
                    }
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof HypixelAPIException) {
                        if (e.getCause().getMessage().equals("Malformed UUID")) {
                            uuidToUsernameCache.put(uuid, "§cError");
                        } else if (e.getCause().getMessage().equals("Invalid API key")) {
                            main.getUtils().handleInvalidApiKey();
                        }
                    }
                }
                busy = false;
            });
        }
        return null;
    }

    public void getLatestUpdateInfoFile() {
        try {
            JsonObject jsonObject = getResponse(new URL("https://raw.githubusercontent.com/confusinguser/ConfusingAddons/master/updateInfo.txt?token=AGCCX4SV65SJN7H7HQR47QS7IP4TG")).getAsJsonObject();
            if (!jsonObject.get("version").getAsString().equals(ConfusingAddons.VERSION)) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] §l§bA new update is available!"));
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] New Version: §d" + jsonObject.get("version").getAsString() + "§b   Current Version: §d" + ConfusingAddons.VERSION));
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§b[ConfusingAddons] §6--------------------------------------------"));
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                        new ChatComponentText("[Download + Changelog]")
                                .setChatStyle(new ChatStyle()
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§l§eClick!")))
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, jsonObject.get("downloadUrl").getAsString())))


                                .appendSibling(
                                        new ChatComponentText(" ")
                                                .appendSibling(new ChatComponentText("[Direct Download]")
                                                        .setChatStyle(new ChatStyle()
                                                                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§l§eClick!")))
                                                                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, jsonObject.get("directDownloadUrl").getAsString()))))));

            }
        } catch (MalformedURLException | JsonIOException e) {
            e.printStackTrace();
        }
    }

    public void clearCaches() {
        uuidToUsernameCache = new HashMap<>();
    }
}
