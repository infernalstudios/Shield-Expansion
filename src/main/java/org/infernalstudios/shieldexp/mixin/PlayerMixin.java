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

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements LivingEntityAccess {

    @Unique
    private static final EntityDataAccessor<Integer> PARRY_COOLDOWN = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<Integer> BLOCKED_COOLDOWN = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
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

}