package com.confusinguser.confusingaddons.asm.hooks;

import com.confusinguser.confusingaddons.core.feature.Feature;
import com.confusinguser.confusingaddons.core.feature.FeatureOptionSlider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class EntityRendererHook {
    public static Entity lastAttacker;

    public static void hurtCameraEffect(EntityLivingBase player, float f) {
        if (!(player instanceof EntityPlayer) || lastAttacker == null) return;
        float f2 = 0;
        if (Feature.isEnabled("HURT_EFFECT_FIX")) {
            double deltaX = lastAttacker.posX - player.posX;
            double deltaZ;
            for (deltaZ = lastAttacker.posZ - player.posZ; deltaX * deltaX + deltaZ * deltaZ < 1.0E-4D; deltaZ = (Math.random() - Math.random()) * 0.01D) {
                deltaX = (Math.random() - Math.random()) * 0.01D;
            }
            f2 = (float)(MathHelper.atan2(deltaZ, deltaX) * 180.0D / Math.PI - (double)player.rotationYaw);
        }
        double intensity = ((FeatureOptionSlider) Feature.getFeatureById("HURT_EFFECT_FIX").getOptions()[0]).getValue();

        GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float) (-f * intensity * 14.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
    }
}
