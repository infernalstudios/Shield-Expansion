package org.infernalstudios.shieldexp.events;

import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;

public class FovEvents {
    public static float onComputeFov(Player player, float currentFovModifier) {
        if (LivingEntityAccess.get(player).getBlocking()) {
            return 1.0F;
        }
        return currentFovModifier;
    }
}