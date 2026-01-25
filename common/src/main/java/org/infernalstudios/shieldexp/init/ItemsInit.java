package org.infernalstudios.shieldexp.init;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemsInit {
    public static final List<Supplier<ShieldItem>> SHIELDS = new ArrayList<>();

    public static final Supplier<ShieldItem> WOODEN_SHIELD = registerShield("wooden_shield", 55);
    public static final Supplier<ShieldItem> GOLDEN_SHIELD = registerShield("golden_shield", 32);
    public static final Supplier<ShieldItem> IRON_SHIELD = registerShield("iron_shield", 165);
    public static final Supplier<ShieldItem> DIAMOND_SHIELD = registerShield("diamond_shield", 363);
    public static final Supplier<ShieldItem> NETHERITE_SHIELD = registerShield("netherite_shield", 607);
    public static final Supplier<ShieldItem> PARAGON_SHIELD = registerShield("paragon_shield", 640);
    public static final Supplier<ShieldItem> GRIEFER_SHIELD = registerShield("griefer_shield", 282);

    public static Supplier<ShieldItem> registerShield(String id, int durability) {
        if (id.equals("paragon_shield") && !Services.PLATFORM.isModLoaded("miningmaster"))
            return null;
        if (id.equals("griefer_shield") && !Services.PLATFORM.isModLoaded("savage_and_ravage"))
            return null;

        Item.Properties properties = new Item.Properties().durability(durability);
        if (id.equals("netherite_shield")) properties.fireResistant();

        Supplier<ShieldItem> shield = Suppliers.memoize(() -> new ShieldItem(properties));

        Services.REGISTRY.registerItem(id, (Supplier<Item>) (Supplier<?>) shield);

        SHIELDS.add(shield);
        return shield;
    }

    public static void init() {
    }
}