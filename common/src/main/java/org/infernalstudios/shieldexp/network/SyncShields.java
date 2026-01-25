package org.infernalstudios.shieldexp.network;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

public class SyncShields implements IPacket {
    private static final int MAX = 32767 * 2;
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

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(shield);
        buf.writeUtf(this.data.toString());
    }

    @Override
    public void handle(Player player) {
        ShieldDataLoader.parse(shield, data.getAsJsonObject());
    }
}