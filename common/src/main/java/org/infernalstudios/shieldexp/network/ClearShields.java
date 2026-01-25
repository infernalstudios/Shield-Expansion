package org.infernalstudios.shieldexp.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class ClearShields implements IPacket {
    public ClearShields(FriendlyByteBuf buf) {
    }

    public ClearShields() {
    }

    @Override
    public void encode(FriendlyByteBuf buf){
    }

    @Override
    public void handle(Player player) {
    }
}