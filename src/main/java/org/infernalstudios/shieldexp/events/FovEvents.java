package org.infernalstudios.shieldexp.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FovEvents {
    //cancels the FOV speed modifier if it is caused by blocking
    @SubscribeEvent
    public void onFovModify(ComputeFovModifierEvent event) {
        if (LivingEntityAccess.get(event.getPlayer()).getBlocking()) event.setNewFovModifier(1.0F);
    }
}
