package org.infernalstudios.shieldexp.events;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.init.Config;

@Mod.EventBusSubscriber(modid = ShieldExpansion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TooltipEvents {
    @SubscribeEvent
    public void onTooltipCreate(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        Player player = event.getEntity();
        if (Config.isShield(item)) {
            event.getToolTip().add(Component.translatable(" "));
            event.getToolTip().add(Component.translatable("shieldexp.tooltip.attribute").withStyle(ChatFormatting.GRAY));
            tooltip(event, item, "cooldownTicks");
            tooltip(event, item, "stamina");
            tooltip(event, item, "speedFactor");
            tooltip(event, item, "parryDamage");
            tooltip(event, item, "parryTicks");
            tooltip(event, item, "blastResistance");
            event.getToolTip().add(Component.translatable(" "));
            if (player != null && Screen.hasShiftDown()) event.getToolTip().add(Component.translatable("shieldexp.tooltip.instructions.parry").withStyle(ChatFormatting.YELLOW));
            else event.getToolTip().add(Component.translatable("shieldexp.tooltip.instructions").withStyle(ChatFormatting.GRAY));
        }
    }

    //adds a line to the passed-through ItemTooltipEvent based on the given item and attribute
    private void tooltip(ItemTooltipEvent event, Item item, String attribute) {
        if (validate(item, attribute)) {
            Double value = ShieldExpansionEvents.getShieldValue(item, attribute);
            String fullKey = "shieldexp.tooltip.attribute." + attribute.replaceAll("(?<!^)([A-Z])", "_$1").toLowerCase().trim();

            if (attribute.equals("speedFactor"))
                event.getToolTip().add(Component.translatable(value < 0.6 ? fullKey + "_slow" : fullKey + "_fast").withStyle(ChatFormatting.DARK_GREEN));
            else {
                if ((attribute.equals("parryTicks") && Config.lenientParryEnabled()) || (attribute.equals("stamina") && Config.lenientStaminaEnabled())) value = value * 2;
                event.getToolTip().add(Component.translatable(fullKey, switch (attribute) {
                    case "cooldownTicks", "parryTicks" -> String.valueOf(value / 20);
                    case "stamina" -> String.valueOf(value.intValue());
                    case "parryDamage", "flatDamage" -> ShieldExpansionEvents.getShieldValue(item, "flatDamage").intValue() + " + " + (value * 100) + "%";
                    case "blastResistance" -> value * 100 + "%";
                    default -> "";
                }).withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }

    //checks whether the values are 0 or currently disabled
    private Boolean validate(Item item, String attribute) {
        if (Config.isShield(item))
            return switch (attribute) {
                    case "cooldownTicks", "stamina" -> !Config.cooldownDisabled() && ShieldExpansionEvents.getShieldValue(item, attribute) != 0;
                    case "blastResistance" -> ShieldExpansionEvents.getShieldValue(item, attribute) != 0;
                    case "parryDamage" -> ShieldExpansionEvents.getShieldValue(item, attribute) != 0 && ShieldExpansionEvents.getShieldValue(item, "flatDamage") != 0;
                    case "speedFactor" -> !Config.speedModifierDisabled();
                    default -> true;};
        else return false;
    }
}
