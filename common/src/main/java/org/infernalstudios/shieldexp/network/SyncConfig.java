package org.infernalstudios.shieldexp.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;

public class SyncConfig implements IPacket {
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

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(stashingCooldown);
        buf.writeBoolean(generalCooldown);
        buf.writeBoolean(speedModification);
        buf.writeBoolean(advancedExplosions);
        buf.writeBoolean(lenientParry);
        buf.writeBoolean(lenientStamina);
    }

    @Override
    public void handle(Player player) {
        if (player.level().isClientSide || player.hasPermissions(2)) {
            ShieldExpansionConfig.STASHING_COOLDOWN = this.stashingCooldown;
            ShieldExpansionConfig.GENERAL_COOLDOWN = this.generalCooldown;
            ShieldExpansionConfig.SPEED_MODIFICATION = this.speedModification;
            ShieldExpansionConfig.ADVANCED_EXPLOSIONS = this.advancedExplosions;
            ShieldExpansionConfig.LENIENT_PARRY = this.lenientParry;
            ShieldExpansionConfig.LENIENT_STAMINA = this.lenientStamina;
        }
    }
}