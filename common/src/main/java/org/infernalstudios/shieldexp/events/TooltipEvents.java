package org.infernalstudios.shieldexp.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.shieldexp.config.ShieldExpansionConfig;

import java.util.List;

public class TooltipEvents {
    public static void addTooltip(ItemStack stack, Player player, List<Component> tooltip) {
        Item item = stack.getItem();
        if (player != null && ShieldExpansionConfig.isShield(item) && ShieldExpansionConfig.TOOLTIPS) {
            tooltip.add(Component.literal(" "));
            tooltip.add(Component.translatable("shieldexp.tooltip.attribute").withStyle(ChatFormatting.GRAY));

            if (ShieldExpansionConfig.TOOLTIP_COOLDOWN)
                addTooltipLine(tooltip, item, "cooldownTicks");

            if (ShieldExpansionConfig.TOOLTIP_STAMINA)
                addTooltipLine(tooltip, item, "stamina");

            if (ShieldExpansionConfig.TOOLTIP_SPEED)
                addTooltipLine(tooltip, item, "speedFactor");

            if (ShieldExpansionConfig.TOOLTIP_PARRY_DAMAGE)
                addTooltipLine(tooltip, item, "parryDamage");

            if (ShieldExpansionConfig.TOOLTIP_PARRY_WINDOW)
                addTooltipLine(tooltip, item, "parryTicks");

            if (ShieldExpansionConfig.TOOLTIP_BLAST_RESISTANCE)
                addTooltipLine(tooltip, item, "blastResistance");
        }
    }

    private static void addTooltipLine(List<Component> tooltip, Item item, String attribute) {
        if (validate(item, attribute)) {
            Double value = ShieldEvents.getShieldValue(item, attribute);
            String fullKey = "shieldexp.tooltip.attribute." + attribute.replaceAll("(?<!^)([A-Z])", "_$1").toLowerCase().trim();

            if (attribute.equals("speedFactor"))
                tooltip.add(Component.translatable(value < 0.6 ? fullKey + "_slow" : fullKey + "_fast").withStyle(ChatFormatting.DARK_GREEN));
            else {
                if ((attribute.equals("parryTicks") && ShieldExpansionConfig.lenientParryEnabled()) || (attribute.equals("stamina") && ShieldExpansionConfig.lenientStaminaEnabled()))
                    value = value * 2;

                String valueStr = switch (attribute) {
                    case "cooldownTicks", "parryTicks" -> String.valueOf(value / 20);
                    case "stamina" -> String.valueOf(value.intValue());
                    case "parryDamage", "flatDamage" ->
                            ShieldEvents.getShieldValue(item, "flatDamage").intValue() + " + " + (value * 100) + "%";
                    case "blastResistance" -> value * 100 + "%";
                    default -> "";
                };

                tooltip.add(Component.translatable(fullKey, valueStr).withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }

    private static Boolean validate(Item item, String attribute) {
        if (ShieldExpansionConfig.isShield(item))
            return switch (attribute) {
                case "cooldownTicks", "stamina" ->
                        ShieldExpansionConfig.cooldownEnabled() && ShieldEvents.getShieldValue(item, attribute) != 0;
                case "blastResistance" -> ShieldEvents.getShieldValue(item, attribute) != 0;
                case "parryDamage" ->
                        ShieldEvents.getShieldValue(item, attribute) != 0 && ShieldEvents.getShieldValue(item, "flatDamage") != 0;
                case "speedFactor" -> ShieldExpansionConfig.speedModifierEnabled();
                default -> true;
            };
        else return false;
    }
}