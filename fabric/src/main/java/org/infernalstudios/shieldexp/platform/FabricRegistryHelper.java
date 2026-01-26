package org.infernalstudios.shieldexp.platform;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.platform.services.IRegistryHelper;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public void registerItem(String id, Supplier<Item> itemSupplier) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, id), itemSupplier.get());
    }

    @Override
    public void registerSound(String id, Supplier<SoundEvent> soundSupplier) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, id), soundSupplier.get());
    }
}