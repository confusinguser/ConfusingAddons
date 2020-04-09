package com.confusinguser.confusingaddons.commands;

import java.util.Collections;
import java.util.List;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Feature;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class HideJoinLeaveMessages extends CommandBase {

	ConfusingAddons main;
	private String fullname;

	public HideJoinLeaveMessages(ConfusingAddons main) {
		this.main = main;
		this.fullname = "hide lobby spam";
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("hjlm");
	}

	@Override
	public String getCommandName() {
		return "hidejoinleavemessages";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/hidejoinleavemessages [on/off]";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		boolean status;
		if (args.length == 0) {
			status = !Feature.HIDE_JOIN_LEAVE_MESSAGES.getStatus();
		} else {
			status = main.getUtil().interpretBooleanString(args[0]);
		}
		Feature.HIDE_JOIN_LEAVE_MESSAGES.setStatus(status);

		if (status) main.getUtil().sendMessageToPlayer("§aEnabled " + fullname);
		else main.getUtil().sendMessageToPlayer("§cDisabled " + fullname);
	}
}