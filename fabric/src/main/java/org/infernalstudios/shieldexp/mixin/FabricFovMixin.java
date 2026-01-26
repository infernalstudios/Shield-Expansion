package org.infernalstudios.shieldexp.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.events.FovEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class FabricFovMixin {

    @Inject(method = "getFieldOfViewModifier", at = @At("RETURN"), cancellable = true)
    private void shieldexp$onComputeFov(CallbackInfoReturnable<Float> cir) {
        float newFov = FovEvents.onComputeFov((Player) (Object) this, cir.getReturnValue());
        cir.setReturnValue(newFov);
    }
}