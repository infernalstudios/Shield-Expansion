package org.infernalstudios.shieldexp.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ConstantConditions")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    protected ItemStack useItem;

    @Shadow
    protected int useItemRemaining;

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "isBlocking", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void shieldexp$isBlocking(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(this.useItem.getItem().getUseDuration(this.useItem, (LivingEntity) (Object) this) - this.useItemRemaining >= 0);
    }
}