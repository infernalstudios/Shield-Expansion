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
package com.teampotato.shieldexpansion.init;

import com.teampotato.shieldexpansion.ShieldExpansion;
import com.teampotato.shieldexpansion.network.ClearShields;
import com.teampotato.shieldexpansion.network.SyncShields;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkInit {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID ++;
    }

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ShieldExpansion.ID, "packets"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(), SyncShields.class, SyncShields::encode, SyncShields::new, SyncShields::handle);
        INSTANCE.registerMessage(nextID(), ClearShields.class, ClearShields::encode, ClearShields::new, ClearShields::handle);
    }
}
