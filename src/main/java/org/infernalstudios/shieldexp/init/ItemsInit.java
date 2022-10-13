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
    public static final RegistryObject<NewShieldItem> WOODEN_SHIELD = registerShield("wooden_shield", 55, 20, 0.78F, 0.0F, 5);
    public static final RegistryObject<NewShieldItem> GOLDEN_SHIELD = registerShield("golden_shield", 32, 25, 0.9F, 0.0F, 6);
    public static final RegistryObject<NewShieldItem> IRON_SHIELD = registerShield("iron_shield", 165, 30, 0.64F, 0.20F, 5);
    public static final RegistryObject<NewShieldItem> DIAMOND_SHIELD = registerShield("diamond_shield", 363, 45, 0.55F, 0.35F, 4);
    public static final RegistryObject<NewShieldItem> NETHERITE_SHIELD = registerShield("netherite_shield", 607, 60, 0.38F, 0.50F, 3);

    public static RegistryObject<NewShieldItem> registerShield(String id, int durability, int blockTicks, float speedFactor, double damageBack, int parryTicks) {
        if (id.equals("netherite_shield")) {
            RegistryObject<NewShieldItem> shield = ITEMS.register(id, () -> new NewShieldItem(new Item.Properties().durability(durability).tab(CreativeModeTab.TAB_COMBAT).fireResistant(), blockTicks, speedFactor, damageBack, parryTicks));
            SHIELDS.add(shield);
            return shield;
        } else {
            RegistryObject<NewShieldItem> shield = ITEMS.register(id, () -> new NewShieldItem(new Item.Properties().durability(durability).tab(CreativeModeTab.TAB_COMBAT), blockTicks, speedFactor, damageBack, parryTicks));
            SHIELDS.add(shield);
            return shield;
        }
    }
}
