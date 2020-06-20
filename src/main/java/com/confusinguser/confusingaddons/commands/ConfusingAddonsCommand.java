package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfusingAddonsCommand extends CommandBase {

    private final ConfusingAddons main;

    public ConfusingAddonsCommand(ConfusingAddons main) {
        this.main = main;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("ca");
    }

    @Override
    public String getCommandName() {
        return "confusingaddons";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ca <subcommand> [on/off]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "hidejoinleavemessages", "nbt", "switchtobatphonewhenslayerdone", "autoopenmaddoxgui", "hidelobbyspam", "showclickcommands", "setkey", "speedbridge", "leftautoclicker", "rightautoclicker");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("speedbridge")) {
            return getListOfStringsMatchingLastWord(args, "security");
        } else if (args.length == 2 && args[0].contains("autoclicker")) {
            return getListOfStringsMatchingLastWord(args, "cps");
        } else if (args.length == 2 && !args[0].contains("autoclicker") && !args[0].equalsIgnoreCase("speedbridge")) {
            return Arrays.asList("on", "off");
        }
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("hidejoinleavemessages")) {
            boolean status;
            if (args.length == 1) {
                status = !Feature.HIDE_JOIN_LEAVE_MESSAGES.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[1]);
            }
            Feature.HIDE_JOIN_LEAVE_MESSAGES.setStatus(status);
            if (status) main.getUtils().sendMessageToPlayer("Join and leave messages are now hidden!", EnumChatFormatting.GREEN);
            else main.getUtils().sendMessageToPlayer("Join and leave messages are now shown!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("nbt")) {

            boolean status;
            if (args.length == 1) {
                status = !Feature.COPY_NBT.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[1]);
            }
            Feature.COPY_NBT.setStatus(status);
            if (status) main.getUtils().sendMessageToPlayer("Copy NBT on click enabled!", EnumChatFormatting.GREEN);
            else main.getUtils().sendMessageToPlayer("Copy NBT on click disabled!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("switchtobatphonewhenslayerdone")) {

            boolean status;
            if (args.length == 1) {
                status = !Feature.SWITCH_TO_BATPHONE_ON_SLAYER_DONE.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[1]);
            }
            Feature.SWITCH_TO_BATPHONE_ON_SLAYER_DONE.setStatus(status);
            if (status)
                main.getUtils().sendMessageToPlayer("Mod will now switch to Batphone when you have killed a slayer boss!", EnumChatFormatting.GREEN);
            else
                main.getUtils().sendMessageToPlayer("Mod will no longer switch to Batphone when you have killed a slayer boss!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("autoopenmaddoxgui")) {

            boolean status;
            if (args.length == 1) {
                status = !Feature.AUTO_OPEN_MADDOX_GUI.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[1]);
            }
            Feature.AUTO_OPEN_MADDOX_GUI.setStatus(status);
            if (status) main.getUtils().sendMessageToPlayer("Maddox GUI will automatically open when calling!", EnumChatFormatting.GREEN);
            else main.getUtils().sendMessageToPlayer("Maddox GUI will no longer automatically open when calling!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("hidelobbyspam")) {

            boolean status;
            if (args.length == 1) {
                status = !Feature.HIDE_LOBBY_SPAM.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[0]);
            }
            Feature.HIDE_LOBBY_SPAM.setStatus(status);

            if (status) main.getUtils().sendMessageToPlayer("Lobby spam is now hidden!", EnumChatFormatting.GREEN);
            else main.getUtils().sendMessageToPlayer("Lobby spam is now shown!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("showclickcommands")) {

            boolean status;
            if (args.length == 1) {
                status = !Feature.SHOW_CLICK_COMMANDS.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[0]);
            }
            Feature.SHOW_CLICK_COMMANDS.setStatus(status);

            if (status) main.getUtils().sendMessageToPlayer("Click commands are now shown!", EnumChatFormatting.GREEN);
            else main.getUtils().sendMessageToPlayer("Click commands are now hidden!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("setkey")) {
            if (args.length == 1) {
                main.getUtils().sendMessageToPlayer("Usage: /ca setkey <your-api-key>", EnumChatFormatting.RED);
            } else {
                try {
                    main.setApiKey(args[1]);
                    main.getUtils().sendMessageToPlayer("API key is now set to " + args[1], EnumChatFormatting.RED);
                } catch (IllegalArgumentException e) {
                    main.getUtils().sendMessageToPlayer("That is not a valid API key!", EnumChatFormatting.RED);
                }
            }
        } else if (args[0].equalsIgnoreCase("speedbridge")) {
            if (args.length == 1) {
                main.getUtils().sendMessageToPlayer("Usage: /ca speedbridge <security> ...", EnumChatFormatting.RED);
            } else if (args[1].equalsIgnoreCase("security")) {
                if (args.length == 2)
                    main.getUtils().sendMessageToPlayer("Usage: /ca speedbridge security <security integer 0-10>", EnumChatFormatting.RED);
                else {
                    try {
                        int speedBridgeSecurity = Integer.parseInt(args[2]);
                        if (speedBridgeSecurity <= 10 && speedBridgeSecurity >= 0) {
                            main.getPlayerListener().speedBridgeSecurity = speedBridgeSecurity;
                            main.getUtils().sendMessageToPlayer("Speedbridge security is now set to " + args[2], EnumChatFormatting.GREEN);
                        } else {
                            main.getUtils().sendMessageToPlayer("Security value has to be in between 0 and 10", EnumChatFormatting.RED);
                        }
                    } catch (NumberFormatException e) {
                        main.getUtils().sendMessageToPlayer("That is not a valid integer!", EnumChatFormatting.RED);
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("leftautoclicker")) {
            if (args.length == 1) {
                main.getUtils().sendMessageToPlayer("Usage: /ca leftautoclicker <cps> ...", EnumChatFormatting.RED);
            } else if (args[1].equalsIgnoreCase("cps")) {
                if (args.length == 2)
                    main.getUtils().sendMessageToPlayer("Usage: /ca leftautoclicker cps <cps 0-20>", EnumChatFormatting.RED);
                else {
                    try {
                        int leftCPS = Integer.parseInt(args[2]);
                        if (leftCPS <= 20 && leftCPS >= 0) {
                            main.getPlayerListener().leftCPS = leftCPS;
                            main.getUtils().sendMessageToPlayer("CPS is now set to " + args[2], EnumChatFormatting.GREEN);
                        } else {
                            main.getUtils().sendMessageToPlayer("CPS must be in between 0 and 20", EnumChatFormatting.RED);
                        }
                    } catch (NumberFormatException e) {
                        main.getUtils().sendMessageToPlayer("That is not a valid integer!", EnumChatFormatting.RED);
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("rightautoclicker")) {
            if (args.length == 1) {
                main.getUtils().sendMessageToPlayer("Usage: /ca rightautoclicker <cps> ...", EnumChatFormatting.RED);
            } else if (args[1].equalsIgnoreCase("cps")) {
                if (args.length == 2)
                    main.getUtils().sendMessageToPlayer("Usage: /ca rightautoclicker cps <cps 0-20>", EnumChatFormatting.RED);
                else {
                    try {
                        int rightCPS = Integer.parseInt(args[2]);
                        if (rightCPS <= 20 && rightCPS >= 0) {
                            main.getPlayerListener().rightCPS = rightCPS;
                            main.getUtils().sendMessageToPlayer("CPS is now set to " + args[2], EnumChatFormatting.GREEN);
                        } else {
                            main.getUtils().sendMessageToPlayer("CPS must be in between 0 and 20", EnumChatFormatting.RED);
                        }
                    } catch (NumberFormatException e) {
                        main.getUtils().sendMessageToPlayer("That is not a valid integer!", EnumChatFormatting.RED);
                    }
                }
            }
        }

    }
}
