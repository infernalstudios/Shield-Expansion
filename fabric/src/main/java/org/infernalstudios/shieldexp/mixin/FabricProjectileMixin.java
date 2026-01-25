package org.infernalstudios.shieldexp.mixin;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class FabricProjectileMixin {
    @Inject(method = "onHit", at = @At("HEAD"))
    private void onProjectileHit(HitResult result, CallbackInfo ci) {
        if (result instanceof EntityHitResult entityHit) {
            ShieldEvents.onProjectileImpact(entityHit.getEntity(), (Projectile) (Object) this);
        }
    }
}