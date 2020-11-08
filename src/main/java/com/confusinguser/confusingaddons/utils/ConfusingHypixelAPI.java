package com.confusinguser.confusingaddons.utils;

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

    public ConfusingHypixelAPI(UUID apiKey) {
        super(apiKey);
        Multithreading.runAsync(() -> {
            try {
                getGuildByPlayer(Minecraft.getMinecraft().thePlayer.getUniqueID()).get().getGuild().getMembers()
                        .forEach(member -> {
                            try {
                                guildMemberCache.add(getPlayerByUuid(member.getUuid()).get().getPlayer().get("displayname").getAsString());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    // Not as nicely done I know :/
    public JsonObject getSkyblockProfileByUUID(UUID uuid) {
        StringBuilder url_string = new StringBuilder(BASE_URL);

        url_string.append("skyblock/profile");
        url_string.append("?key=").append(super.getApiKey());

        url_string.append("&profile=").append(uuid.toString().replace("-", ""));

        StringBuffer response;
        try {
            URL url = new URL(url_string.toString());

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
            return getSkyblockProfileByUUID(uuid);
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
            return getSkyblockProfileByUUID(uuid);

        } else if (jsonObject.has("cause") && jsonObject.get("cause").getAsString().contentEquals("Invalid API key!")) {
            return null;
        }
        return jsonObject;
    }

    public List<String> getGuildMembers() {
        return new ArrayList<>(guildMemberCache);
    }
}
