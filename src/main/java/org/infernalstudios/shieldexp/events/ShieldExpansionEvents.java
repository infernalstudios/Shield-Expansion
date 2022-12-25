package org.infernalstudios.shieldexp.events;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.items.NewShieldItem;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShieldExpansionEvents {

    @SubscribeEvent
    public void onStartUsing(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getItem();

            if (stack.getItem() instanceof ShieldItem && !player.getCooldowns().isOnCooldown(stack.getItem())) {
                if (!LivingEntityAccess.get(player).getBlocking()) {
                    LivingEntityAccess.get(player).setBlocking(true);

                    if (stack.getItem() instanceof NewShieldItem shield) {
                        AttributeModifier speedModifier = new AttributeModifier(player.getUUID() , "Blocking Speed", (double)(4.0*shield.getSpeedFactor()), AttributeModifier.Operation.MULTIPLY_TOTAL);
                        if (!player.getAttribute(Attributes.MOVEMENT_SPEED).hasModifier(speedModifier)) player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(speedModifier);
                    }
                }

                if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                    if (stack.getItem() instanceof NewShieldItem shield) LivingEntityAccess.get(player).setParryWindow(shield.PARRY_TICKS);
                    else LivingEntityAccess.get(player).setParryWindow(5);
                }
            }
        }
    }

    @SubscribeEvent
    public void onStopUsing(LivingEntityUseItemEvent event) {
        if ((event instanceof LivingEntityUseItemEvent.Stop || event instanceof LivingEntityUseItemEvent.Finish) && event.getEntity() instanceof Player player) {
            ItemStack stack = event.getItem();

            if (stack.getItem() instanceof ShieldItem) {
                if (LivingEntityAccess.get(player).getBlocking()) {
                    if (stack.getItem() instanceof NewShieldItem) {
                        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(player.getUUID());
                    }
                    LivingEntityAccess.get(player).setBlocking(false);
                }

                if (!player.level.isClientSide) {
                    if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                        if (stack.getItem() instanceof NewShieldItem newShield) {
                            player.getCooldowns().addCooldown(stack.getItem(), newShield.getShieldTicks());
                        } else player.getCooldowns().addCooldown(stack.getItem(), 20);
                    }
                    LivingEntityAccess.get(player).setParryWindow(0);
                }
            }
        }
    }

    @SubscribeEvent
    public void onUseTick(LivingEntityUseItemEvent.Tick event) {
        //checks if the player is swinging despite blocking to sync the blocking state after attacking out of the blocking state on the client
        if (event.getEntity() instanceof Player player && event.getItem().getItem() instanceof ShieldItem && LivingEntityAccess.get(player).getBlocking() && player.swinging) {
            if (event.getItem().getItem() instanceof NewShieldItem) {
                player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(player.getUUID());
            }
            LivingEntityAccess.get(player).setBlocking(false);

            if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                if (player.getUseItem().getItem() instanceof NewShieldItem newShield) {
                    player.getCooldowns().addCooldown(player.getUseItem().getItem(), newShield.getShieldTicks());
                } else player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);
            }
            LivingEntityAccess.get(player).setParryWindow(0);
            player.stopUsingItem();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        //check if the player is no longer using a shield
        if (!(event.player.getUseItem().getItem() instanceof ShieldItem)) {
            event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(event.player.getUUID());
        }
        //checks if the player switches to a different inventory slot
        if (!(event.player.getMainHandItem().getItem() instanceof NewShieldItem || event.player.getOffhandItem().getItem() instanceof NewShieldItem) && LivingEntityAccess.get(event.player).getBlocking()) {
            event.player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(event.player.getUUID());
            if (!(event.player.getMainHandItem().getItem() instanceof ShieldItem || event.player.getOffhandItem().getItem() instanceof ShieldItem) && LivingEntityAccess.get(event.player).getBlocking()) {
                LivingEntityAccess.get(event.player).setBlocking(false);

                if (LivingEntityAccess.get(event.player).getBlockedCooldown() <= 0) {
                    if (event.player.getUseItem().getItem() instanceof NewShieldItem newShield) {
                        event.player.getCooldowns().addCooldown(event.player.getUseItem().getItem(), newShield.getShieldTicks());
                    } else event.player.getCooldowns().addCooldown(event.player.getUseItem().getItem(), 20);
                }
                LivingEntityAccess.get(event.player).setParryWindow(0);
                event.player.stopUsingItem();
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();

        if (event.getEntity() instanceof Player player
                && player.getUseItem().getItem() instanceof ShieldItem
                && LivingEntityAccess.get(player).getBlocking()
                && !player.getCooldowns().isOnCooldown(player.getUseItem().getItem())
                && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
            event.getSource().getDirectEntity().playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);

            if (LivingEntityAccess.get(player).getParryWindow() > 0) {
                LivingEntityAccess.get(player).setParryWindow(0);

                if (directEntity instanceof LivingEntity livingEntity) {
                    if (player.getUseItem().getItem() instanceof NewShieldItem shield && shield.getDamageFactor() != 0) livingEntity.hurt(DamageSource.sting(player), (float) (event.getAmount() * shield.getDamageFactor()));
                    livingEntity.knockback(0.55F, directEntity.getDeltaMovement().x, directEntity.getDeltaMovement().z);
                    livingEntity.knockback(0.5F, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
                }

                if (!player.level.isClientSide()) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            } else if (player.getUseItem().getItem() instanceof NewShieldItem newShield) {
                player.getCooldowns().addCooldown(player.getUseItem().getItem(), newShield.getShieldTicks());
            } else player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);

            if (player.getUsedItemHand() == InteractionHand.MAIN_HAND) player.getUseItem().hurtAndBreak(1, player, (player1) -> { player1.broadcastBreakEvent(EquipmentSlot.MAINHAND); removeBlocking(player); });
            else player.getUseItem().hurtAndBreak(1, player, (player1) -> { player1.broadcastBreakEvent(EquipmentSlot.OFFHAND); removeBlocking(player); });
            LivingEntityAccess.get(player).setBlockedCooldown(10);

            removeBlocking(player);

            player.stopUsingItem();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        HitResult rayTraceResult = event.getRayTraceResult();

        if (rayTraceResult instanceof EntityHitResult entityRayTraceResult) {
            if (entityRayTraceResult.getEntity() instanceof Player player) {

                if (player.getUseItem().getItem() instanceof ShieldItem shield && LivingEntityAccess.get(player).getBlocking()  && !player.getCooldowns().isOnCooldown(shield)) {
                    player.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);

                    if (LivingEntityAccess.get(player).getParryWindow() <= 0) {
                        if (player.getUseItem().getItem() instanceof NewShieldItem newShield) {
                            player.getCooldowns().addCooldown(player.getUseItem().getItem(), newShield.getShieldTicks());
                        } else player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);
                    } else {
                        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                        //projectile.syncPacketPositionCodec(projectile.getX(), projectile.getY(), projectile.getZ());
                        LivingEntityAccess.get(player).setParryWindow(0);

                        if (!player.level.isClientSide()) {
                            ServerPlayer serverPlayer = (ServerPlayer) player;
                            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                        }
                    }

                    if (player.getUsedItemHand() == InteractionHand.MAIN_HAND) player.getUseItem().hurtAndBreak(1, player, (player1) -> { player1.broadcastBreakEvent(EquipmentSlot.MAINHAND); removeBlocking(player); });
                    else player.getUseItem().hurtAndBreak(1, player, (player1) -> { player1.broadcastBreakEvent(EquipmentSlot.OFFHAND); removeBlocking(player); });
                    LivingEntityAccess.get(player).setBlockedCooldown(10);

                    removeBlocking(player);

                    player.stopUsingItem();
                    event.setCanceled(true);
                }
            }
        }
    }

    public void removeBlocking(Player player) {
        if (LivingEntityAccess.get(player).getBlocking()) {
            if (player.getUseItem().getItem() instanceof NewShieldItem newShield) {
                player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(player.getUUID());
                LivingEntityAccess.get(player).setBlockedCooldown(newShield.getShieldTicks());
            }
            LivingEntityAccess.get(player).setBlocking(false);
        }
    }
}