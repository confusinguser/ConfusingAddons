package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GChatCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "gchat";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("gc");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (ConfusingAddons.getInstance().getAPI() == null) {
            return new ArrayList<>();
        } else {
            return getListOfStringsMatchingLastWord(args, ConfusingAddons.getInstance().getAPI().getGuildMembers());
        }
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
        MinecraftForge.EVENT_BUS.post(new ClientChatReceivedEvent((byte) 0, new ChatComponentText("§2Guild > §7Confusinguser§f: TEST")));
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/gc " + String.join(" ", args));
    }
}
