package org.infernalstudios.shieldexp.items;

import net.minecraft.world.item.ShieldItem;

public class NewShieldItem extends ShieldItem {
    public float SPEED_MODIFIER = 1.0F;
    public float DAMAGE_RETURNING_FACTOR = 0.0F;
    public int PARRY_TICKS = 5;

    public NewShieldItem(Properties properties, float speedFactor, float damageBack, int ticks) {
        super(properties);
        SPEED_MODIFIER = speedFactor;
        DAMAGE_RETURNING_FACTOR = damageBack;
        PARRY_TICKS = ticks;
    }

    public NewShieldItem(Properties properties, float speedFactor, float damageBack) {
        super(properties);
        SPEED_MODIFIER = speedFactor;
        DAMAGE_RETURNING_FACTOR = damageBack;
    }

    public float getSpeedFactor() {
        return SPEED_MODIFIER;
    }

    public float getDamageFactor() {
        return DAMAGE_RETURNING_FACTOR;
    }
}
