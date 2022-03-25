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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.CooldownTracker;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LivingEntityAccess {

	@Unique
	private static final DataParameter<Integer> PARRY_COOLDOWN = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);

	@Unique
	private static final DataParameter<Integer> BLOCKED_COOLDOWN = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "blockUsingShield", at = @At("HEAD"))
	private void shieldexp$blockUsingShield(LivingEntity attacker, CallbackInfo ci) {
		// TODO: Configure 'Attacking Cooldown Percentage'
		float attackCooldown = 1.0F; // 100%
		if (this.getUseItem().isShield(this)) {
			if (this.random.nextFloat() < attackCooldown) {
				if (this.getParryCooldown() <= 0) {
					this.getCooldowns().addCooldown(this.getUseItem().getItem(), 20);
				} else {
					attacker.knockback(0.55F, attacker.getDeltaMovement().x, attacker.getDeltaMovement().z);
					this.setParryCooldown(0);
					if (!this.level.isClientSide) {
						((ServerPlayerEntity) (Object) this).connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
					}
				}
				this.setBlockedCooldown(10);
				this.stopUsingItem();
			}
		}
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void shieldexp$defineSynchedData(CallbackInfo ci) {
		this.entityData.define(PARRY_COOLDOWN, 0);
		this.entityData.define(BLOCKED_COOLDOWN, 0);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void shieldexp$tick(CallbackInfo ci) {
		if (!this.level.isClientSide) {
			if (this.getParryCooldown() > 0) {
				this.setParryCooldown(this.getParryCooldown() - 1);
			}
			if (this.getBlockedCooldown() > 0) {
				this.setBlockedCooldown(this.getBlockedCooldown() - 1);
			}
		}
	}

	@Override
	public int getParryCooldown() {
		return this.entityData.get(PARRY_COOLDOWN);
	}

	@Override
	public void setParryCooldown(int parry) {
		this.entityData.set(PARRY_COOLDOWN, parry);
	}

	@Override
	public int getBlockedCooldown() {
		return this.entityData.get(BLOCKED_COOLDOWN);
	}

	@Override
	public void setBlockedCooldown(int block) {
		this.entityData.set(BLOCKED_COOLDOWN, block);
	}

	@Shadow
	public abstract CooldownTracker getCooldowns();

}
