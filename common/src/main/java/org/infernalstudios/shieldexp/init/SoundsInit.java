package org.infernalstudios.shieldexp.init;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.function.Supplier;

public class SoundsInit {
    public static final Supplier<SoundEvent> PARRY_SOUND = registerSoundEvent("parry_sound");

    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
        Supplier<SoundEvent> sound = Suppliers.memoize(() -> SoundEvent.createVariableRangeEvent(id));

        Services.REGISTRY.registerSound(name, sound);
        return sound;
    }

    public static void init() {
    }
}