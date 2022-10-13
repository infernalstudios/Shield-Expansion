package org.infernalstudios.shieldexp.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.items.NewShieldItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ShieldExpansion.MOD_ID);
    public static final List<RegistryObject<NewShieldItem>> SHIELDS = new ArrayList<>();

    //movement speed factors are modified to align with the vanilla shield modifier, actual results are 75%, 90%, 60%, 45%, and 25% as intended
    public static final RegistryObject<NewShieldItem> WOODEN_SHIELD = registerShield("wooden_shield", 55, 0.78F, 0.0F);
    public static final RegistryObject<NewShieldItem> GOLDEN_SHIELD = registerShield("golden_shield", 77, 0.9F, 0.00F);
    public static final RegistryObject<NewShieldItem> IRON_SHIELD = registerShield("iron_shield", 165, 0.64F, 0.20F);
    public static final RegistryObject<NewShieldItem> DIAMOND_SHIELD = registerShield("diamond_shield", 363, 0.55F, 0.35F);
    public static final RegistryObject<NewShieldItem> NETHERITE_SHIELD = registerShield("netherite_shield", 607, 0.38F, 0.50F);

    public static RegistryObject<NewShieldItem> registerShield(String id, int durability, float speedFactor, float damageBack) {
        RegistryObject<NewShieldItem> shield = ITEMS.register(id, () -> new NewShieldItem(new Item.Properties().durability(durability).tab(CreativeModeTab.TAB_COMBAT), speedFactor, damageBack));
        SHIELDS.add(shield);
        return shield;
    }
}
