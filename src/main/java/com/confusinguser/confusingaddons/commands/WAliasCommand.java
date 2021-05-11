package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.core.feature.Feature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.List;

public class WAliasCommand extends CommandBase {
    private final WarpCommand warp;
    private final MsgCommand msg;

    public WAliasCommand(WarpCommand warp, MsgCommand msg) {
        this.warp = warp;
        this.msg = msg;
    }

    @Override
    public String getCommandName() {
        return "w";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (Feature.isEnabled("W_ALIAS_WARP"))
            return warp.addTabCompletionOptions(sender, args, pos);
        return msg.addTabCompletionOptions(sender, args, pos);
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
        if (Feature.isEnabled("W_ALIAS_WARP"))
            return warp.getCommandUsage(sender);
        return msg.getCommandUsage(sender);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (Feature.isEnabled("W_ALIAS_WARP"))
            warp.processCommand(sender, args);
        else
            msg.processCommand(sender, args);
    }
}