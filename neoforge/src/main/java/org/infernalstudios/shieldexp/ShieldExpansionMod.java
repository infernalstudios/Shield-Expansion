package org.infernalstudios.shieldexp;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.infernalstudios.shieldexp.events.CreativeTabEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.neoforge.client.NeoForgeClientEvents;
import org.infernalstudios.shieldexp.platform.NeoForgeNetworkHelper;
import org.infernalstudios.shieldexp.platform.NeoForgeRegistryHelper;

@Mod(Constants.MOD_ID)
public class ShieldExpansionMod {

    public ShieldExpansionMod(IEventBus modEventBus) {
        CommonClass.init();

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(NeoForgeNetworkHelper::registerPayloads);

        NeoForge.EVENT_BUS.register(this);
        NeoForgeRegistryHelper.init(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForgeClientEvents.init(modEventBus);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        CreativeTabEvents.onBuildTabContents(event.getTabKey(), event::accept);
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ShieldDataLoader());
    }
}