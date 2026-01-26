package org.infernalstudios.shieldexp.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.compat.BetterCombatAttackListener;
import org.infernalstudios.shieldexp.config.ClothConfigIntegration;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.events.FovEvents;
import org.infernalstudios.shieldexp.events.TooltipEvents;
import org.infernalstudios.shieldexp.platform.Services;

@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class NeoForgeClientEvents {

    public static void init(IEventBus modBus) {
        modBus.addListener(NeoForgeClientEvents::onClientSetup);
        NeoForge.EVENT_BUS.register(ForgeClientRuntimeEvents.class);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientEvents.initShields();
            ClientEvents.copyOptionalResourcePackIfMissing();

            if (Services.PLATFORM.isModLoaded("bettercombat")) {
                BetterCombatAttackListener.register();
            }
        });
        registerConfigScreen();
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> ClothConfigIntegration.createScreen(parent)
        );
    }

    public static class ForgeClientRuntimeEvents {

        @SubscribeEvent
        public static void onFovModify(ComputeFovModifierEvent event) {
            float newFov = FovEvents.onComputeFov(event.getPlayer(), event.getNewFovModifier());
            if (newFov != event.getNewFovModifier()) {
                event.setNewFovModifier(newFov);
            }
        }

        @SubscribeEvent
        public static void onTooltip(ItemTooltipEvent event) {
            TooltipEvents.addTooltip(event.getItemStack(), event.getEntity(), event.getToolTip());
        }
    }
}