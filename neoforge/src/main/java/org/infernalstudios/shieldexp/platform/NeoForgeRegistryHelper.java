package org.infernalstudios.shieldexp.platform;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.platform.services.IRegistryHelper;

import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Constants.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);

    public NeoForgeRegistryHelper() {

    }

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        SOUNDS.register(bus);
    }

    @Override
    public void registerItem(String id, Supplier<Item> itemSupplier) {
        ITEMS.register(id, itemSupplier);
    }

    @Override
    public void registerSound(String id, Supplier<SoundEvent> soundSupplier) {
        SOUNDS.register(id, soundSupplier);
    }
}