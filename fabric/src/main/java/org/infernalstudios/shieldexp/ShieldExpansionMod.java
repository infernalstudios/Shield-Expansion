package org.infernalstudios.shieldexp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.infernalstudios.shieldexp.events.CreativeTabEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

public class ShieldExpansionMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricShieldDataLoader());

        ItemGroupEvents.modifyEntriesEvent(net.minecraft.world.item.CreativeModeTabs.COMBAT).register(entries -> {
            CreativeTabEvents.onBuildTabContents(net.minecraft.world.item.CreativeModeTabs.COMBAT, entries::accept);
        });
    }

    private static class FabricShieldDataLoader extends ShieldDataLoader implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shields");
        }
    }
}