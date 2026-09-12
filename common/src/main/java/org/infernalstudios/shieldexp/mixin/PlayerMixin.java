package org.infernalstudios.shieldexp.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.network.SyncStamina;
import org.infernalstudios.shieldexp.platform.Services;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements LivingEntityAccess {

    @Unique
    private int shieldexp$parryCooldown;

    @Unique
    private int shieldexp$blockedCooldown;

    @Unique
    private int shieldexp$usedStamina;

    @Unique
    private ItemStack shieldexp$lastShield = ItemStack.EMPTY;

    @Unique
    private boolean shieldexp$isBlocking;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void shieldexp$tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            if (this.shieldexp$getParryWindow() > 0) this.shieldexp$setParryWindow(this.shieldexp$getParryWindow() - 1);
            if (this.shieldexp$getBlockedCooldown() > 0)
                this.shieldexp$setBlockedCooldown(this.shieldexp$getBlockedCooldown() - 1);
        }
    }

    @Override
    public int shieldexp$getParryWindow() {
        return this.shieldexp$parryCooldown;
    }

    @Override
    public void shieldexp$setParryWindow(int parry) {
        this.shieldexp$parryCooldown = parry;
    }

    @Override
    public int shieldexp$getBlockedCooldown() {
        return this.shieldexp$blockedCooldown;
    }

    @Override
    public void shieldexp$setBlockedCooldown(int block) {
        this.shieldexp$blockedCooldown = block;
    }

    @Override
    public boolean shieldexp$getBlocking() {
        return this.shieldexp$isBlocking;
    }

    @Override
    public void shieldexp$setBlocking(boolean bool) {
        this.shieldexp$isBlocking = bool;
    }

    @Override
    public int shieldexp$getUsedStamina() {
        return this.shieldexp$usedStamina;
    }

    @Override
    public void shieldexp$setUsedStamina(int stamina) {
        this.shieldexp$usedStamina = stamina;
        if (!this.level().isClientSide && (Object) this instanceof ServerPlayer serverPlayer) {
            Services.NETWORK.sendToPlayer(serverPlayer, new SyncStamina(stamina));
        }
    }

    @Override
    public ItemStack shieldexp$getLastShield() {
        return this.shieldexp$lastShield;
    }

    @Override
    public void shieldexp$setLastShield(ItemStack shield) {
        this.shieldexp$lastShield = shield;
    }
}