package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.ConfusingAddons;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

@SuppressWarnings("unused")
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

            int rarityLineIndex = list.size() - 1;
            if (extraAttributes.hasKey("baseStatBoostPercentage")) {
                for (int i = 0; i < list.size(); i++) {
                    if (ConfusingAddons.getInstance().getUtils().isRairityLine(list.get(i))) {
                        rarityLineIndex = i;
                        break;
                    }
                }

                int baseStatBoostPercentage = extraAttributes.getInteger("baseStatBoostPercentage");
                if (extraAttributes.hasKey("item_tier")) {
                    int itemTier = extraAttributes.getInteger("item_tier");
                    String floorName = ConfusingAddons.getInstance().getLangUtils().getFloorNameFromNumber(itemTier);
                    list.add(rarityLineIndex, "§7Base Stats: §" +
                            ConfusingAddons.getInstance().getLangUtils().getMinecraftColorCodeFromDouble(itemTier / 4d) +
                            "[" + floorName + "] §r§" + ConfusingAddons.getInstance().getLangUtils().getMinecraftColorCodeFromDouble(baseStatBoostPercentage / 50d) +
                            baseStatBoostPercentage + "/50");
                } else {
                    list.add(rarityLineIndex, "§7Base Stats: §" + ConfusingAddons.getInstance().getLangUtils().getMinecraftColorCodeFromDouble(baseStatBoostPercentage / 50d) + extraAttributes.getInteger("baseStatBoostPercentage") + "/50");
                }
            }
        }
        return list;
    }
}
