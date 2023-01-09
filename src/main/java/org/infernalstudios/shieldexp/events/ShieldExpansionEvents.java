package org.infernalstudios.shieldexp.events;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.init.Config;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;

import static org.infernalstudios.shieldexp.init.ShieldDataLoader.SHIELD_STATS;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShieldExpansionEvents {

    @SubscribeEvent
    public void onStartUsing(LivingEntityUseItemEvent.Start event) {
        Item item = event.getItem().getItem();
        if (Config.isShield(item) && event.getEntity() instanceof Player player && player.attackAnim == 0 && !player.getCooldowns().isOnCooldown(item)) {
            int parryTicks = getShieldValue(item, "parryTicks").intValue();
            if (Config.lenientParryEnabled()) parryTicks = parryTicks * 2;
            LivingEntityAccess.get(player).setParryWindow(parryTicks);
            LivingEntityAccess.get(player).setBlockedCooldown(10);
            LivingEntityAccess.get(player).setUsedStamina(0);
            AttributeModifier speedModifier = new AttributeModifier(player.getUUID() , "Blocking Speed", 4.0*getShieldValue(item, "speedFactor"), AttributeModifier.Operation.MULTIPLY_TOTAL);
            if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(speedModifier) && !Config.speedModifierDisabled())
                player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(speedModifier);
            if (!LivingEntityAccess.get(player).getBlocking())
                LivingEntityAccess.get(player).setBlocking(true);
        }
    }

    @SubscribeEvent
    public void onStopUsing(LivingEntityUseItemEvent event) {
        Item item = event.getItem().getItem();
        if ((event instanceof LivingEntityUseItemEvent.Stop || event instanceof LivingEntityUseItemEvent.Finish) && event.getEntity() instanceof Player player && Config.isShield(item)) {
            removeBlocking(player);
            if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled())
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
        }
    }

    @SubscribeEvent
    public void onUseTick(LivingEntityUseItemEvent.Tick event) {
        //checks if the player is attacking despite blocking, to sync the blocking state after attacking out of the blocking state on the client
        Item item = event.getItem().getItem();
        if (event.getEntity() instanceof Player player && Config.isShield(item) && LivingEntityAccess.get(player).getBlocking() && player.attackAnim > 0) {
            removeBlocking(player);
            if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled())
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
            player.stopUsingItem();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Item item = player.getUseItem().getItem();
        Item lastShield = LivingEntityAccess.get(player).getLastShield().getItem();

        //check if the player is no longer using a shield
        if (!Config.isShield(item)) {
            removeBlocking(player);
        }

        //checks if the player is in the blocking state without holding a shield
        if (!(Config.isShield(player.getMainHandItem().getItem()) || Config.isShield(player.getOffhandItem().getItem())) && LivingEntityAccess.get(player).getBlocking()) {
            removeBlocking(player);
            player.stopUsingItem();
        }

        //checks if the player switches to a different item
        if (lastShield != item && Config.isShield(lastShield) && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled()) {
            if (!player.getCooldowns().isOnCooldown(lastShield) && LivingEntityAccess.get(player).getBlockedCooldown() <= 0)
                player.getCooldowns().addCooldown(lastShield, getShieldValue(lastShield, "cooldownTicks").intValue());
            if (Config.isShield(item)) LivingEntityAccess.get(player).setLastShield(item.getDefaultInstance());
            else LivingEntityAccess.get(player).setLastShield(new ItemStack(Items.AIR));
        }
    }

    //shield behavior when hit by a melee attack
    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();
        if (event.getEntity() instanceof Player player && validateBlocking(player) && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
            Item item = player.getUseItem().getItem();
            player.level.playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);
            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                player.level.playSound(null, player.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.HOSTILE, 1.0f, 1.0f);
                if (directEntity instanceof LivingEntity livingEntity) {
                    if (Config.isShield(item) && getShieldValue(item, "parryDamage") != 0)
                        livingEntity.hurt(DamageSource.sting(player), event.getAmount() * getShieldValue(item, "parryDamage").floatValue() + getShieldValue(item, "flatDamage").floatValue());
                    livingEntity.knockback(0.55F, directEntity.getDeltaMovement().x, directEntity.getDeltaMovement().z);
                    livingEntity.knockback(0.5F, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
                }
                damageItem(player, 1);
            } else {
                damageItem(player, (int) event.getAmount());
                if (event.getAmount() > 5) stamina(player, item, 3);
                else if (event.getAmount() > 0) stamina(player, item, 2);
            }
            event.setCanceled(true);
        }
    }

    //shield behavior when hit by a projectile
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        HitResult rayTraceResult = event.getRayTraceResult();
        if (rayTraceResult instanceof EntityHitResult entityRayTraceResult && entityRayTraceResult.getEntity() instanceof Player player && validateBlocking(player)) {
            Item item = player.getUseItem().getItem();
            player.level.playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);
            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                player.level.playSound(null, player.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.HOSTILE, 1.0f, 1.0f);
                projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                projectile.syncPacketPositionCodec(projectile.getX(), projectile.getY(), projectile.getZ());
                damageItem(player, 1);
            } else {
                damageItem(player, 1);
                stamina(player, item, 1);
            }
            event.setCanceled(true);
        }
    }

    //shield behavior when hit by an explosion
    @SubscribeEvent
    public void onExplosionImpact(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        if (event.getEntity() instanceof Player player && validateBlocking(player) && source.getMsgId().contains("explosion")) {
            Item item = player.getUseItem().getItem();
            double damageFactor = (1.00 - getShieldValue(item, "blastResistance"));
            double usedDurability = event.getAmount() * damageFactor;
            player.level.playSound(null, player.getOnPos(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 1.0f, 1.0f);
            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                player.level.playSound(null, player.getOnPos(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.HOSTILE, 1.0f, 1.0f);
                damageItem(player, (int) usedDurability);
                if (!Config.cooldownDisabled()) {
                    if (event.getAmount() >= 15) {
                        if (damageFactor > 0) {
                            player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() * damageFactor));
                            removeBlocking(player);
                            player.stopUsingItem();
                        }
                    } else if (event.getAmount() >= 10) {
                        player.getCooldowns().addCooldown(item, (int) ((getShieldValue(item, "cooldownTicks").intValue() / 2) * damageFactor));
                        removeBlocking(player);
                        player.stopUsingItem();
                    } else if (event.getAmount() > 5) stamina(player, item, (int) (3 * damageFactor));
                    else if (event.getAmount() > 0) stamina(player, item, (int) (2 * damageFactor));
                }
                event.setCanceled(true);
            } else {
                damageItem(player, (int) usedDurability);
                if (!Config.cooldownDisabled()) {
                    if (event.getAmount() >= 15) {
                        player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() + getShieldValue(item, "cooldownTicks").intValue() * 2 * damageFactor));
                        removeBlocking(player);
                        player.stopUsingItem();
                    } else if (event.getAmount() >= 10) {
                        player.getCooldowns().addCooldown(item, (int) (getShieldValue(item, "cooldownTicks").intValue() + getShieldValue(item, "cooldownTicks").intValue() * damageFactor));
                        removeBlocking(player);
                        player.stopUsingItem();
                    } else if (event.getAmount() > 5) {
                        player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
                        removeBlocking(player);
                        player.stopUsingItem();
                    } else if (event.getAmount() > 0) stamina(player, item, 3);
                }
                event.setCanceled(true);
                if (Config.advancedExplosionsEnabled()) player.hurt(new DamageSource("partialblast"), (float) (event.getAmount() / 2 * damageFactor));
            }
        }
    }

    //removes the blocking state
    public void removeBlocking(Player player) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(player.getUUID());
        if (LivingEntityAccess.get(player).getBlocking())
            LivingEntityAccess.get(player).setBlocking(false);
        LivingEntityAccess.get(player).setParryWindow(0);
    }

    //returns true if the player is allowed to block
    public boolean validateBlocking(Player player) {
        return Config.isShield(player.getUseItem().getItem())
                && LivingEntityAccess.get(player).getBlocking()
                && player.attackAnim == 0
                && !player.getCooldowns().isOnCooldown(player.getUseItem().getItem());
    }

    //reads a shield attribute from the given shield's stats map, or the default map if no map is found
    public static Double getShieldValue(Item item, String value) {
        String key = ForgeRegistries.ITEMS.getKey(item).toString();
        return SHIELD_STATS.containsKey(key) ? SHIELD_STATS.get(key).get(value) : SHIELD_STATS.get("shieldexp:default").get(value);
    }

    //increases the current used stamina count of the given player, and removes the blocking state if the given shield's stamina is used up
    public void stamina(Player player, Item item, int stamina) {
        if (!Config.cooldownDisabled()) {
            LivingEntityAccess.get(player).setUsedStamina(LivingEntityAccess.get(player).getUsedStamina() + stamina);
            int maxStamina = getShieldValue(item, "stamina").intValue();
            if (Config.lenientStaminaEnabled()) maxStamina = maxStamina * 2;
            if (LivingEntityAccess.get(player).getUsedStamina() >= maxStamina) {
                player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
                removeBlocking(player);
                player.stopUsingItem();
            }
        }
    }

    //reduces durability of the given player's used shield, and removes the blocking state if it breaks
    public void damageItem(Player player, int amount) {
        player.getUseItem().hurtAndBreak(amount, player, (player1) -> {
            player1.broadcastBreakEvent(player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            removeBlocking(player);
            player.stopUsingItem();
        });
    }
}