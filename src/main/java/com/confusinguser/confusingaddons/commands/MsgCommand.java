package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;

public class MsgCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "msg";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("tell", "t");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> output = ConfusingAddons.getInstance().getAPI().getFriends();
        output.addAll(ConfusingAddons.getInstance().getAPI().getGuildMembers());
        return output;
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
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/msg " + String.join(" ", args));
    }
}
