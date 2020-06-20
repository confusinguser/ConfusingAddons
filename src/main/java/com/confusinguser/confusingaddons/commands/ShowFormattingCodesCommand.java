package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collections;
import java.util.List;

public class ShowFormattingCodesCommand extends CommandBase {

    private final ConfusingAddons main;

    public ShowFormattingCodesCommand(ConfusingAddons main) {
        this.main = main;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("sfc");
    }

    @Override
    public String getCommandName() {
        return "showformattingcodes";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/showformattingcodes [on/off]";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        boolean status;
        if (args.length == 0) {
            status = !Feature.SHOW_FORMATTING_CODES.isEnabled();
        } else {
            status = main.getUtils().interpretBooleanString(args[0]);
        }
        Feature.SHOW_FORMATTING_CODES.setStatus(status);

        if (status) main.getUtils().sendMessageToPlayer("Enabled show click commands", EnumChatFormatting.GREEN);
        else main.getUtils().sendMessageToPlayer("Disabled show click commands", EnumChatFormatting.RED);
    }
}
