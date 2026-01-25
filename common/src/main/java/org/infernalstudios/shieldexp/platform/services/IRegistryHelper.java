package org.infernalstudios.shieldexp.platform.services;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public interface IRegistryHelper {
    void registerItem(String id, Supplier<Item> itemSupplier);
    void registerSound(String id, Supplier<SoundEvent> soundSupplier);
}