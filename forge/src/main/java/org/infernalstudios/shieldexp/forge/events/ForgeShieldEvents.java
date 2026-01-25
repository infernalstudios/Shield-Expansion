package org.infernalstudios.shieldexp.forge.events;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.infernalstudios.shieldexp.events.TooltipEvents;

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
    public static void onLivingHurt(LivingAttackEvent event) {
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
    public static void onTooltip(ItemTooltipEvent event) {
        TooltipEvents.addTooltip(event.getItemStack(), event.getEntity(), event.getToolTip());
    }
}