package org.infernalstudios.shieldexp.forge.events;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingIncomingDamageEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.infernalstudios.shieldexp.events.TooltipEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.network.SyncShields;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeShieldEvents {

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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ShieldEvents.onPlayerTick(event.player);
        }
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
        if (event.getEntity() instanceof ServerPlayer player) {
            Map<ResourceLocation, JsonElement> shieldMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : ShieldDataLoader.toSync) {
                shieldMap.put(entry.getKey(), entry.getValue());
            }
            Services.NETWORK.sendToPlayer(player, new SyncConfig());
            Services.NETWORK.sendToPlayer(player, new SyncShields(shieldMap));
        }
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        TooltipEvents.addTooltip(event.getItemStack(), event.getEntity(), event.getToolTip());
    }
}