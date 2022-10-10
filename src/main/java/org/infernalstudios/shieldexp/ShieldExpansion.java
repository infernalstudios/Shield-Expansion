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

import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.events.FovEvents;
import org.infernalstudios.shieldexp.events.ShieldExpansionEvents;
import org.infernalstudios.shieldexp.init.ItemsInit;

@Mod("shieldexp")
public class ShieldExpansion {
    public static final String NAME = "Shield Expansion";
    public static final String MOD_ID = "shieldexp";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public ShieldExpansion() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemsInit.ITEMS.register(modBus);

        MinecraftForge.EVENT_BUS.register(new ShieldExpansionEvents());
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
        MinecraftForge.EVENT_BUS.register(new FovEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

}