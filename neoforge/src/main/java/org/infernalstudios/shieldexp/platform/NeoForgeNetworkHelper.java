package org.infernalstudios.shieldexp.platform;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.infernalstudios.shieldexp.network.IPacket;
import org.infernalstudios.shieldexp.network.SyncBlocking;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.network.SyncShields;
import org.infernalstudios.shieldexp.platform.services.INetworkHelper;

public class NeoForgeNetworkHelper implements INetworkHelper {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1.0");

        registrar.playToClient(
                SyncShields.TYPE,
                SyncShields.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> payload.handle(context.player()))
        );
        registrar.playToServer(
                SyncBlocking.TYPE,
                SyncBlocking.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> payload.handle(context.player()))
        );
        registrar.playBidirectional(
                SyncConfig.TYPE,
                SyncConfig.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> payload.handle(context.player()))
        );
    }

    @Override
    public <MSG extends IPacket> void registerPacket(int id, Class<MSG> clazz) {
    }

    @Override
    public void sendToServer(IPacket message) {
        PacketDistributor.sendToServer(message);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket message) {
        PacketDistributor.sendToPlayer(player, message);
    }
}