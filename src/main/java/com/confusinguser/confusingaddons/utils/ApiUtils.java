package com.confusinguser.confusingaddons.utils;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hypixel.api.exceptions.HypixelAPIException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
                    URL url;
                    if (main.getAPI() == null) {
                        url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();

                        String inputLine;
                        while ((inputLine = bufferedReader.readLine()) != null) {
                            response.append(inputLine);
                        }
                        bufferedReader.close();

                        if (connection.getResponseCode() == 204) {
                            uuidToUsernameCache.put(uuid, "§cInvalid UUID!");
                        }

                        try {
                            JsonArray nameHistory = new JsonParser().parse(response.toString()).getAsJsonArray();
                            String name = nameHistory.get(nameHistory.size() - 1).getAsJsonObject().get("name").getAsString();
                            uuidToUsernameCache.put(uuid, name);
                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        }
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

                } catch (IOException | InterruptedException e) {
                    if (e instanceof IOException && e.getMessage().contains("Server returned HTTP response code: 400 for URL")) {
                        uuidToUsernameCache.put(uuid, "§cInvalid UUID!");
                    } else {
                        e.printStackTrace();
                    }
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof HypixelAPIException) {
                        if (e.getCause().getMessage().equals("Malformed UUID")) {
                            uuidToUsernameCache.put(uuid, "§cInvalid UUID!");
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

    public void clearCaches() {
        uuidToUsernameCache = new HashMap<>();
    }
}
