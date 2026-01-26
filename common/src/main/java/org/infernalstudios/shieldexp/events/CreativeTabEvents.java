package org.infernalstudios.shieldexp.events;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.shieldexp.init.ItemsInit;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.function.Consumer;

public class CreativeTabEvents {

    private static final ResourceKey<CreativeModeTab> COMBAT_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            ResourceLocation.parse("combat")
    );

    public static void onBuildTabContents(ResourceKey<CreativeModeTab> tabKey, Consumer<ItemStack> acceptor) {
        if (tabKey.equals(COMBAT_TAB)) {
            acceptor.accept(new ItemStack(ItemsInit.WOODEN_SHIELD.get()));
            acceptor.accept(new ItemStack(ItemsInit.IRON_SHIELD.get()));
            acceptor.accept(new ItemStack(ItemsInit.GOLDEN_SHIELD.get()));
            acceptor.accept(new ItemStack(ItemsInit.DIAMOND_SHIELD.get()));
            acceptor.accept(new ItemStack(ItemsInit.NETHERITE_SHIELD.get()));

            if (Services.PLATFORM.isModLoaded("miningmaster"))
                acceptor.accept(new ItemStack(ItemsInit.PARAGON_SHIELD.get()));
            if (Services.PLATFORM.isModLoaded("savage_and_ravage"))
                acceptor.accept(new ItemStack(ItemsInit.GRIEFER_SHIELD.get()));
        }
    }
}