package org.infernalstudios.shieldexp.neoforge.events;

import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.events.ShieldEvents;

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
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        if (ShieldEvents.onLivingHurt(event.getEntity(), event.getSource(), event.getNewDamage())) {
            event.setNewDamage(0);
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
}