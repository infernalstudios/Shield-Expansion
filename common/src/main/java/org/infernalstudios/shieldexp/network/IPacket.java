package org.infernalstudios.shieldexp.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public interface IPacket {
    void encode(FriendlyByteBuf buf);

    void handle(Player player);
}