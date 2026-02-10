package org.infernalstudios.shieldexp.compat;

import net.bettercombat.api.client.BetterCombatClientEvents;
import net.minecraft.world.item.Item;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;
import org.infernalstudios.shieldexp.network.SyncBlocking;
import org.infernalstudios.shieldexp.platform.Services;

public class BetterCombatAttackListener {

    public static void register() {
        BetterCombatClientEvents.ATTACK_START.register((player, hand) -> {
            if (ShieldExpansionConfig.ITEM_ONLY_MODE) return;

            Item item = player.getOffhandItem().getItem();
            if (ShieldExpansionConfig.isShield(item)) {
                Services.NETWORK.sendToServer(new SyncBlocking(player.getUUID(), false));
            }
        });
    }
}