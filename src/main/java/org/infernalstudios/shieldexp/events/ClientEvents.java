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
package org.infernalstudios.shieldexp.events;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.ExtendedPlayerEntity;
import org.infernalstudios.shieldexp.init.ItemsInit;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    public static void initShields() {
        for (RegistryObject<ShieldItem> shieldItem : ItemsInit.SHIELDS) {
            ItemModelsProperties.register(
                    shieldItem.get(),
                    new ResourceLocation("minecraft:blocking"),
                    (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
            );
        }
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && Minecraft.getInstance().options.keyAttack.isDown() && ExtendedPlayerEntity.get(player).getBlocking()) player.stopUsingItem();
    }
}
