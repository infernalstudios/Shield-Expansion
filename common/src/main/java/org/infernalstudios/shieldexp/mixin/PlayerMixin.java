package org.infernalstudios.shieldexp.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    @Unique
    private static final EntityDataAccessor<Integer> USED_STAMINA = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);

    @Unique
    private static final EntityDataAccessor<ItemStack> LAST_SHIELD = SynchedEntityData.defineId(Player.class, EntityDataSerializers.ITEM_STACK);

    @Unique
    private static final EntityDataAccessor<Boolean> IS_BLOCKING = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void shieldexp$defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(PARRY_COOLDOWN, 0);
        builder.define(BLOCKED_COOLDOWN, 0);
        builder.define(USED_STAMINA, 0);
        builder.define(LAST_SHIELD, new ItemStack(Items.AIR));
        builder.define(IS_BLOCKING, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void shieldexp$tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            if (this.getParryWindow() > 0) this.setParryWindow(this.getParryWindow() - 1);
            if (this.getBlockedCooldown() > 0)
                this.setBlockedCooldown(this.getBlockedCooldown() - 1);
        }
    }

    @Override
    public int getParryWindow() {
        return this.entityData.get(PARRY_COOLDOWN);
    }

    @Override
    public void setParryWindow(int parry) {
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
    public boolean getBlocking() {
        return this.entityData.get(IS_BLOCKING);
    }

    @Override
    public void setBlocking(boolean bool) {
        this.entityData.set(IS_BLOCKING, bool);
    }

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