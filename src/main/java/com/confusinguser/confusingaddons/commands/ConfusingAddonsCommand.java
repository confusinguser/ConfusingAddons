package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.gui.ConfusingAddonsGui;
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
            return getListOfStringsMatchingLastWord(args, "hidejoinleavemessages", "nbt", "switchtobatphonewhenslayerdone", "autoopenmaddoxgui", "hidelobbyspam", "showclickcommands", "showpacketsinchat", "setkey", "speedbridge");
        } else if (args.length == 2) {
            return Arrays.asList("on", "off");
        }
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            main.getEventListener().setGuiToOpen(new ConfusingAddonsGui());
            return;
        }
        if (args[0].equalsIgnoreCase("hidejoinleavemessages")) {
            boolean status;
            if (args.length == 1) {
                status = !Feature.HIDE_JOIN_LEAVE_MESSAGES.isEnabled();
            } else {
                status = main.getUtils().interpretBooleanString(args[1]);
            }
            Feature.HIDE_JOIN_LEAVE_MESSAGES.setStatus(status);
            if (status)
                main.getUtils().sendMessageToPlayer("Join and leave messages are now hidden!", EnumChatFormatting.GREEN);
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
            if (status)
                main.getUtils().sendMessageToPlayer("Maddox GUI will automatically open when calling!", EnumChatFormatting.GREEN);
            else
                main.getUtils().sendMessageToPlayer("Maddox GUI will no longer automatically open when calling!", EnumChatFormatting.RED);
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
        }
    }
}
