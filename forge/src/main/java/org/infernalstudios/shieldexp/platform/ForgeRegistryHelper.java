package org.infernalstudios.shieldexp.platform;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.platform.services.IRegistryHelper;

import java.util.function.Supplier;

public class ForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MOD_ID);

    public ForgeRegistryHelper() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
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