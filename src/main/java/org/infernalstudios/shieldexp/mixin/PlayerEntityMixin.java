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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;
import org.infernalstudios.shieldexp.api.ExtendedPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ExtendedPlayerEntity {
    @Unique private static final DataParameter<Integer> PARRY_COOLDOWN = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    @Unique private static final DataParameter<Integer> BLOCKED_COOLDOWN = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    @Unique private static final DataParameter<Integer> USED_STAMINA = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.INT);
    @Unique private static final DataParameter<ItemStack> LAST_SHIELD = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.ITEM_STACK);
    @Unique private static final DataParameter<Boolean> IS_BLOCKING = EntityDataManager.defineId(PlayerEntity.class, DataSerializers.BOOLEAN);

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void shieldexp$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(PARRY_COOLDOWN, 0);
        this.entityData.define(BLOCKED_COOLDOWN, 0);
        this.entityData.define(USED_STAMINA, 0);
        this.entityData.define(LAST_SHIELD, new ItemStack(Items.AIR));
        this.entityData.define(IS_BLOCKING, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void shieldexp$tick(CallbackInfo ci) {
        if (!this.level.isClientSide) {
            if (this.getParryCooldown() > 0) this.setParryCooldown(this.getParryCooldown() - 1);
            if (this.getBlockedCooldown() > 0) this.setBlockedCooldown(this.getBlockedCooldown() - 1);
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

    @Override
    public boolean getBlocking() { return this.entityData.get(IS_BLOCKING); }

    @Override
    public void setBlocking(boolean bool) { this.entityData.set(IS_BLOCKING, bool); }

    @Override
    public int getUsedStamina() {
        return this.entityData.get(USED_STAMINA);
    }

    @Override
    public void setUsedStamina(int stamina) {
        this.entityData.set(USED_STAMINA, stamina);
    }

    @Override
    public ItemStack getLastShield() {
        return this.entityData.get(LAST_SHIELD);
    }

    @Override
    public void setLastShield(ItemStack shield) {
        this.entityData.set(LAST_SHIELD, shield);
    }
}
