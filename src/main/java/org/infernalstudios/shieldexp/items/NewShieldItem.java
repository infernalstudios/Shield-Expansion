package org.infernalstudios.shieldexp.items;

import net.minecraft.world.item.ShieldItem;

public class NewShieldItem extends ShieldItem {
    public float SPEED = 1.0F;
    public float DAMAGE = 0.0F;

    public NewShieldItem(Properties properties, float speedFactor, float damageBack) {
        super(properties);
        SPEED = speedFactor;
        DAMAGE = damageBack;
    }

    public float getSpeedFactor() {
        return SPEED;
    }

    public float getDamageFactor() {
        return DAMAGE;
    }
}
