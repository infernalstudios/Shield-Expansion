package org.infernalstudios.shieldexp.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShieldDataLoader extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<String, Map<String, Double>> SHIELD_STATS = new ShieldStatsMap();
    public static final List<Map.Entry<ResourceLocation, JsonElement>> toSync = new ArrayList<>();
    public static final String DEFAULT_SHIELD_NAME = Constants.MOD_ID + ":default";
    public static Map<ResourceLocation, JsonElement> FILE_MAP = new HashMap<>();

    public ShieldDataLoader() {
        super(GSON, "shields");
    }

    public static void parse(ResourceLocation name, JsonObject data) {
        String key = name.toString();
        if (BuiltInRegistries.ITEM.containsKey(name) || key.equals(DEFAULT_SHIELD_NAME)) {
            Map<String, Double> stats = new HashMap<>();
            stats.put("cooldownTicks", data.get("cooldownTicks").getAsDouble());
            stats.put("speedFactor", data.get("speedFactor").getAsDouble());
            stats.put("parryDamage", data.get("parryDamage").getAsDouble());
            stats.put("parryTicks", data.get("parryTicks").getAsDouble());
            stats.put("stamina", data.get("stamina").getAsDouble());
            stats.put("blastResistance", data.get("blastResistance").getAsDouble());
            stats.put("flatDamage", data.get("flatDamage").getAsDouble());
            SHIELD_STATS.remove(key);
            SHIELD_STATS.put(key, stats);

            if (!key.equals(DEFAULT_SHIELD_NAME))
                ShieldExpansionConfig.extendList(key);
        }
    }

    @Override
    public void apply(@NotNull Map<ResourceLocation, JsonElement> files, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profilerFiller) {
        FILE_MAP = files;
        toSync.clear();

        for (ResourceLocation name : FILE_MAP.keySet()) {
            if (BuiltInRegistries.ITEM.containsKey(name) || name.toString().equals(DEFAULT_SHIELD_NAME)) {
                JsonElement data = files.get(name);
                parse(name, data.getAsJsonObject());
            }
        }
        toSync.addAll(files.entrySet());
    }

    private static class ShieldStatsMap extends HashMap<String, Map<String, Double>> {
        private static final Map<String, Double> EMERGENCY_DEFAULT = new HashMap<>() {
            {
                this.put("cooldownTicks", 30.0);
                this.put("speedFactor", 0.65);
                this.put("parryDamage", 0.10);
                this.put("parryTicks", 5.0);
                this.put("stamina", 2.0);
                this.put("blastResistance", 0.0);
                this.put("flatDamage", 1.0);
            }
        };

        @Override
        public Map<String, Double> get(Object key) {
            var result = super.get(key);
            if (result != null || !key.equals(DEFAULT_SHIELD_NAME)) return result;
            return EMERGENCY_DEFAULT;
        }
    }
}