package org.infernalstudios.shieldexp.events;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShieldExpansionEvents {

    @SubscribeEvent
    public void onStartUsing(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack stack = event.getItem();

            if (stack.is(Items.SHIELD)) {
                if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                    LivingEntityAccess.get(player).setParryCooldown(5);
                }
            }
        }
    }

    @SubscribeEvent
    public void onStopUsing(LivingEntityUseItemEvent event) {

        if ((event instanceof LivingEntityUseItemEvent.Stop || event instanceof LivingEntityUseItemEvent.Finish) && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack stack = event.getItem();

            if (stack.is(Items.SHIELD)) {
                if (!player.level.isClientSide) {
                    if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                        player.getCooldowns().addCooldown(stack.getItem(), 20);
                    }
                    LivingEntityAccess.get(player).setParryCooldown(0);
                }
            }
        }

    }

    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity directEntity = source.getDirectEntity();

        if (event.getEntity() instanceof Player && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
            Player player = (Player) event.getEntity();

            if (player.getUseItem().is(Items.SHIELD)) {
                event.getSource().getDirectEntity().playSound(SoundEvents.SHIELD_BLOCK, 1, 1);
                if (LivingEntityAccess.get(player).getParryCooldown() <= 0) {
                    player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);
                } else {
                    if (directEntity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) directEntity;
                        livingEntity.knockback(0.55F, directEntity.getDeltaMovement().x, directEntity.getDeltaMovement().z);
                        livingEntity.knockback(0.5F, player.getX() - livingEntity.getX(), player.getZ() - livingEntity.getZ());
                    }
                    LivingEntityAccess.get(player).setParryCooldown(0);

                    if (!player.level.isClientSide()) {
                        ServerPlayer serverPlayer = (ServerPlayer) player;
                        serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                    }
                }

                LivingEntityAccess.get(player).setBlockedCooldown(10);
                player.stopUsingItem();

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getEntity();
        HitResult rayTraceResult = event.getRayTraceResult();

        if (rayTraceResult instanceof EntityHitResult) {
            EntityHitResult entityRayTraceResult = (EntityHitResult) rayTraceResult;
            if (entityRayTraceResult.getEntity() instanceof Player) {
                Player player = (Player) entityRayTraceResult.getEntity();

                if (player.getUseItem().is(Items.SHIELD)) {
                    player.playSound(SoundEvents.SHIELD_BLOCK, 1, 1);
                    if (LivingEntityAccess.get(player).getParryCooldown() <= 0) {
                        player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);
                    } else {
                        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                        LivingEntityAccess.get(player).setParryCooldown(0);

                        if (!player.level.isClientSide()) {
                            ServerPlayer serverPlayer = (ServerPlayer) player;
                            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                        }
                    }

                    LivingEntityAccess.get(player).setBlockedCooldown(10);
                    player.stopUsingItem();

                    event.setCanceled(true);
                }
            }
        }
    }

}