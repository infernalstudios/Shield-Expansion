package org.infernalstudios.shieldexp.network;

import com.google.gson.JsonElement;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

import java.util.function.Supplier;

public class SyncShields {
    private static final int MAX = 32767 * 2;
    JsonElement data;
    ResourceLocation shield;

    public SyncShields(FriendlyByteBuf buf) {
        this.shield = buf.readResourceLocation();
        this.data = GsonHelper.fromJson(ShieldDataLoader.GSON, buf.readUtf(MAX), JsonElement.class);
    }

    public void encode(FriendlyByteBuf buf){
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
        Player player = Minecraft.getInstance().player;
        if (player != null){
            ShieldDataLoader.parse(shield, data.getAsJsonObject());
        }
    }
}
