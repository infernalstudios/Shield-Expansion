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
package com.teampotato.shieldexpansion;

import com.teampotato.shieldexpansion.events.ClientEvent;
import com.teampotato.shieldexpansion.events.ShieldExpansionEvents;
import com.teampotato.shieldexpansion.init.Config;
import com.teampotato.shieldexpansion.init.ItemsInit;
import com.teampotato.shieldexpansion.init.NetworkInit;
import com.teampotato.shieldexpansion.init.ShieldDataLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ShieldExpansion.ID)
public class ShieldExpansion {
    public static final String ID = "shieldexpansion";

    public ShieldExpansion() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemsInit.ITEMS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG, "ShieldExpansion-common.toml");

        modBus.addListener(this::clientSetup);
        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new ShieldExpansionEvents());
        MinecraftForge.EVENT_BUS.register(new ShieldDataLoader());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvent::setup);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event){
        NetworkInit.registerPackets();
    }
}
