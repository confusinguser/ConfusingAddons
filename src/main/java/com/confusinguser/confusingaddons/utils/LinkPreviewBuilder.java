package com.confusinguser.confusingaddons.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LinkPreviewBuilder {

    private static final Map<String, Link> cache = new HashMap<>();

    public static Link extractLinkPreviewInfo(URL url) throws IOException {
        if (!url.getProtocol().equals("https")) {
            return null;
        }
        if (cache.containsKey(url.toString())) {
            return cache.get(url.toString());
        }
        Connection connection = Jsoup.connect(url.toExternalForm());
        if (url.getHost().endsWith("youtube.com")) connection.cookie("CONSENT", "YES+0");
        Document document = connection.get();

        String title = getMetaTagContent(document, "meta[property=og:title]");
        if (title.isEmpty()) title = getMetaTagContent(document, "meta[name=title]");;
        if (title.isEmpty()) title = document.title();

        String description = getMetaTagContent(document, "meta[property=og:description]");
        if (description.isEmpty()) description = getMetaTagContent(document, "meta[name=description]");

        String domain = getMetaTagContent(document, "meta[property=og:url]");
        if (domain.isEmpty()) domain = url.toExternalForm();
        try {
            domain = new URL(domain).getHost();
        } catch (Exception ignored) {
        }

        String image = getMetaTagContent(document, "meta[property=og:image]");
        if (image.isEmpty()) image = getMetaTagContent(document, "meta[itemprop=image]");

        String siteName = getMetaTagContent(document, "meta[property=og:site_name]");

        String uploadDate = getMetaTagContent(document, "meta[itemprop=uploadDate]");

        String genre = getMetaTagContent(document, "meta[itemprop=genre]");

        if (url.getHost().endsWith("youtube.com")) {
            String documentBody = document.body().toString();
            int playerResponseIndex = documentBody.indexOf("ytInitialPlayerResponse = {") + 26;
            int playerResponseEndIndex = documentBody.indexOf("};", playerResponseIndex) + 1;
            String playerResponseStr = documentBody.substring(playerResponseIndex, playerResponseEndIndex);
            JsonObject playerResponse = new JsonParser().parse(playerResponseStr).getAsJsonObject();

            JsonObject videoDetails = playerResponse.getAsJsonObject("videoDetails");
            int lengthSeconds = -1;
            try {
                lengthSeconds = Integer.parseInt(videoDetails.get("lengthSeconds").getAsString());
            } catch (NumberFormatException ignored) {}
            description = videoDetails.get("shortDescription").getAsString();
            int views = -1;
            try {
                views = Integer.parseInt(videoDetails.get("viewCount").getAsString());
            } catch (NumberFormatException ignored) {}

            String channel = videoDetails.get("author").getAsString();

            JsonArray formats = playerResponse.getAsJsonObject("streamingData").getAsJsonArray("adaptiveFormats");
            int quality = -1;
            for (JsonElement forma : formats) {
                if (!(forma instanceof JsonObject)) continue;
                JsonObject format = (JsonObject) forma;
                try {
                    quality = Math.max(quality, format.get("height").getAsInt());
                } catch (Exception ignored) {}
                }
            Link link = new YoutubeLink(domain, url.toExternalForm(), title, description, image, siteName, uploadDate, genre, lengthSeconds, views, channel, quality);
            cache.put(url.toString(), link);
            return link;
        }

        Link link = new Link(domain, url.toExternalForm(), title, description, image, siteName, uploadDate, genre);
        cache.put(url.toString(), link);
        return link;
    }

    private static String getMetaTagContent(Document document, String cssQuery) {
        Element elm = document.select(cssQuery).first();
        if (elm != null) {
            return elm.attr("content");
        }
        return "";
    }

    public static class Link {

        private final String domain;
        private final String url;
        private final String title;
        private final String description;
        private final String image;
        private final String siteName;
        private final String uploadDate;
        private final String genre;


        public Link(String domain, String url, String title, String description, String image, String siteName, String genre, String uploadDate) {
            this.domain = domain;
            this.url = url;
            this.title = title;
            this.description = description;
            this.image = image;
            this.siteName = siteName;
            this.uploadDate = uploadDate;
            this.genre = genre;
        }

        public String getDomain() {
            return domain;
        }

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }

        public String getSiteName() {
            return siteName;
        }

        public String getUploadDate() {
            return uploadDate;
        }

        public String getGenre() {
            return genre;
        }
    }

    public static class YoutubeLink extends Link {

        private final int lengthSeconds;
        private final int views;
        private final String channel;
        private final int quality;


        public YoutubeLink(String domain, String url, String title, String description, String image, String siteName, String uploadDate, String genre, int lengthSeconds, int views, String channel, int quality) {
            super(domain, url, title, description, image, siteName, genre, uploadDate);
            this.lengthSeconds = lengthSeconds;
            this.views = views;
            this.channel = channel;
            this.quality = quality;
        }

        public int getLengthSeconds() {
            return lengthSeconds;
        }

        public int getViews() {
            return views;
        }

        public String getChannel() {
            return channel;
        }

        public int getQuality() {
            return quality;
        }
    }
}
