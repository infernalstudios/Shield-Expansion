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
package org.infernalstudios.shieldexp.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.init.Config;

import static org.infernalstudios.shieldexp.init.ShieldDataLoader.SHIELD_STATS;
@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShieldExpansionEvents {

    @SubscribeEvent
    public void onStartUsing(LivingEntityUseItemEvent.Start event) {
        Item item = event.getItem().getItem();
        if (Config.isShield(item) && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (player.attackAnim == 0 && !player.getCooldowns().isOnCooldown(item)) {
                int parryTicks = getShieldValue(item, "parryTicks").intValue();
                if (Config.lenientParryEnabled()) parryTicks = parryTicks * 2;
                LivingEntityAccess.get(player).setParryCooldown(parryTicks);
                LivingEntityAccess.get(player).setBlockedCooldown(10);
                LivingEntityAccess.get(player).setUsedStamina(0);
                AttributeModifier speedModifier = new AttributeModifier(player.getUUID(), "Blocking Speed", 4.0 * getShieldValue(item, "speedFactor"), AttributeModifier.Operation.MULTIPLY_TOTAL);
                ModifiableAttributeInstance movementSpeedInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
                if (movementSpeedInstance != null && !movementSpeedInstance.hasModifier(speedModifier) && !Config.speedModifierDisabled())
                    movementSpeedInstance.addTransientModifier(speedModifier);
                if (!LivingEntityAccess.get(player).getBlocking())
                    LivingEntityAccess.get(player).setBlocking(true);
            }
        }
    }

    @SubscribeEvent
    public void onStopUsing(LivingEntityUseItemEvent event) {
        Item item = event.getItem().getItem();
        if ((event instanceof LivingEntityUseItemEvent.Stop || event instanceof LivingEntityUseItemEvent.Finish) && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (Config.isShield(item)) {
                removeBlocking(player);
                if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled())
                    player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
            }
        }
    }

    @SubscribeEvent
    public void onUseTick(LivingEntityUseItemEvent.Tick event) {
        //checks if the player is attacking despite blocking, to sync the blocking state after attacking out of the blocking state on the client
        Item item = event.getItem().getItem();
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (Config.isShield(item) && LivingEntityAccess.get(player).getBlocking() && player.attackAnim > 0) {
                removeBlocking(player);
                if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0 && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled())
                    player.getCooldowns().addCooldown(item, getShieldValue(item, "cooldownTicks").intValue());
                player.stopUsingItem();
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        Item item = player.getUseItem().getItem();
        Item lastShield = LivingEntityAccess.get(player).getLastShield().getItem();

        //check if the player is no longer using a shield
        if (!Config.isShield(item)) removeBlocking(player);

        //checks if the player is in the blocking state without holding a shield
        if (!(Config.isShield(player.getMainHandItem().getItem()) || Config.isShield(player.getOffhandItem().getItem())) && LivingEntityAccess.get(player).getBlocking()) {
            removeBlocking(player);
            player.stopUsingItem();
        }

        //checks if the player switches to a different item
        if (lastShield != item && Config.isShield(lastShield) && !Config.stashingCooldownDisabled() && !Config.cooldownDisabled())
                if (!player.getCooldowns().isOnCooldown(lastShield) && LivingEntityAccess.get(player).getBlockedCooldown() <= 0)
                    player.getCooldowns().addCooldown(lastShield, getShieldValue(lastShield, "cooldownTicks").intValue());
        if (Config.isShield(item)) LivingEntityAccess.get(player).setLastShield(item.getDefaultInstance());
        else LivingEntityAccess.get(player).setLastShield(new ItemStack(Items.AIR));
    }

    //shield behavior when hit by a melee attack
    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (validateBlocking(player) && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
                Item item = player.getUseItem().getItem();
                player.level.playSound(player, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.HOSTILE, 1.0F, 1.0F);
                if (LivingEntityAccess.get(player).getParryCooldown() > 0) {
                    player.level.playSound(null, player.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundCategory.HOSTILE, 1.0f, 1.0f);
                    if (directEntity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) directEntity;
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
    }

    //shield behavior when hit by a projectile
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        if (rayTraceResult instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
            if (entityRayTraceResult.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entityRayTraceResult.getEntity();
                if (!validateBlocking(player)) return;
                Item item = player.getUseItem().getItem();
                player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.HOSTILE, 1.0f, 1.0f);
                if (LivingEntityAccess.get(player).getParryCooldown() > 0) {
                    player.level.playSound(null, player.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundCategory.HOSTILE, 1.0f, 1.0f);
                    projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                    damageItem(player, 1);
                } else {
                    damageItem(player, 1);
                    stamina(player, item, 1);
                }
                event.setCanceled(true);
            }
        }
    }

    //shield behavior when hit by an explosion
    @SubscribeEvent
    public void onExplosionImpact(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (validateBlocking(player) && source.getMsgId().contains("explosion")) {
                Item item = player.getUseItem().getItem();
                double damageFactor = (1.00 - getShieldValue(item, "blastResistance"));
                double usedDurability = event.getAmount() * damageFactor;
                player.level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.HOSTILE, 1.0f, 1.0f);
                if (LivingEntityAccess.get(player).getParryCooldown() > 0) {
                    player.level.playSound(null, player.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundCategory.HOSTILE, 1.0f, 1.0f);
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
                    if (Config.advancedExplosionsEnabled())
                        player.hurt(new DamageSource("partialblast"), (float) (event.getAmount() / 2 * damageFactor));
                }
            }
        }

    }

    //removes the blocking state
    public void removeBlocking(PlayerEntity player) {
        ModifiableAttributeInstance movementSpeedInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedInstance == null) return;
        movementSpeedInstance.removeModifier(player.getUUID());
        if (LivingEntityAccess.get(player).getBlocking())
            LivingEntityAccess.get(player).setBlocking(false);
        LivingEntityAccess.get(player).setParryCooldown(0);
    }

    //returns true if the player is allowed to block
    public boolean validateBlocking(PlayerEntity player) {
        return Config.isShield(player.getUseItem().getItem())
                && LivingEntityAccess.get(player).getBlocking()
                && player.attackAnim == 0
                && !player.getCooldowns().isOnCooldown(player.getUseItem().getItem());
    }

    //reads a shield attribute from the given shield's stats map, or the default map if no map is found
    public static Double getShieldValue(Item item, String value) {
        ResourceLocation itemID = ForgeRegistries.ITEMS.getKey(item);
        if (itemID == null) return SHIELD_STATS.get("shieldexp:default").get(value);
        String key = itemID.toString();
        return SHIELD_STATS.containsKey(key) ? SHIELD_STATS.get(key).get(value) : SHIELD_STATS.get("shieldexp:default").get(value);
    }

    //increases the current used stamina count of the given player, and removes the blocking state if the given shield's stamina is used up
    public void stamina(PlayerEntity player, Item item, int stamina) {
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
    public void damageItem(PlayerEntity player, int amount) {
        player.getUseItem().hurtAndBreak(amount, player, (playerEntity) -> {
            playerEntity.broadcastBreakEvent(player.getUsedItemHand() == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND);
            removeBlocking(player);
            player.stopUsingItem();
        });
    }
}
