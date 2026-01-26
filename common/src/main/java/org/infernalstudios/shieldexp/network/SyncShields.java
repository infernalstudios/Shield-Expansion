package org.infernalstudios.shieldexp.network;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

import java.util.HashMap;
import java.util.Map;

public class SyncShields implements IPacket {
    private static final int MAX = 32767 * 2;
    private final Map<ResourceLocation, JsonElement> shields;

    public SyncShields(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.shields = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            ResourceLocation id = buf.readResourceLocation();
            JsonElement data = GsonHelper.fromJson(ShieldDataLoader.GSON, buf.readUtf(MAX), JsonElement.class);
            this.shields.put(id, data);
        }
    }

    public SyncShields(Map<ResourceLocation, JsonElement> shields) {
        this.shields = shields;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(shields.size());
        shields.forEach((id, data) -> {
            buf.writeResourceLocation(id);
            buf.writeUtf(data.toString());
        });
    }

    @Override
    public void handle(Player player) {
        shields.forEach((id, data) -> ShieldDataLoader.parse(id, data.getAsJsonObject()));
    }
}