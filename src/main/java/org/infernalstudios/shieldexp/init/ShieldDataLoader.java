package org.infernalstudios.shieldexp.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.network.ClearShields;
import org.infernalstudios.shieldexp.network.SyncShields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShieldDataLoader extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static Map<ResourceLocation, JsonElement> FILE_MAP = new HashMap<>();
    public static final Map<String, Map<String, Double>> SHIELD_STATS = new ShieldStatsMap();
    public static final List<Map.Entry<ResourceLocation, JsonElement>> toSync = new ArrayList<>();

    public static final String DEFAULT_SHIELD_NAME = ShieldExpansion.MOD_ID + ":default";

    public ShieldDataLoader() {
        super(GSON, "shields");
    }

    @SubscribeEvent
    void register(AddReloadListenerEvent event) {
        event.addListener(new ShieldDataLoader());
    }

    //on datapack load, reads all JSON files in the shields data folder of every namespace and adds the stats to a map
    @Override
    public void apply(Map<ResourceLocation, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        FILE_MAP = files;

        for (ResourceLocation name : FILE_MAP.keySet()) {
            if (ForgeRegistries.ITEMS.containsKey(name) || name.toString().equals(DEFAULT_SHIELD_NAME)) {
                JsonElement data = files.get(name);

                parse(name, data.getAsJsonObject());
            }
        }
        toSync.addAll(files.entrySet());
    }

    @SubscribeEvent
    public static void syncShieldsOnJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        if (!player.level().isClientSide()){
            NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClearShields());

            for (Map.Entry<ResourceLocation, JsonElement> file : toSync){
                NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncShields(file.getKey(), file.getValue()));
            }
        }
    }

    public static void parse(ResourceLocation name, JsonObject data) {
        String key = name.toString();
        if (ForgeRegistries.ITEMS.containsKey(name) || key.equals(DEFAULT_SHIELD_NAME)) {
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

            if (!key.equals(DEFAULT_SHIELD_NAME))
                Config.extendList(key);
        }
    }

    public static void clearAll() {
        SHIELD_STATS.clear();
    }

    private static class ShieldStatsMap extends HashMap<String, Map<String, Double>> {
        // This should NEVER need to be used, but just in case the data is missing, this is the hard-coded default data
        private static final Map<String, Double> EMERGENCY_DEFAULT = new HashMap<>() {
            {
                // these should be the same as the ones in src/resources/data/shieldexp/shields/default.json
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

            // if we have a result OR we are not trying to get the default, return what we have
            if (result != null || !key.equals(DEFAULT_SHIELD_NAME)) return result;

            // if we don't have the default, return the emergency default
            return EMERGENCY_DEFAULT;
        }
    }
}