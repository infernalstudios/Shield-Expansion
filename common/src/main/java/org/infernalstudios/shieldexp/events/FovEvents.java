package org.infernalstudios.shieldexp.events;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class FovEvents {
    public static float onComputeFov(Player player, float currentFovModifier) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            AttributeModifier shieldMod = speedAttribute.getModifier(ShieldEvents.SPEED_MODIFIER_ID);

            if (shieldMod != null) {
                double currentSpeed = speedAttribute.getValue();
                double modifierScale = 1.0D + shieldMod.amount();
                double speedWithoutShield = currentSpeed / modifierScale;

                return (float) ((speedWithoutShield / (double) player.getAbilities().getWalkingSpeed() + 1.0D) / 2.0D);
            }
        }

        return currentFovModifier;
    }
}