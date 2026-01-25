package org.infernalstudios.shieldexp.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.network.IPacket;
import org.infernalstudios.shieldexp.platform.services.INetworkHelper;

import java.util.HashMap;
import java.util.Map;

public class FabricNetworkHelper implements INetworkHelper {
    private final Map<Class<?>, ResourceLocation> classToId = new HashMap<>();

    @Override
    public <MSG extends IPacket> void registerPacket(int id, Class<MSG> clazz) {
        ResourceLocation loc = new ResourceLocation(Constants.MOD_ID, "packet_" + id);
        classToId.put(clazz, loc);

        ServerPlayNetworking.registerGlobalReceiver(loc, (server, player, handler, buf, responseSender) -> {
            try {
                FriendlyByteBuf copy = PacketByteBufs.copy(buf);
                MSG packet = clazz.getConstructor(FriendlyByteBuf.class).newInstance(copy);
                server.execute(() -> packet.handle(player));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPacketRegistrar.register(loc, clazz);
        }
    }

    private static class ClientPacketRegistrar {
        public static <MSG extends IPacket> void register(ResourceLocation loc, Class<MSG> clazz) {
            ClientPlayNetworking.registerGlobalReceiver(loc, (client, handler, buf, responseSender) -> {
                try {
                    FriendlyByteBuf copy = PacketByteBufs.copy(buf);
                    MSG packet = clazz.getConstructor(FriendlyByteBuf.class).newInstance(copy);
                    client.execute(() -> packet.handle(client.player));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void sendToServer(IPacket message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientSender.send(classToId.get(message.getClass()), buf);
        }
    }

    private static class ClientSender {
        public static void send(ResourceLocation loc, FriendlyByteBuf buf) {
            ClientPlayNetworking.send(loc, buf);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        message.encode(buf);
        ServerPlayNetworking.send(player, classToId.get(message.getClass()), buf);
    }
}