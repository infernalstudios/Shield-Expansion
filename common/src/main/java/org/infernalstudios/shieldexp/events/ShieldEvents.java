package org.infernalstudios.shieldexp.events;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.infernalstudios.shieldexp.init.DamageTypesInit;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.init.SoundsInit;

import static org.infernalstudios.shieldexp.init.ShieldDataLoader.SHIELD_STATS;

public class ShieldEvents {

    public static boolean onStartUsing(Entity entity, ItemStack stack) {
        Item item = stack.getItem();
        if (entity instanceof Player player && player.getCooldowns().isOnCooldown(item)) {
            return true;
        }

        if (ShieldExpansionConfig.isShield(item) && entity instanceof Player player && player.attackAnim == 0) {
            int parryTicks = getShieldValue(item, "parryTicks").intValue();
            if (ShieldExpansionConfig.lenientParryEnabled()) parryTicks = parryTicks * 2;

            LivingEntityAccess.get(player).setParryWindow(parryTicks);
            LivingEntityAccess.get(player).setBlockedCooldown(10);
            LivingEntityAccess.get(player).setUsedStamina(0);

            AttributeModifier speedModifier = new AttributeModifier(player.getUUID() , "Blocking Speed", 4.0 * getShieldValue(item, "speedFactor"), AttributeModifier.Operation.MULTIPLY_TOTAL);

            if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(speedModifier) && ShieldExpansionConfig.speedModifierEnabled())
                player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(speedModifier);

            if (!LivingEntityAccess.get(player).getBlocking())
                LivingEntityAccess.get(player).setBlocking(true);
        }
        return false;
    }

    public static void onStopUsing(Entity entity, ItemStack stack) {
        Item item = stack.getItem();
        if (entity instanceof Player player && ShieldExpansionConfig.isShield(item)) {
            removeBlocking(player);
            if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && ShieldExpansionConfig.stashingCooldownEnabled() && ShieldExpansionConfig.cooldownEnabled())
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
        }
    }

    public static void onUseTick(Entity entity, ItemStack stack) {
        Item item = stack.getItem();
        if (entity instanceof Player player && ShieldExpansionConfig.isShield(item) && LivingEntityAccess.get(player).getBlocking() && player.attackAnim > 0) {
            removeBlocking(player);
            if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && ShieldExpansionConfig.stashingCooldownEnabled() && ShieldExpansionConfig.cooldownEnabled())
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
            player.stopUsingItem();
        }
    }

    public static void onPlayerTick(Player player) {
        Item item = player.getUseItem().getItem();
        Item lastShield = LivingEntityAccess.get(player).getLastShield().getItem();

        if (!ShieldExpansionConfig.isShield(item)) {
            removeBlocking(player);
        }

        if (!(ShieldExpansionConfig.isShield(player.getMainHandItem().getItem()) || ShieldExpansionConfig.isShield(player.getOffhandItem().getItem())) && LivingEntityAccess.get(player).getBlocking()) {
            removeBlocking(player);
            player.stopUsingItem();
        }

        if (lastShield != item && ShieldExpansionConfig.isShield(lastShield) && ShieldExpansionConfig.stashingCooldownEnabled() && ShieldExpansionConfig.cooldownEnabled() && player.isUsingItem())
            if (!player.getCooldowns().isOnCooldown(lastShield) && LivingEntityAccess.get(player).getBlockedCooldown() <= 0)
                player.getCooldowns().addCooldown(lastShield, getShieldValue(lastShield, "cooldownTicks").intValue());

        if (ShieldExpansionConfig.isShield(item)) LivingEntityAccess.get(player).setLastShield(item.getDefaultInstance());
        else LivingEntityAccess.get(player).setLastShield(new ItemStack(Items.AIR));
    }

    public static boolean onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof Player player)) return false;

        if (validateBlocking(player) && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
            Item item = player.getUseItem().getItem();
            player.level().playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);

            if (!player.level().isClientSide) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) player, source, amount, 0.0F, true);
            }

            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                player.level().playSound(null, player.getOnPos(), SoundsInit.PARRY_SOUND.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
                Entity directEntity = source.getDirectEntity();
                if (directEntity instanceof LivingEntity livingEntity) {
                    if (ShieldExpansionConfig.isShield(item) && getShieldValue(item, "parryDamage") != 0)
                        livingEntity.hurt(livingEntity.damageSources().sting(player), amount * getShieldValue(item, "parryDamage").floatValue() + getShieldValue(item, "flatDamage").floatValue());
                    livingEntity.knockback(0.55F, directEntity.getDeltaMovement().x, directEntity.getDeltaMovement().z);
                    livingEntity.knockback(0.5F, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
                }
                damageItem(player, 1);
            } else {
                damageItem(player, (int) amount);
                if (amount > 5) stamina(player, item, 3);
                else if (amount > 0) stamina(player, item, 2);
            }
            return true;
        }

        if (validateBlocking(player) && source.getMsgId().contains("explosion")) {
            handleExplosion(player, source, amount);
            return true;
        }

        return false;
    }

    private static void handleExplosion(Player player, DamageSource source, float amount) {
        Item item = player.getUseItem().getItem();
        double damageFactor = (1.00 - getShieldValue(item, "blastResistance"));
        double usedDurability = amount * damageFactor;
        player.level().playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);

        float damageTaken = 0.0F;
        if (LivingEntityAccess.get(player).getParryWindow() > 0) {
            player.level().playSound(null, player.getOnPos(), SoundsInit.PARRY_SOUND.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
            damageItem(player, (int) usedDurability);
            if (ShieldExpansionConfig.cooldownEnabled()) {
                if (amount >= 15) {
                    if (damageFactor > 0) {
                        player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() * damageFactor));
                        removeBlocking(player);
                        player.stopUsingItem();
                    }
                } else if (amount >= 10) {
                    player.getCooldowns().addCooldown(item, (int) (((double) getShieldValue(item, "cooldownTicks").intValue() / 2) * damageFactor));
                    removeBlocking(player);
                    player.stopUsingItem();
                } else if (amount > 5) stamina(player, item, (int) (3 * damageFactor));
                else if (amount > 0) stamina(player, item, (int) (2 * damageFactor));
            }
        } else {
            damageItem(player, (int) usedDurability);
            if (ShieldExpansionConfig.cooldownEnabled()) {
                if (amount >= 15) {
                    player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() + getShieldValue(item, "cooldownTicks").intValue() * 2 * damageFactor));
                    removeBlocking(player);
                    player.stopUsingItem();
                } else if (amount >= 10) {
                    player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() + getShieldValue(item, "cooldownTicks").intValue() * damageFactor));
                    removeBlocking(player);
                    player.stopUsingItem();
                } else if (amount > 5) {
                    player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
                    removeBlocking(player);
                    player.stopUsingItem();
                } else if (amount > 0) stamina(player, item, 3);
            }

            if (ShieldExpansionConfig.advancedExplosionsEnabled()) {
                damageTaken = (float) (amount / 2 * damageFactor);
                player.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypesInit.PARTIALBLAST)), damageTaken);
            }
        }

        if (!player.level().isClientSide) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) player, source, amount, damageTaken, true);
        }
    }

    public static boolean onProjectileImpact(Entity entity, Entity projectile) {
        if (entity instanceof Player player && validateBlocking(player)) {
            Item item = player.getUseItem().getItem();
            player.level().playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);

            if (!player.level().isClientSide) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((ServerPlayer) player, new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.ARROW)), 0.0F, 0.0F, true);
            }

            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                player.level().playSound(null, player.getOnPos(), SoundsInit.PARRY_SOUND.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
                projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                projectile.syncPacketPositionCodec(projectile.getX(), projectile.getY(), projectile.getZ());
                damageItem(player, 1);
            } else {
                damageItem(player, 1);
                stamina(player, item, 1);
            }
            return true;
        }
        return false;
    }

    public static void removeBlocking(Player player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(player.getUUID());
        if (LivingEntityAccess.get(player).getBlocking())
            LivingEntityAccess.get(player).setBlocking(false);
        LivingEntityAccess.get(player).setParryWindow(0);
    }

    public static boolean validateBlocking(Player player) {
        return ShieldExpansionConfig.isShield(player.getUseItem().getItem())
                && LivingEntityAccess.get(player).getBlocking()
                && player.attackAnim == 0
                && !player.getCooldowns().isOnCooldown(player.getUseItem().getItem());
    }

    public static Double getShieldValue(Item item, String value) {
        String key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item).toString();
        return SHIELD_STATS.containsKey(key) ? SHIELD_STATS.get(key).get(value) : SHIELD_STATS.get(ShieldDataLoader.DEFAULT_SHIELD_NAME).get(value);
    }

    public static void stamina(Player player, Item item, int stamina) {
        if (ShieldExpansionConfig.cooldownEnabled()) {
            LivingEntityAccess.get(player).setUsedStamina(LivingEntityAccess.get(player).getUsedStamina() + stamina);
            int maxStamina = getShieldValue(item, "stamina").intValue();
            if (ShieldExpansionConfig.lenientStaminaEnabled()) maxStamina = maxStamina * 2;
            if (LivingEntityAccess.get(player).getUsedStamina() >= maxStamina) {
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
                removeBlocking(player);
                player.stopUsingItem();
            }
        }
    }

    public static void damageItem(Player player, int amount) {
        player.getUseItem().hurtAndBreak(amount, player, (player1) -> {
            player1.broadcastBreakEvent(player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            removeBlocking(player);
            player.stopUsingItem();
        });
    }
}