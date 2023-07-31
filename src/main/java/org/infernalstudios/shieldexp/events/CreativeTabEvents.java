package org.infernalstudios.shieldexp.events;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.init.ItemsInit;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CreativeTabEvents {
    @SubscribeEvent
    public static void addShields(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ItemsInit.WOODEN_SHIELD);
            event.accept(ItemsInit.IRON_SHIELD);
            event.accept(ItemsInit.GOLDEN_SHIELD);
            event.accept(ItemsInit.DIAMOND_SHIELD);
            event.accept(ItemsInit.NETHERITE_SHIELD);
            event.accept(ItemsInit.GRIEFER_SHIELD);
            event.accept(ItemsInit.PARAGON_SHIELD);
        }
    }
}
