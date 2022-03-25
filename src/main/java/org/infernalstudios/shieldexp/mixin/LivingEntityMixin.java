/**
 * Copyright 2022 Infernal Studios
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infernalstudios.shieldexp.mixin;

import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	protected ItemStack useItem;

	@Shadow
	protected int useItemRemaining;

	public LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "stopUsingItem", at = @At("HEAD"))
	private void shieldexp$stopUsingItem(CallbackInfo ci) {
		if ((Object) this instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) (Object) this;
			if (useItem.isShield(player)) {
				if (!this.level.isClientSide) {
					if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
						player.getCooldowns().addCooldown(useItem.getItem(), 20);
					}
					LivingEntityAccess.get(player).setParryCooldown(0);
				}
			}
		}
	}

	@Inject(method = "startUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isUsingItem()Z", shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void shieldexp$startUsingItem(Hand hand, CallbackInfo ci, ItemStack stack) {
		if ((Object) this instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) (Object) this;
			if (stack.isShield(player)) {
				if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
					LivingEntityAccess.get(player).setParryCooldown(5);
				}
			}
		}
	}

	@Inject(method = "isBlocking", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private void shieldexp$isBlocking(CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(this.useItem.getItem().getUseDuration(this.useItem) - this.useItemRemaining >= 0);
	}

	@Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/DamageSource;isProjectile()Z"))
	private boolean shieldexp$hurt(DamageSource damageSource) {
		return false;
	}

}
