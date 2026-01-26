package org.infernalstudios.shieldexp.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.jetbrains.annotations.NotNull;

public class SyncConfig implements IPacket {
    public static final CustomPacketPayload.Type<SyncConfig> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "sync_config"));

    public static final StreamCodec<FriendlyByteBuf, SyncConfig> STREAM_CODEC = StreamCodec.ofMember(
            SyncConfig::encode,
            SyncConfig::new
    );

    private final boolean stashingCooldown;
    private final boolean generalCooldown;
    private final boolean speedModification;
    private final boolean advancedExplosions;
    private final boolean lenientParry;
    private final boolean lenientStamina;

    public SyncConfig() {
        this.stashingCooldown = ShieldExpansionConfig.STASHING_COOLDOWN;
        this.generalCooldown = ShieldExpansionConfig.GENERAL_COOLDOWN;
        this.speedModification = ShieldExpansionConfig.SPEED_MODIFICATION;
        this.advancedExplosions = ShieldExpansionConfig.ADVANCED_EXPLOSIONS;
        this.lenientParry = ShieldExpansionConfig.LENIENT_PARRY;
        this.lenientStamina = ShieldExpansionConfig.LENIENT_STAMINA;
    }

    public SyncConfig(FriendlyByteBuf buf) {
        this.stashingCooldown = buf.readBoolean();
        this.generalCooldown = buf.readBoolean();
        this.speedModification = buf.readBoolean();
        this.advancedExplosions = buf.readBoolean();
        this.lenientParry = buf.readBoolean();
        this.lenientStamina = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(stashingCooldown);
        buf.writeBoolean(generalCooldown);
        buf.writeBoolean(speedModification);
        buf.writeBoolean(advancedExplosions);
        buf.writeBoolean(lenientParry);
        buf.writeBoolean(lenientStamina);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(Player player) {
        if (player.hasPermissions(2)) {
            ShieldExpansionConfig.STASHING_COOLDOWN = this.stashingCooldown;
            ShieldExpansionConfig.GENERAL_COOLDOWN = this.generalCooldown;
            ShieldExpansionConfig.SPEED_MODIFICATION = this.speedModification;
            ShieldExpansionConfig.ADVANCED_EXPLOSIONS = this.advancedExplosions;
            ShieldExpansionConfig.LENIENT_PARRY = this.lenientParry;
            ShieldExpansionConfig.LENIENT_STAMINA = this.lenientStamina;
        }
    }
}