package org.infernalstudios.shieldexp.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.network.IPacket;
import org.infernalstudios.shieldexp.platform.services.INetworkHelper;

public class ForgeNetworkHelper implements INetworkHelper {
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Constants.MOD_ID, "packets"),
            () -> "1.0", s -> true, s -> true
    );
    private static int ID = 0;

    @Override
    public <MSG extends IPacket> void registerPacket(int id, Class<MSG> clazz) {
        INSTANCE.registerMessage(ID++, clazz, IPacket::encode,
                (buf) -> {
                    try {
                        return clazz.getConstructor(FriendlyByteBuf.class).newInstance(buf);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to decode packet " + clazz.getName(), e);
                    }
                },
                (msg, ctxSupplier) -> {
                    NetworkEvent.Context ctx = ctxSupplier.get();
                    ctx.enqueueWork(() -> {
                        Player sender = ctx.getSender();
                        if (sender != null) {
                            msg.handle(sender);
                        } else {
                            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handle(msg));
                        }
                    });
                    ctx.setPacketHandled(true);
                }
        );
    }

    private static class ClientPacketHandler {
        public static void handle(IPacket msg) {
            msg.handle(Minecraft.getInstance().player);
        }
    }

    @Override
    public void sendToServer(IPacket message) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), message);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, IPacket message) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}