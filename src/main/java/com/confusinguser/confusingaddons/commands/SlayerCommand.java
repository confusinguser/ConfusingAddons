package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Multithreading;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.hypixel.api.exceptions.HypixelAPIException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SlayerCommand extends CommandBase {

    private final ConfusingAddons main;

    public SlayerCommand(ConfusingAddons main) {
        this.main = main;
    }

    @Override
    public String getCommandName() {
        return "slayer";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/slayer <player>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (main.getAPI() == null) {
            main.getUtils().sendMessageToPlayer("You have not set an API key, generate one by running §b'/api new' §cor /ca setkey <your-api-key>' to set it", EnumChatFormatting.RED);
            return;
        }
        Multithreading.runAsync(() -> {
            if (args.length == 0) {
                main.getUtils().sendMessageToPlayer("Usage:" + getCommandUsage(sender), EnumChatFormatting.RED);
                return;
            }
            Map.Entry<String, UUID> playerObj = main.getUtils().getUUIDByUsername(args[0]);
            if (playerObj == null || playerObj.getKey() == null || playerObj.getValue() == null) {
                main.getUtils().sendMessageToPlayer("The specified player does not exist!", EnumChatFormatting.RED);
                return;
            }
            int zombieSlayer = 0;
            int spiderSlayer = 0;
            int wolfSlayer = 0;
            try {
                JsonObject player = main.getAPI().getPlayerByUuid(playerObj.getValue()).get().getPlayer().getAsJsonObject("stats");
                try {
                    player = player.getAsJsonObject("SkyBlock").getAsJsonObject("profiles");
                } catch (NullPointerException e) {
                    main.getUtils().sendMessageToPlayer("The specified player has never played SkyBlock!", EnumChatFormatting.RED);
                    return;
                }

                for (Map.Entry<String, JsonElement> skyblockProfile : player.entrySet()) {
                    UUID skyblockProfileUUID = UUID.fromString(skyblockProfile.getValue().getAsJsonObject().get("profile_id").getAsString().replaceAll("([0-9a-f]{8})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]{4})([0-9a-f]+)", "$1-$2-$3-$4-$5"));
                    JsonObject skyblockProfileSlayer = main.getAPI().getSkyblockProfileByUUID(skyblockProfileUUID);
                    if (skyblockProfileSlayer == null) {
                        main.getUtils().sendMessageToPlayer("The specified player has never played SkyBlock!", EnumChatFormatting.RED);
                        return;
                    }
                    skyblockProfileSlayer = skyblockProfileSlayer.getAsJsonObject("profile").getAsJsonObject("members").getAsJsonObject(playerObj.getValue().toString().replace("-", "")).getAsJsonObject("slayer_bosses");
                    System.out.println(skyblockProfileSlayer);
                    try {
                        zombieSlayer += skyblockProfileSlayer.getAsJsonObject("zombie").get("xp").getAsInt();
                        spiderSlayer += skyblockProfileSlayer.getAsJsonObject("spider").get("xp").getAsInt();
                        wolfSlayer += skyblockProfileSlayer.getAsJsonObject("wolf").get("xp").getAsInt();
                    } catch (JsonParseException | NullPointerException ignored) {
                    }
                }
            } catch (InterruptedException | ExecutionException | JsonParseException e) {
                e.printStackTrace();
            } catch (HypixelAPIException e) {
                main.getUtils().sendMessageToPlayer("The API key is invalid! Please use '/api new' to generate a new one", EnumChatFormatting.RED);
            }

            main.getUtils().sendMessageToPlayer(EnumChatFormatting.BOLD + playerObj.getKey() + "'s slayer exp", EnumChatFormatting.GOLD);
            main.getUtils().sendMessageToPlayer("  Zombie: " + zombieSlayer, EnumChatFormatting.LIGHT_PURPLE);
            main.getUtils().sendMessageToPlayer("  Spider: " + spiderSlayer, EnumChatFormatting.LIGHT_PURPLE);
            main.getUtils().sendMessageToPlayer("  Wolf   : " + wolfSlayer, EnumChatFormatting.LIGHT_PURPLE);
            main.getUtils().sendMessageToPlayer("Total slayer exp: " + (zombieSlayer + spiderSlayer + wolfSlayer), EnumChatFormatting.GOLD);
        });
    }
}
