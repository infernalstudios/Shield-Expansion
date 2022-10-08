package org.infernalstudios.shieldexp.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.items.NewShieldItem;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ShieldExpansion.MOD_ID);

    public static final RegistryObject<Item> WOODEN_SHIELD = ITEMS.register("wooden_shield", () -> new NewShieldItem(90, 0, new Item.Properties().durability(55)));
    public static final RegistryObject<Item> GOLDEN_SHIELD = ITEMS.register("golden_shield", () -> new NewShieldItem(75, 15, new Item.Properties().durability(77)));
    public static final RegistryObject<Item> IRON_SHIELD = ITEMS.register("iron_shield", () -> new NewShieldItem(60, 30, new Item.Properties().durability(165)));
    public static final RegistryObject<Item> DIAMOND_SHIELD = ITEMS.register("diamond_shield", () -> new NewShieldItem(45, 50, new Item.Properties().durability(363)));
    public static final RegistryObject<Item> NETHERITE_SHIELD = ITEMS.register("netherite_shield", () -> new NewShieldItem(25, 75, new Item.Properties().durability(607)));
}
