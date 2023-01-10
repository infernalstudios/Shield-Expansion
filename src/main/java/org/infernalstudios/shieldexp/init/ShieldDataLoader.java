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
import org.infernalstudios.shieldexp.network.SyncShields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ShieldDataLoader extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static Map<ResourceLocation, JsonElement> FILE_MAP = new HashMap<>();
    public static Map<String, Map<String, Double>> SHIELD_STATS = new HashMap<>();
    public static List<Map.Entry<ResourceLocation, JsonElement>> toSync = new ArrayList<>();

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
            if (ForgeRegistries.ITEMS.containsKey(name) || name.toString().equals(ShieldExpansion.MOD_ID + ":default")) {
                JsonElement data = files.get(name);

                parse(name, data.getAsJsonObject());

                if (!name.toString().equals(ShieldExpansion.MOD_ID + ":default"))
                    Config.extendList(name.toString());
            }
        }
        toSync.addAll(files.entrySet());
    }

    @SubscribeEvent
    public static void syncProfessionsOnJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();

        if (!player.level.isClientSide()){
//            NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClearProfessionsPacket());

            for (Map.Entry<ResourceLocation, JsonElement> file : toSync){
                NetworkInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SyncShields(file.getKey(), file.getValue()));
            }
        }
    }

    public static void parse(ResourceLocation name, JsonObject data) {
        if (ForgeRegistries.ITEMS.containsKey(name)) {
            Map<String, Double> stats = new HashMap<>();
            stats.put("cooldownTicks", data.getAsJsonObject().get("cooldownTicks").getAsDouble());
            stats.put("speedFactor", data.getAsJsonObject().get("speedFactor").getAsDouble());
            stats.put("parryDamage", data.getAsJsonObject().get("parryDamage").getAsDouble());
            stats.put("parryTicks", data.getAsJsonObject().get("parryTicks").getAsDouble());
            stats.put("stamina", data.getAsJsonObject().get("stamina").getAsDouble());
            stats.put("blastResistance", data.getAsJsonObject().get("blastResistance").getAsDouble());
            stats.put("flatDamage", data.getAsJsonObject().get("flatDamage").getAsDouble());
            SHIELD_STATS.put(name.toString(), stats);

            if (!name.toString().equals(ShieldExpansion.MOD_ID + ":default"))
                Config.extendList(name.toString());
        }
    }

    public static void clearAll() {
        SHIELD_STATS.clear();
        toSync.clear();
    }
}
