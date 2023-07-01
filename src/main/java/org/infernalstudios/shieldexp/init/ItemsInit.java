/**
 * Copyright 2022 Infernal Studios
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infernalstudios.shieldexp.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.ShieldExpansion;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
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
        Item.Properties properties = new Item.Properties().durability(durability).tab(ItemGroup.TAB_COMBAT);
        if (id.equals("netherite_shield")) properties.fireResistant();
        RegistryObject<ShieldItem> shield = ITEMS.register(id, () -> new ShieldItem(properties));

        SHIELDS.add(shield);
        return shield;
    }
}