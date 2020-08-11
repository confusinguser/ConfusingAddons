package com.confusinguser.confusingaddons.commands;

import com.confusinguser.confusingaddons.ConfusingAddons;
import com.confusinguser.confusingaddons.utils.Multithreading;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FindprivatemegaCommand extends CommandBase {

    private final AtomicBoolean stop = new AtomicBoolean(true);

    @Override
    public String getCommandName() {
        return "findprivatemega";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> output = new ArrayList<>();
        output.add("fpm");
        return output;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/findprivatemega";
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1 && args[0].toLowerCase().startsWith("stop")) {
            synchronized (stop) {
                stop.set(true);
            }
            return;
        }
        boolean stopLocal;
        synchronized (stop) {
            stopLocal = stop.get();
        }
        if (stopLocal) {
            synchronized (stop) {
                stop.set(false);
            }
            Multithreading.runAsync(() -> {
                while (true) {
                    synchronized (stop) {
                        if (stop.get()) break;
                    }
                    if (ConfusingAddons.getInstance().getUtils().isInAnEmptyLobby()) {
                        ConfusingAddons.getInstance().getUtils().sendMessageToPlayer("§lFound an empty lobby!", EnumChatFormatting.GREEN);
                        Minecraft.getMinecraft().thePlayer.playSound("random.bowhit", 1.0F, 1.2f);
                        break;
                    }
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp end");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    synchronized (stop) {
                        if (stop.get()) break;
                    }
                    if (ConfusingAddons.getInstance().getUtils().isInAPrivateMega()) {
                        ConfusingAddons.getInstance().getUtils().sendMessageToPlayer("§lFound a private mega!", EnumChatFormatting.GREEN);
                        Minecraft.getMinecraft().thePlayer.playSound("random.bowhit", 1.0F, 1.2f);
                        break;
                    }
                    if (!ConfusingAddons.getInstance().getUtils().isInAPrivateMega()) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/warp hub");
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        } else {
            ConfusingAddons.getInstance().getUtils().sendMessageToPlayer("You are already looking for a private mega!", EnumChatFormatting.RED);
        }
    }
}
