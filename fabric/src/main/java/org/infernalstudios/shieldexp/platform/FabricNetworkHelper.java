package org.infernalstudios.shieldexp.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.infernalstudios.shieldexp.network.IPacket;
import org.infernalstudios.shieldexp.platform.services.INetworkHelper;

import java.lang.reflect.Field;

public class FabricNetworkHelper implements INetworkHelper {

    @SuppressWarnings("unchecked")
    @Override
    public <MSG extends IPacket> void registerPacket(int id, Class<MSG> clazz) {
        try {
            Field typeField = clazz.getField("TYPE");
            Field codecField = clazz.getField("STREAM_CODEC");

            CustomPacketPayload.Type<MSG> type = (CustomPacketPayload.Type<MSG>) typeField.get(null);
            StreamCodec<? super FriendlyByteBuf, MSG> codec = (StreamCodec<? super FriendlyByteBuf, MSG>) codecField.get(null);

            PayloadTypeRegistry.playS2C().register(type, codec);
            PayloadTypeRegistry.playC2S().register(type, codec);

            ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> context.server().execute(() -> payload.handle(context.player())));

            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
                ClientPacketRegistrar.register(type);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + clazz.getName(), e);
        }
    }

    @Override
    public void sendToServer(IPacket message) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientSender.send(message);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket message) {
        ServerPlayNetworking.send(player, message);
    }

    private static class ClientPacketRegistrar {
        public static <MSG extends IPacket> void register(CustomPacketPayload.Type<MSG> type) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> context.client().execute(() -> payload.handle(context.player())));
        }
    }

    private static class ClientSender {
        public static void send(IPacket message) {
            ClientPlayNetworking.send(message);
        }
    }
}