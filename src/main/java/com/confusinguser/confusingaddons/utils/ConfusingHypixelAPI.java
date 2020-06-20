package com.confusinguser.confusingaddons.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.hypixel.api.HypixelAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class ConfusingHypixelAPI extends HypixelAPI {

    private static final String BASE_URL = "https://api.hypixel.net/";
    private static final String USER_AGENT = "Mozilla/5.0";

    public ConfusingHypixelAPI(UUID apiKey) {
        super(apiKey);
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
                // Restore interrupted state...
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
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
            return getSkyblockProfileByUUID(uuid);

        } else if (jsonObject.has("cause") && jsonObject.get("cause").getAsString().contentEquals("Invalid API key!")) {
            return null;
        }
        return jsonObject;
    }
}
