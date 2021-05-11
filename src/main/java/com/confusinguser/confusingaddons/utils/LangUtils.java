package com.confusinguser.confusingaddons.utils;

import java.util.*;

public class LangUtils {
    public static final Map<String, String> apiNamesToUserFriendyNames = new HashMap<>();
    static {
        apiNamesToUserFriendyNames.put("LOG", "Oak Wood");
        apiNamesToUserFriendyNames.put("LOG:1", "Spruce Wood");
        apiNamesToUserFriendyNames.put("LOG:2", "Brich Wood");
        apiNamesToUserFriendyNames.put("LOG:3", "Jungle Wood");
        apiNamesToUserFriendyNames.put("LOG_2", "Acacia Wood");
        apiNamesToUserFriendyNames.put("LOG_2:1", "Dark Oak Wood");
        apiNamesToUserFriendyNames.put("RAW_FISH:1", "Raw Salmon");
        apiNamesToUserFriendyNames.put("RAW_FISH:2", "Clownfish");
        apiNamesToUserFriendyNames.put("RAW_FISH:3", "Pufferfish");
        apiNamesToUserFriendyNames.put("POTATO_ITEM", "Potato");
        apiNamesToUserFriendyNames.put("CARROT_ITEM", "Carrot");
        apiNamesToUserFriendyNames.put("RABBIT", "Raw Rabbit");
        apiNamesToUserFriendyNames.put("ENCHANTED_RABBIT", "Enchanted Raw Rabbit");
        apiNamesToUserFriendyNames.put("HUGE_MUSHROOM_1", "Brown Mushroom Block");
        apiNamesToUserFriendyNames.put("HUGE_MUSHROOM_2", "Red Mushroom Block");
        apiNamesToUserFriendyNames.put("ENCHANTED_HUGE_MUSHROOM_1", "Enchanted Brown Mushroom Block");
        apiNamesToUserFriendyNames.put("ENCHANTED_HUGE_MUSHROOM_2", "Enchanted Red Mushroom Block");
        apiNamesToUserFriendyNames.put("NETHER_STALK", "Nether Wart");
        apiNamesToUserFriendyNames.put("ENCHANTED_NETHER_STALK", "Enchanted Nether Wart");
        apiNamesToUserFriendyNames.put("SNOW_BALL", "Snowball");
        apiNamesToUserFriendyNames.put("CLAY_BALL", "Clay");
        apiNamesToUserFriendyNames.put("SULPHUR", "Gunpowder");
    }
    
    public static String getMinecraftColorCodeFromDouble(double input) {
        if (input <= .33) {
            return "c";
        }
        if (input <= .66) {
            return "e";
        }
        if (input <= .99) {
            return "a";
        }
        return "2";
    }


    public static String getFloorNameFromNumber(int floor) {
        if (floor == 0) {
            return "Entrance";
        } else {
            return "Floor " + floor;
        }
    }

    public static String beautifyString(String input) {
        if (apiNamesToUserFriendyNames.containsKey(input)) {
            return apiNamesToUserFriendyNames.get(input);
        }
        StringBuilder output = new StringBuilder();
        for (String word : input.replace('_', ' ').split(" ")) {
            output.append(' ').append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
        }
        return output.toString().trim();
    }

    public static boolean useIntVersion(double input) {
        return input == (int) input;
    }

    public static String alignString(String currentLine, int preferredIndentation, int minimumIndentation) {
        int indentationForLine = Math.max(preferredIndentation - currentLine.length(), minimumIndentation);
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < indentationForLine; i++) {
            output.append(" ");
        }
        return output.toString();
    }

    public static String getTranslation(String translationKey) {
        try {
            return ResourceBundle.getBundle("languages.lang", Locale.ENGLISH).getString(translationKey);
        } catch (MissingResourceException ex) {
            return "";
        }
    }
}
