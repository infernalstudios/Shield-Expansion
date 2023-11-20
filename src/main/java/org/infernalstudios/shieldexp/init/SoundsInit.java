package org.infernalstudios.shieldexp.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.shieldexp.ShieldExpansion;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SoundsInit {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ShieldExpansion.MOD_ID);

    public static final RegistryObject<SoundEvent> PARRY_SOUND = registerSoundEvent("parry_sound");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(ShieldExpansion.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> new SoundEvent(id));
    }
}