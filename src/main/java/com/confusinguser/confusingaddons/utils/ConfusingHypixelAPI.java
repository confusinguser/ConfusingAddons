package com.confusinguser.confusingaddons.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hypixel.api.HypixelAPI;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ConfusingHypixelAPI extends HypixelAPI {

    private static final String BASE_URL = "https://api.hypixel.net/";
    private static final String USER_AGENT = "Mozilla/5.0";

    private final List<String> guildMemberCache = new ArrayList<>();
    private final List<String> friendsCache = new ArrayList<>();

    public ConfusingHypixelAPI(UUID apiKey) {
        super(apiKey);
        Multithreading.runAsync(() -> {
            UUID playerUUID = Minecraft.getMinecraft().thePlayer.getUniqueID();
            try {
                getGuildByPlayer(playerUUID).get().getGuild().getMembers()
                        .forEach(member -> guildMemberCache.add(getNameByUUID(member.getUuid().toString())));
                getFriends(playerUUID).get().getFriendShips()
                        .forEach(friendShip -> friendsCache.add(getNameByUUID(
                                (friendShip.getUuidReceiver() == playerUUID ?
                                        friendShip.getUuidSender() : friendShip.getUuidReceiver()).toString())));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }


    public JsonObject getResponse(String urlString) {
        StringBuffer response;
        try {
            URL url = new URL(urlString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            response = new StringBuffer();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            return getResponse(urlString);
        }

        JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

        if (jsonObject.has("throttle") && jsonObject.get("throttle").getAsBoolean()) {
            //System.out.println("Got throttled, pausing for 17 seconds");
            try {
                Thread.sleep(17000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            return getResponse(urlString);

        } else if (jsonObject.has("cause") && jsonObject.get("cause").getAsString().contentEquals("Invalid API key!")) {
            return null;
        }
        return jsonObject;
    }

    public String getNameByUUID(String UUID) {
        String urlString = "https://api.mojang.com/user/profiles/" + UUID.replace("-", "") + "/names";
        JsonArray names = getResponse(urlString).getAsJsonArray();
        return names.get(names.size() - 1).getAsJsonObject().get("name").getAsString();
    }

    // Not as nicely done I know :/
    public JsonObject getSkyblockProfileByUUID(UUID uuid) {
        String urlString = BASE_URL + "skyblock/profile?key=" + super.getApiKey() + "&profile=" + uuid.toString().replace("-", "");
        return getResponse(urlString);
    }

    public List<String> getGuildMembers() {
        return new ArrayList<>(guildMemberCache);
    }

    public List<String> getFriends() {
        return new ArrayList<>(friendsCache);
    }
}
