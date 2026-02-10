package org.infernalstudios.shieldexp.events;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;

public class FovEvents {

    public static float onComputeFov(Player player, float currentFovModifier) {
        if (ShieldExpansionConfig.ITEM_ONLY_MODE) return currentFovModifier;

        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            AttributeModifier shieldMod = speedAttribute.getModifier(ShieldEvents.SPEED_MODIFIER_ID);

            if (shieldMod != null) {
                double currentSpeed = speedAttribute.getValue();
                double modifierScale = 1.0D + shieldMod.getAmount();
                double speedWithoutShield = currentSpeed / modifierScale;

                return (float) ((speedWithoutShield / (double) player.getAbilities().getWalkingSpeed() + 1.0D) / 2.0D);
            }
        }

        return currentFovModifier;
    }
}