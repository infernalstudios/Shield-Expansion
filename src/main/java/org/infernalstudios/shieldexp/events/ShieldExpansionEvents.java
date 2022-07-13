package org.infernalstudios.shieldexp.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
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
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack stack = event.getItem();

            if (stack.isShield(player)) {
                if (LivingEntityAccess.get(player).getBlockedCooldown() <= 0) {
                    LivingEntityAccess.get(player).setParryCooldown(5);
                }
            }
        }
    }

    @SubscribeEvent
    public void onStopUsing(LivingEntityUseItemEvent event) {

        if (event instanceof LivingEntityUseItemEvent.Stop || event instanceof LivingEntityUseItemEvent.Finish && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            ItemStack stack = event.getItem();

            if (stack.isShield(player)) {
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

        if (event.getEntity() instanceof PlayerEntity && (source.getMsgId().equals("player") || source.getMsgId().equals("mob"))) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            if (player.getUseItem().isShield(player)) {
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
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                            serverPlayer.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
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
        RayTraceResult rayTraceResult = event.getRayTraceResult();

        if (rayTraceResult instanceof EntityRayTraceResult) {
            EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
            if (entityRayTraceResult.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entityRayTraceResult.getEntity();

                if (player.getUseItem().isShield(player)) {
                    if (LivingEntityAccess.get(player).getParryCooldown() <= 0) {
                        player.getCooldowns().addCooldown(player.getUseItem().getItem(), 20);
                    } else {
                        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.0D));
                        LivingEntityAccess.get(player).setParryCooldown(0);

                        if (!player.level.isClientSide()) {
                            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                            serverPlayer.connection.send(new SChangeGameStatePacket(SChangeGameStatePacket.ARROW_HIT_PLAYER, 0.0F));
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
