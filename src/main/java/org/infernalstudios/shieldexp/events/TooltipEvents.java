/**
 * Copyright 2022 Infernal Studios
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infernalstudios.shieldexp.events;

import org.infernalstudios.shieldexp.ShieldExpansion;
import org.infernalstudios.shieldexp.init.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ShieldExpansion.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TooltipEvents {
    @SubscribeEvent
    public void onTooltipCreate(ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        PlayerEntity player = event.getPlayer();
        if (player != null && Config.isShield(item)) {
            event.getToolTip().add(ITextComponent.nullToEmpty((" ")));
            event.getToolTip().add(new TranslationTextComponent("shieldexp.tooltip.attribute").withStyle(TextFormatting.GRAY));
            tooltip(event, item, "cooldownTicks");
            tooltip(event, item, "stamina");
            tooltip(event, item, "speedFactor");
            tooltip(event, item, "parryDamage");
            tooltip(event, item, "parryTicks");
            tooltip(event, item, "blastResistance");
            event.getToolTip().add(ITextComponent.nullToEmpty(" "));
            if (Screen.hasShiftDown()) event.getToolTip().add(new TranslationTextComponent("shieldexp.tooltip.instructions.parry").withStyle(TextFormatting.YELLOW));
            else event.getToolTip().add(new TranslationTextComponent("shieldexp.tooltip.instructions").withStyle(TextFormatting.GRAY));
        }
    }

    //adds a line to the passed-through ItemTooltipEvent based on the given item and attribute
    private void tooltip(ItemTooltipEvent event, Item item, String attribute) {
        if (validate(item, attribute)) {
            Double value = ShieldExpansionEvents.getShieldValue(item, attribute);
            String fullKey = "shieldexp.tooltip.attribute." + attribute.replaceAll("(?<!^)([A-Z])", "_$1").toLowerCase().trim();

            if (attribute.equals("speedFactor"))
                event.getToolTip().add(new TranslationTextComponent(value < 0.6 ? fullKey + "_slow" : fullKey + "_fast").withStyle(TextFormatting.DARK_GREEN));
            else {
                if ((attribute.equals("parryTicks") && Config.lenientParryEnabled()) || (attribute.equals("stamina") && Config.lenientStaminaEnabled())) value = value * 2;
                event.getToolTip().add(new TranslationTextComponent(fullKey, switch (attribute) {
                    case "cooldownTicks", "parryTicks" -> String.valueOf(value / 20);
                    case "stamina" -> String.valueOf(value.intValue());
                    case "parryDamage", "flatDamage" -> ShieldExpansionEvents.getShieldValue(item, "flatDamage").intValue() + " + " + (value * 100) + "%";
                    case "blastResistance" -> value * 100 + "%";
                    default -> "";
                }).withStyle(TextFormatting.DARK_GREEN));
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

