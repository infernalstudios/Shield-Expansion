package org.infernalstudios.shieldexp.access;

import net.minecraft.world.item.ItemStack;

public interface LivingEntityAccess {

    static LivingEntityAccess get(Object object) {
        return (LivingEntityAccess) object;
    }

    /**
     * Returns the remaining ticks where parrying is possible.
     */
    int shieldexp$getParryWindow();

    /**
     * Sets the amount of next ticks where parrying is possible.
     */
    void shieldexp$setParryWindow(int parry);

    int shieldexp$getBlockedCooldown();

    void shieldexp$setBlockedCooldown(int block);

    /**
     * Returns true if the player is currently in the blocking state.
     */
    boolean shieldexp$getBlocking();

    /**
     * Sets the blocking state of the player to the given bool value.
     */
    void shieldexp$setBlocking(boolean bool);

    int shieldexp$getUsedStamina();

    void shieldexp$setUsedStamina(int stamina);

    ItemStack shieldexp$getLastShield();

    void shieldexp$setLastShield(ItemStack shield);

}