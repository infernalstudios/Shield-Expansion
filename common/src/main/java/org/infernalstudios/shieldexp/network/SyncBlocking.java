package org.infernalstudios.shieldexp.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.events.ShieldEvents;

import java.util.Objects;
import java.util.UUID;

public class SyncBlocking implements IPacket {
    UUID id;
    boolean blocking;

    public SyncBlocking(FriendlyByteBuf buf) {
        this.id = buf.readUUID();
        this.blocking = buf.readBoolean();
    }

    public SyncBlocking(UUID id, boolean blocking) {
        this.id = id;
        this.blocking = blocking;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeBoolean(blocking);
    }

    @Override
    public void handle(Player player) {
        Player targetPlayer = player.level().getPlayerByUUID(id);
        if (targetPlayer == null) return;

        Item item = targetPlayer.getOffhandItem().getItem();
        Objects.requireNonNull(targetPlayer.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(targetPlayer.getUUID());
        LivingEntityAccess.get(targetPlayer).setBlocking(false);
        LivingEntityAccess.get(targetPlayer).setParryWindow(0);

        if (targetPlayer.isUsingItem()) {
            if (!targetPlayer.getCooldowns().isOnCooldown(item))
                targetPlayer.getCooldowns().addCooldown(item, ShieldEvents.getShieldValue(item, "cooldownTicks").intValue());
            targetPlayer.stopUsingItem();
        }
    }
}