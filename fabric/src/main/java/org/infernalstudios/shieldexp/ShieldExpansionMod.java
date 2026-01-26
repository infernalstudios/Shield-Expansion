package org.infernalstudios.shieldexp;

import com.google.gson.JsonElement;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.infernalstudios.shieldexp.events.CreativeTabEvents;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.network.SyncShields;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.HashMap;
import java.util.Map;

public class ShieldExpansionMod implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricShieldDataLoader());

        ItemGroupEvents.modifyEntriesEvent(net.minecraft.world.item.CreativeModeTabs.COMBAT).register(entries -> {
            CreativeTabEvents.onBuildTabContents(net.minecraft.world.item.CreativeModeTabs.COMBAT, entries::accept);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Map<ResourceLocation, JsonElement> shieldMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : ShieldDataLoader.toSync) {
                shieldMap.put(entry.getKey(), entry.getValue());
            }
            Services.NETWORK.sendToPlayer(handler.player, new SyncConfig());
            Services.NETWORK.sendToPlayer(handler.player, new SyncShields(shieldMap));
        });
    }

    private static class FabricShieldDataLoader extends ShieldDataLoader implements IdentifiableResourceReloadListener {
        @Override
        public ResourceLocation getFabricId() {
            return new ResourceLocation(Constants.MOD_ID, "shields");
        }
    }
}