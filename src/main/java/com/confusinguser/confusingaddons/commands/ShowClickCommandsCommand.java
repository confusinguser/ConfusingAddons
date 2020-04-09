package com.confusinguser.confusingaddons.commands;

import java.util.Collections;
import java.util.List;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ShowClickCommandsCommand extends CommandBase {

	ConfusingAddons main;

	public ShowClickCommandsCommand(ConfusingAddons main) {
		this.main = main;
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("scc");
	}

	@Override
	public String getCommandName() {
		return "showclickcommands";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/showclickcommands [on/off]";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		boolean status;
		if (args.length == 0) {
			status = !Feature.SHOW_CLICK_COMMANDS.getStatus();
		} else {
			status = main.getUtil().interpretBooleanString(args[0]);
		}
		Feature.SHOW_CLICK_COMMANDS.setStatus(status);
		
		if (status) main.getUtil().sendMessageToPlayer("§aEnabled show click commands");
		else main.getUtil().sendMessageToPlayer("§cDisabled show click commands");
	}
}
