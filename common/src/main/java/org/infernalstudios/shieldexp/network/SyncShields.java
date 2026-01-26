package org.infernalstudios.shieldexp.network;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SyncShields implements IPacket {
    public static final CustomPacketPayload.Type<SyncShields> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_shields"));
    private static final int MAX = 32767 * 8;

    public static final StreamCodec<FriendlyByteBuf, SyncShields> STREAM_CODEC = StreamCodec.ofMember(
            SyncShields::encode,
            SyncShields::new
    );

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

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(shields.size());
        shields.forEach((id, data) -> {
            buf.writeResourceLocation(id);
            buf.writeUtf(data.toString(), MAX);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        shields.forEach((id, data) -> ShieldDataLoader.parse(id, data.getAsJsonObject()));
    }
}