package org.infernalstudios.shieldexp.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;

import java.util.function.Supplier;

public class ClearShields {
    public ClearShields(FriendlyByteBuf buf) {
    }

    public void encode(FriendlyByteBuf buf){
    }

    public ClearShields() {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(this::handle);
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handle() {
        Player player = Minecraft.getInstance().player;
        if (player != null){
            ShieldDataLoader.clearAll();
        }
    }
}
