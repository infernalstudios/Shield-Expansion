package org.infernalstudios.shieldexp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.infernalstudios.shieldexp.compat.BetterCombatAttackListener;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.platform.Services;

public class ShieldExpansionModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEvents.initShields();
        ClientEvents.copyOptionalResourcePackIfMissing();

        ClientTickEvents.END_CLIENT_TICK.register(client -> ClientEvents.onClientTick());

        if (Services.PLATFORM.isModLoaded("bettercombat")) {
            BetterCombatAttackListener.register();
        }
    }
}