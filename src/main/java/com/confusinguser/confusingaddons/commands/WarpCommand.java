package com.confusinguser.confusingaddons.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.List;

public class WarpCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("w");
    } // TODO toggle alias

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return getListOfStringsMatchingLastWord(args, "hub", "home", "castle", "darkauction");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "darkauction":
                    args[0] = "da";
                case "h":
                    args[0] = "home";
                case "mushroom":
                case "mush":
                case "sugar":
                    args[0] = "desert";
            }
        }
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp " + String.join(" ", args));
    }
}
