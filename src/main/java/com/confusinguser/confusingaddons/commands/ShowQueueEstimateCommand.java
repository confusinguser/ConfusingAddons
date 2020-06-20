package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

public class ShowQueueEstimateCommand extends CommandBase {

    private final ConfusingAddons main;

    public ShowQueueEstimateCommand(ConfusingAddons main) {
        this.main = main;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("sqe");
    }

    @Override
    public String getCommandName() {
        return "showqueueestimate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/showqueueestiamte [on/off]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        boolean status;
        if (args.length == 0) {
            status = !Feature.SHOW_QUEUE_ESIMATE.isEnabled();
        } else {
            status = main.getUtils().interpretBooleanString(args[0]);
        }
        Feature.SHOW_QUEUE_ESIMATE.setStatus(status);

        if (status) main.getUtils().sendMessageToPlayer("Enabled show queue estimate", EnumChatFormatting.GREEN);
        else main.getUtils().sendMessageToPlayer("Disabled queue estimate commands", EnumChatFormatting.RED);
    }
}
