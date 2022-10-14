package org.infernalstudios.shieldexp.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ShieldItem;

public class NewShieldItem extends ShieldItem {
    public static final ResourceLocation BLOCKING = new ResourceLocation("minecraft:blocking");
    public float SPEED_MODIFIER = 1.0F;
    public double DAMAGE_RETURNING_FACTOR = 0.0;
    public int PARRY_TICKS = 5;
    public int SHIELD_TICKS = 10;

    public NewShieldItem(Properties properties, int blockTicks, float speedFactor, double damageBack, int parryTicks) {
        super(properties);
        SHIELD_TICKS = blockTicks;
        SPEED_MODIFIER = speedFactor;
        DAMAGE_RETURNING_FACTOR = damageBack;
        PARRY_TICKS = parryTicks;
    }

    public float getSpeedFactor() {
        return SPEED_MODIFIER;
    }

    public double getDamageFactor() {
        return DAMAGE_RETURNING_FACTOR;
    }

    public int getParryTicks() { return PARRY_TICKS; }

    public int getShieldTicks() { return SHIELD_TICKS; }
}