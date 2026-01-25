package org.infernalstudios.shieldexp.mixin;

import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class FabricPlayerTickMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onPlayerTick(CallbackInfo ci) {
        ShieldEvents.onPlayerTick((Player) (Object) this);
    }
}