package org.infernalstudios.shieldexp.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.compat.BetterCombatAttackListener;
import org.infernalstudios.shieldexp.config.ClothConfigIntegration;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.events.FovEvents;
import org.infernalstudios.shieldexp.platform.Services;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientEvents.initShields();
            ClientEvents.copyOptionalResourcePackIfMissing();

            if (Services.PLATFORM.isModLoaded("bettercombat")) {
                BetterCombatAttackListener.register();
            }
        });

        MinecraftForge.EVENT_BUS.register(ForgeClientRuntimeEvents.class);
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> ClothConfigIntegration.createScreen(parent)
                )
        );
    }

    public static class ForgeClientRuntimeEvents {
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                ClientEvents.onClientTick();
            }
        }

        @SubscribeEvent
        public static void onFovModify(ComputeFovModifierEvent event) {
            float newFov = FovEvents.onComputeFov(event.getPlayer(), event.getNewFovModifier());
            if (newFov != event.getNewFovModifier()) {
                event.setNewFovModifier(newFov);
            }
        }
    }
}