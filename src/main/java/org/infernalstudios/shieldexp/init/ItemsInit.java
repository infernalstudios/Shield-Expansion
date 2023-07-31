package org.infernalstudios.shieldexp.init;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.infernalstudios.shieldexp.ShieldExpansion;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ShieldExpansion.MOD_ID);
    public static final List<RegistryObject<ShieldItem>> SHIELDS = new ArrayList<>();

    public static final RegistryObject<ShieldItem> WOODEN_SHIELD = registerShield("wooden_shield", 55);
    public static final RegistryObject<ShieldItem> GOLDEN_SHIELD = registerShield("golden_shield", 32);
    public static final RegistryObject<ShieldItem> IRON_SHIELD = registerShield("iron_shield", 165);
    public static final RegistryObject<ShieldItem> DIAMOND_SHIELD = registerShield("diamond_shield", 363);
    public static final RegistryObject<ShieldItem> NETHERITE_SHIELD = registerShield("netherite_shield", 607);
    public static final RegistryObject<ShieldItem> PARAGON_SHIELD = registerShield("paragon_shield", 640);
    public static final RegistryObject<ShieldItem> GRIEFER_SHIELD = registerShield("griefer_shield", 282);

    public static RegistryObject<ShieldItem> registerShield(String id, int durability) {
        Item.Properties properties = new Item.Properties().durability(durability);
        if (id.equals("netherite_shield")) properties.fireResistant();
        RegistryObject<ShieldItem> shield = ITEMS.register(id, () -> new ShieldItem(properties));

        SHIELDS.add(shield);
        return shield;
    }
}