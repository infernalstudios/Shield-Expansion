package org.infernalstudios.shieldexp;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.infernalstudios.shieldexp.events.CreativeTabEvents;
import org.infernalstudios.shieldexp.forge.client.ForgeClientEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

@Mod(Constants.MOD_ID)
public class ShieldExpansionMod {
    public ShieldExpansionMod() {
        CommonClass.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);

        if (ModList.get().isLoaded("cloth_config")) {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ForgeClientEvents::registerConfigScreen);
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