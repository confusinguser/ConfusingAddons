package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.ConfusingAddons;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ItemStackHook {

    public static List<String> getToolTip(ItemStack item, List<String> list) {
        if (item == null || item.getTagCompound() == null) {
            return list;
        }
        if (item.getTagCompound().hasKey("ExtraAttributes")) {
            NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
            if (extraAttributes.hasKey("timestamp")) {
                String timestamp = ConfusingAddons.getInstance().getUtils().formatTimestamp(extraAttributes.getString("timestamp"));
                if (timestamp == null) return list;
                list.add("Timestamp: §c" + timestamp);
            }
            if (extraAttributes.hasKey("spawnedFor")) {
                String spawnedFor = extraAttributes.getString("spawnedFor");
                 spawnedFor = ConfusingAddons.getInstance().getApiUtils().getDisplayNameFromUUID(spawnedFor);
                list.add("§fOriginal Owner: " + (spawnedFor == null ? "§7Loading..." : spawnedFor));
            }
        }
        return list;
    }
}
