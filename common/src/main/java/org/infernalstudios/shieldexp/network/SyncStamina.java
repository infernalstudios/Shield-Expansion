package org.infernalstudios.shieldexp.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.jetbrains.annotations.NotNull;

public class SyncStamina implements IPacket {
    public static final CustomPacketPayload.Type<SyncStamina> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_stamina"));

    public static final StreamCodec<ByteBuf, SyncStamina> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, s -> s.stamina,
            SyncStamina::new
    );

    private final int stamina;

    public SyncStamina(int stamina) {
        this.stamina = stamina;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        LivingEntityAccess.get(player).shieldexp$setUsedStamina(this.stamina);
    }
}
