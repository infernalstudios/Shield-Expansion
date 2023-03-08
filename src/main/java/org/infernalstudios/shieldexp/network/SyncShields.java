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
package org.infernalstudios.shieldexp.network;

import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

import java.util.function.Supplier;

public class SyncShields {
    private static final int MAX = 32767 * 2;
    final JsonElement data;
    final ResourceLocation shield;

    public SyncShields(PacketBuffer buf) {
        this.shield = buf.readResourceLocation();
        this.data = JSONUtils.fromJson(ShieldDataLoader.GSON, buf.readUtf(MAX), JsonElement.class);
    }

    public void encode(PacketBuffer buf){
        buf.writeResourceLocation(shield);
        buf.writeUtf(this.data.toString());
    }

    public SyncShields(ResourceLocation shield, JsonElement data){
        this.shield = shield;
        this.data = data;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(this::handle);
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null){
            ShieldDataLoader.parse(shield, data.getAsJsonObject());
        }
    }
}