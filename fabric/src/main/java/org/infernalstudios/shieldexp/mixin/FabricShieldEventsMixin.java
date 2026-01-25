package org.infernalstudios.shieldexp.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class FabricShieldEventsMixin extends Entity {

    public FabricShieldEventsMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getUseItem();

    @Inject(method = "startUsingItem", at = @At("HEAD"))
    private void onStartUsing(InteractionHand hand, CallbackInfo ci) {
        ShieldEvents.onStartUsing(this, ((LivingEntity) (Object) this).getItemInHand(hand));
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void onStopUsing(CallbackInfo ci) {
        ShieldEvents.onStopUsing(this, this.getUseItem());
    }

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void onUseTick(ItemStack stack, CallbackInfo ci) {
        ShieldEvents.onUseTick(this, stack);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (ShieldEvents.onLivingHurt((LivingEntity) (Object) this, source, amount)) {
            cir.setReturnValue(false);
        }
    }
}