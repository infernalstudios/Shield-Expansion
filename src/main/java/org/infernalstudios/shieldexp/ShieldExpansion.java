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
package org.infernalstudios.shieldexp;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.events.ShieldExpansionEvents;
import org.infernalstudios.shieldexp.init.Config;
import org.infernalstudios.shieldexp.init.ItemsInit;
import org.infernalstudios.shieldexp.init.NetworkInit;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

import static org.infernalstudios.shieldexp.events.ClientEvents.initShields;

@Mod(ShieldExpansion.MOD_ID)
public class ShieldExpansion {
    public static final String MOD_ID = "shieldexp";

    public ShieldExpansion() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemsInit.ITEMS.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG, "ShieldExpansion-common.toml");

        modBus.addListener(this::clientSetup);
        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new ShieldExpansionEvents());
        MinecraftForge.EVENT_BUS.register(new ShieldDataLoader());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        initShields();
    }

    public void commonSetup(final FMLCommonSetupEvent event){
        NetworkInit.registerPackets();
    }
}
