package org.infernalstudios.shieldexp.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import org.infernalstudios.shieldexp.compat.BetterCombatAttackListener;
import org.infernalstudios.shieldexp.events.ClientEvents;
import org.infernalstudios.shieldexp.events.TooltipEvents;
import org.infernalstudios.shieldexp.platform.Services;

public class ShieldExpansionModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEvents.initShields();
        ClientEvents.copyOptionalResourcePackIfMissing();

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> TooltipEvents.addTooltip(stack, Minecraft.getInstance().player, lines));

        if (Services.PLATFORM.isModLoaded("bettercombat")) {
            BetterCombatAttackListener.register();
        }
    }
}