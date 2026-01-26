package org.infernalstudios.shieldexp.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public interface IPacket extends CustomPacketPayload {
    void handle(Player player);
}