package com.confusinguser.confusingaddons.asm.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

@SuppressWarnings("unused")
public class EntityRendererHook {
    public static Entity lastEntityThatAttacked;
    
    public static void hurtCameraEffect(EntityLivingBase player) {
        if (!(player instanceof EntityPlayer) || lastEntityThatAttacked == null) return;
        double d1 = lastEntityThatAttacked.posX - player.posX;
        double d0;

        for (d0 = lastEntityThatAttacked.posZ - player.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
        {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }

        player.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * 180.0D / Math.PI - (double)player.rotationYaw);
    }
}
