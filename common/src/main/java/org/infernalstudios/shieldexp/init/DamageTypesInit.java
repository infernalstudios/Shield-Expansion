package org.infernalstudios.shieldexp.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypesInit {
    public static final ResourceKey<DamageType> PARTIALBLAST = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("shieldexp", "partialblast"));

    public static void bootstrap(BootstapContext<DamageType> context) { context.register(PARTIALBLAST, new DamageType("partialblast", 0.1F)); }
}
