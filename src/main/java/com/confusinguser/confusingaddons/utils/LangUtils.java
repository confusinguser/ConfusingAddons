package com.confusinguser.confusingaddons.utils;

import net.minecraft.client.Minecraft;

import java.util.*;
import java.util.function.Function;

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

    public static String cutOffString(String input, int maxWidth, int maxRows, Function<String, Integer> widthFunc) {
        if (input.isEmpty()) return input;
        String[] words = splitKeepDelimiter(input, " ");
        StringBuilder currRow = new StringBuilder();
        StringBuilder rows = new StringBuilder();
        int rowCount = 0;
        for (String word : words) {
            if (widthFunc.apply(currRow + word) > maxWidth) {
                if (currRow.toString().isEmpty()) currRow.append(word); // So that every row takes at least a word
                if (currRow.toString().endsWith(" ")) currRow.deleteCharAt(currRow.length()-1);
                rowCount++;
                if (rowCount >= maxRows) {
                    if (maxWidth < widthFunc.apply("...")) currRow = new StringBuilder("...");
                    for (int j = 0; j < currRow.length(); j++) {
                        if (widthFunc.apply(currRow.toString()) <= maxWidth - widthFunc.apply("...")) {
                            currRow.append("...");
                            return rows.append(currRow).toString();
                        } else currRow.deleteCharAt(currRow.length()-1);
                    }
                } else {
                    rows.append(currRow).append("\n");
                    currRow = new StringBuilder();
                }
            } else {
                currRow.append(word);
            }

        }
        if (!currRow.toString().isEmpty()) rows.append(currRow);
        if (rows.toString().endsWith("\n")) rows.deleteCharAt(rows.length()-1);
        return rows.toString();
    }

    public static String[] splitKeepDelimiter(String input, String delimiter) {
        if (delimiter.isEmpty()) return new String[0];
        List<String> output = new ArrayList<>();
        StringBuilder stringSinceLastMatch = new StringBuilder();
        char[] charArray = input.toCharArray();
        for (int i = 0; i <= charArray.length - delimiter.length(); i++) {
            stringSinceLastMatch.append(charArray[i]);

            StringBuilder charsInFront = new StringBuilder();
            for (int j = 0; j < delimiter.length(); j++) {
                charsInFront.append(charArray[i + j]);
            }

            if (charsInFront.toString().equals(delimiter)) {
                output.add(stringSinceLastMatch.deleteCharAt(stringSinceLastMatch.length() - 1).toString() + charsInFront);
                stringSinceLastMatch = new StringBuilder();
            }

            if (i == charArray.length - delimiter.length() && !stringSinceLastMatch.toString().isEmpty()) {
                output.add(stringSinceLastMatch.deleteCharAt(stringSinceLastMatch.length() - 1).toString() + charsInFront);
            }
        }
        return output.toArray(new String[0]);
    }

    public static String deleteFromEnd(String input, int amountToDelete) {
        return input.substring(0, input.length()-1-amountToDelete);
    }

    public static int countBoldifiableCharacters(String input) {
        final String boldifiable = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";
        int result = 0;
        for (char character : input.toCharArray()) {
            if (boldifiable.contains(String.valueOf(character))) {
                result++;
            }
        }
        return result;
    }

    public static int getStringWidth(String text) {
        return Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
    }
}
