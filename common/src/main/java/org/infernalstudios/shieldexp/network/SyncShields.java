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

public class SyncShields implements IPacket {
    public static final CustomPacketPayload.Type<SyncShields> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_shields"));
    private static final int MAX = 32767 * 2;
    public static final StreamCodec<FriendlyByteBuf, SyncShields> STREAM_CODEC = StreamCodec.ofMember(
            SyncShields::encode,
            SyncShields::new
    );
    JsonElement data;
    ResourceLocation shield;

    public SyncShields(FriendlyByteBuf buf) {
        this.shield = buf.readResourceLocation();
        this.data = GsonHelper.fromJson(ShieldDataLoader.GSON, buf.readUtf(MAX), JsonElement.class);
    }

    public SyncShields(ResourceLocation shield, JsonElement data) {
        this.shield = shield;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(shield);
        buf.writeUtf(this.data.toString());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        ShieldDataLoader.parse(shield, data.getAsJsonObject());
    }
}