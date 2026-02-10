package org.infernalstudios.shieldexp.neoforge.events;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.network.SyncShields;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeShieldEvents {

    @SubscribeEvent
    public static void onStartUsing(LivingEntityUseItemEvent.Start event) {
        if (ShieldEvents.onStartUsing(event.getEntity(), event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onStopUsing(LivingEntityUseItemEvent.Stop event) {
        ShieldEvents.onStopUsing(event.getEntity(), event.getItem());
    }

    @SubscribeEvent
    public static void onFinishUsing(LivingEntityUseItemEvent.Finish event) {
        ShieldEvents.onStopUsing(event.getEntity(), event.getItem());
    }

    @SubscribeEvent
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        ShieldEvents.onUseTick(event.getEntity(), event.getItem());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        ShieldEvents.onPlayerTick(event.getEntity());
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (ShieldEvents.onLivingHurt(event.getEntity(), event.getSource(), event.getAmount())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() instanceof EntityHitResult entityHitResult) {
            if (ShieldEvents.onProjectileImpact(entityHitResult.getEntity(), event.getEntity())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            Services.NETWORK.sendToPlayer(serverPlayer, new SyncConfig());

            Map<ResourceLocation, JsonElement> shieldMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : ShieldDataLoader.toSync) {
                shieldMap.put(entry.getKey(), entry.getValue());
            }
            Services.NETWORK.sendToPlayer(serverPlayer, new SyncShields(shieldMap));
        }
    }
}