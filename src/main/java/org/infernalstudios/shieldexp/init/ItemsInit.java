package org.infernalstudios.shieldexp.init;

import net.minecraft.world.item.CreativeModeTab;
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

    //movement speed factors are modified to align with the vanilla shield modifier, actual results are 90%, 75%, 60%, 45%, and 25% as intended
    public static final RegistryObject<Item> WOODEN_SHIELD = ITEMS.register("wooden_shield", () -> new NewShieldItem(new Item.Properties().durability(55).tab(CreativeModeTab.TAB_COMBAT), 0.9F, 0.0F));
    public static final RegistryObject<Item> GOLDEN_SHIELD = ITEMS.register("golden_shield", () -> new NewShieldItem(new Item.Properties().durability(77).tab(CreativeModeTab.TAB_COMBAT), 0.78F, 0.15F));
    public static final RegistryObject<Item> IRON_SHIELD = ITEMS.register("iron_shield", () -> new NewShieldItem(new Item.Properties().durability(165).tab(CreativeModeTab.TAB_COMBAT), 0.64F, 0.30F));
    public static final RegistryObject<Item> DIAMOND_SHIELD = ITEMS.register("diamond_shield", () -> new NewShieldItem(new Item.Properties().durability(363).tab(CreativeModeTab.TAB_COMBAT), 0.55F, 0.50F));
    public static final RegistryObject<Item> NETHERITE_SHIELD = ITEMS.register("netherite_shield", () -> new NewShieldItem(new Item.Properties().durability(607).tab(CreativeModeTab.TAB_COMBAT), 0.38F, 0.75F));
}
