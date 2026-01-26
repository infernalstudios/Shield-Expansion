package org.infernalstudios.shieldexp.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.events.ShieldEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class SyncBlocking implements IPacket {
    public static final CustomPacketPayload.Type<SyncBlocking> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_blocking"));

    public static final StreamCodec<ByteBuf, SyncBlocking> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, s -> s.id,
            ByteBufCodecs.BOOL, s -> s.blocking,
            SyncBlocking::new
    );

    UUID id;
    boolean blocking;

    public SyncBlocking(UUID id, boolean blocking) {
        this.id = id;
        this.blocking = blocking;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        Player targetPlayer = player.level().getPlayerByUUID(id);
        if (targetPlayer == null) return;

        Item item = targetPlayer.getOffhandItem().getItem();
        Objects.requireNonNull(targetPlayer.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(ShieldEvents.SPEED_MODIFIER_ID);

        LivingEntityAccess.get(targetPlayer).setBlocking(false);
        LivingEntityAccess.get(targetPlayer).setParryWindow(0);

        if (targetPlayer.isUsingItem()) {
            if (!targetPlayer.getCooldowns().isOnCooldown(item))
                targetPlayer.getCooldowns().addCooldown(item, ShieldEvents.getShieldValue(item, "cooldownTicks").intValue());
            targetPlayer.stopUsingItem();
        }
    }
}