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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.network.ClearShields;
import org.infernalstudios.shieldexp.network.SyncShields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShieldDataLoader extends JsonReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static Map<ResourceLocation, JsonElement> FILE_MAP = new HashMap<>();
    public static final Map<String, Map<String, Double>> SHIELD_STATS = new HashMap<>();
    public static final List<Map.Entry<ResourceLocation, JsonElement>> toSync = new ArrayList<>();

    public ShieldDataLoader() {
        super(GSON, "shields");
    }

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(new ShieldDataLoader());
    }

    @Override
    public void apply(Map<ResourceLocation, JsonElement> files, IResourceManager resourceManager, IProfiler profiler) {
        FILE_MAP = files;

        for (ResourceLocation name : FILE_MAP.keySet()) {
            if (ForgeRegistries.ITEMS.containsKey(name) || name.toString().equals(ShieldExpansion.MOD_ID + ":default")) {
                JsonElement data = files.get(name);

                parse(name, data.getAsJsonObject());
            }
        }
        toSync.addAll(files.entrySet());
    }

    @SubscribeEvent
    public static void syncShieldsOnJoin(PlayerEvent.PlayerLoggedInEvent event){
        PlayerEntity player = event.getPlayer();

        if (!player.level.isClientSide()){
            NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new ClearShields());
            for (Map.Entry<ResourceLocation, JsonElement> file : toSync){
                NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SyncShields(file.getKey(), file.getValue()));
            }
        }
    }

    public static void parse(ResourceLocation name, JsonObject data) {
        String key = name.toString();
        if (ForgeRegistries.ITEMS.containsKey(name) || key.equals(ShieldExpansion.MOD_ID + ":default")) {
            Map<String, Double> stats = new HashMap<>();
            stats.put("cooldownTicks", data.getAsJsonObject().get("cooldownTicks").getAsDouble());
            stats.put("speedFactor", data.getAsJsonObject().get("speedFactor").getAsDouble());
            stats.put("parryDamage", data.getAsJsonObject().get("parryDamage").getAsDouble());
            stats.put("parryTicks", data.getAsJsonObject().get("parryTicks").getAsDouble());
            stats.put("stamina", data.getAsJsonObject().get("stamina").getAsDouble());
            stats.put("blastResistance", data.getAsJsonObject().get("blastResistance").getAsDouble());
            stats.put("flatDamage", data.getAsJsonObject().get("flatDamage").getAsDouble());
            SHIELD_STATS.remove(key);
            SHIELD_STATS.put(key, stats);

            if (!key.equals(ShieldExpansion.MOD_ID + ":default")) Config.extendList(key);
        }
    }

    public static void clearAll() {
        SHIELD_STATS.clear();
    }
}
