package org.infernalstudios.shieldexp.access;

import net.minecraft.world.item.ItemStack;

public interface LivingEntityAccess {

    static LivingEntityAccess get(Object object) {
        return (LivingEntityAccess) object;
    }

    /**
     * Returns the remaining ticks where parrying is possible.
     */
    int getParryWindow();

    /**
     * Sets the amount of next ticks where parrying is possible.
     */
    void setParryWindow(int parry);

    int getBlockedCooldown();

    void setBlockedCooldown(int block);

    /**
     * Returns true if the player is currently in the blocking state.
     */
    boolean getBlocking();

    /**
     * Sets the blocking state of the player to the given bool value.
     */
    void setBlocking(boolean bool);

    int getUsedStamina();

    void setUsedStamina(int stamina);

    ItemStack getLastShield();

    void setLastShield(ItemStack shield);

}