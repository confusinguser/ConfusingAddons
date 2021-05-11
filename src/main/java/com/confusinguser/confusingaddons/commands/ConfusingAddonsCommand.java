package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.core.feature.Category;
import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.gui.SettingsGui;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
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
            return getListOfStringsMatchingLastWord(args, "nbt", "setkey");
        }
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            main.getEventListener().setGuiToOpen(new SettingsGui(Category.MAIN));
            return;
        }
        if (args[0].equalsIgnoreCase("nbt")) {
            boolean status;
            if (args.length == 1) {
                status = !Feature.isEnabled("COPY_NBT");
            } else {
                status = Utils.interpretBooleanString(args[1]);
            }
            Feature.getFeatureById("COPY_NBT").setStatus(status);
            if (status) Utils.sendMessageToPlayer("Copy NBT when [right ctrl] is pressed enabled!", EnumChatFormatting.GREEN);
            else Utils.sendMessageToPlayer("Copy NBT when [right ctrl] is pressed disabled!", EnumChatFormatting.RED);
        } else if (args[0].equalsIgnoreCase("setkey")) {
            if (args.length == 1) {
                Utils.sendMessageToPlayer("Usage: /ca setkey <your-api-key>", EnumChatFormatting.RED);
            } else {
                try {
                    main.setApiKey(args[1]);
                    Utils.sendMessageToPlayer("API key is now set to " + args[1], EnumChatFormatting.RED);
                } catch (IllegalArgumentException e) {
                    Utils.sendMessageToPlayer("That is not a valid API key!", EnumChatFormatting.RED);
                }
            }
        }
    }
}
