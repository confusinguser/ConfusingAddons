package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.utils.LangUtils;
import com.confusinguser.confusingaddons.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ItemStackHook {

    public static List<String> getToolTip(ItemStack item, List<String> list) {
        if (item == null || item.getTagCompound() == null) {
            return list;
        }

        if (item.getTagCompound().hasKey("ExtraAttributes") && (Feature.isEnabled("SKYBLOCK_TOOLTIP_FEATURES") || Feature.isEnabled("DUNGEON_GEAR_TOOLTIP"))) {
            int rarityLineIndex = list.size() - 1;
            for (int i = 0; i < list.size(); i++) {
                if (Utils.isRarityLine(list.get(i))) {
                    rarityLineIndex = i;
                    break;
                }
            }
            NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
//            if (Feature.isEnabled("SKYBLOCK_TOOLTIP_FEATURES")) {
//                if (extraAttributes.hasKey("timestamp")) {
//                    String timestamp = Utils.formatTimestamp(extraAttributes.getString("timestamp"));
//                    if (timestamp == null) return list;
//                    list.add("Timestamp: §c" + timestamp);
//                }
//                if (extraAttributes.hasKey("spawnedFor")) {
//                    String spawnedFor = extraAttributes.getString("spawnedFor");
//                    spawnedFor = ConfusingAddons.getInstance().getApiUtil().getDisplayNameFromUUID(spawnedFor);
//                    list.add("§fOriginal Owner: " + (spawnedFor == null ? "§7Loading..." : spawnedFor));
//                }
//            }

            if (Feature.isEnabled("DUNGEON_GEAR_TOOLTIP") && extraAttributes.hasKey("baseStatBoostPercentage")) {
                int baseStatBoostPercentage = extraAttributes.getInteger("baseStatBoostPercentage");
                if (extraAttributes.hasKey("item_tier")) {
                    int itemTier = extraAttributes.getInteger("item_tier");
                    String floorName = LangUtils.getFloorNameFromNumber(itemTier);
                    list.add(rarityLineIndex, "§7Base Stats: §" +
                            LangUtils.getMinecraftColorCodeFromDouble((itemTier + 2) / 7d) +
                            "[" + floorName + "] §r§" + LangUtils.getMinecraftColorCodeFromDouble(baseStatBoostPercentage / 50d) +
                            baseStatBoostPercentage + "/50");
                } else {
                    list.add(rarityLineIndex, "§7Base Stats: §" + LangUtils.getMinecraftColorCodeFromDouble(baseStatBoostPercentage / 50d) + extraAttributes.getInteger("baseStatBoostPercentage") + "/50");
                }
            }
        }
        return list;
    }
}
