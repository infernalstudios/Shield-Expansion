package org.infernalstudios.shieldexp.platform.services;

import net.minecraft.server.level.ServerPlayer;
import org.infernalstudios.shieldexp.network.IPacket;

public interface INetworkHelper {
    /**
     * Registers a packet for network communication.
     */
    <MSG extends IPacket> void registerPacket(int id, Class<MSG> clazz);

    /**
     * Sends a packet to the server (Client -> Server).
     */
    void sendToServer(IPacket message);

    /**
     * Sends a packet to a specific player (Server -> Client).
     */
    void sendToPlayer(ServerPlayer player, IPacket message);
}