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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import org.infernalstudios.shieldexp.access.LivingEntityAccess;
import org.infernalstudios.shieldexp.init.Config;
import org.infernalstudios.shieldexp.init.ItemsInit;

public class ClientEvents {

    public static void initShields() {
        IItemPropertyGetter blockFn = (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        for (RegistryObject<ShieldItem> shieldItem : ItemsInit.SHIELDS) ItemModelsProperties.register(shieldItem.get(), new ResourceLocation("minecraft:blocking"), blockFn);
    }


    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && Minecraft.getInstance().options.keyAttack.isDown() && LivingEntityAccess.get(player).getBlocking()) player.stopUsingItem();
    }

    @SubscribeEvent
    public void onFovModify(FOVUpdateEvent event) {
        if (LivingEntityAccess.get(event.getEntity()).getBlocking()) event.setNewfov(1.0F);
    }
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

            if (attribute.equals("speedFactor")) {
                String key = value < 0.6 ? fullKey + "_slow" : fullKey + "_fast";
                TranslationTextComponent component = (TranslationTextComponent) new TranslationTextComponent(key).withStyle(TextFormatting.DARK_GREEN);
                event.getToolTip().add(component);
            } else {
                if ((attribute.equals("parryTicks") && Config.lenientParryEnabled()) || (attribute.equals("stamina") && Config.lenientStaminaEnabled())) {
                    value = value * 2;
                }

                String text;
                switch (attribute) {
                    case "cooldownTicks":
                    case "parryTicks":
                        text = String.valueOf(value / 20);
                        break;
                    case "stamina":
                        text = String.valueOf(value.intValue());
                        break;
                    case "parryDamage":
                    case "flatDamage":
                        text = ShieldExpansionEvents.getShieldValue(item, "flatDamage").intValue() + " + " + (value * 100) + "%";
                        break;
                    case "blastResistance":
                        text = value * 100 + "%";
                        break;
                    default:
                        text = "";
                }

                TranslationTextComponent component = (TranslationTextComponent) new TranslationTextComponent(fullKey, text).withStyle(TextFormatting.DARK_GREEN);
                event.getToolTip().add(component);
            }
        }
    }

    //checks whether the values are 0 or currently disabled
    private boolean validate(Item item, String attribute) {
        if (Config.isShield(item)) {
            switch (attribute) {
                case "cooldownTicks":
                case "stamina":
                    return !Config.cooldownDisabled() && ShieldExpansionEvents.getShieldValue(item, attribute) != 0;
                case "blastResistance":
                    return ShieldExpansionEvents.getShieldValue(item, attribute) != 0;
                case "parryDamage":
                    return ShieldExpansionEvents.getShieldValue(item, attribute) != 0 && ShieldExpansionEvents.getShieldValue(item, "flatDamage") != 0;
                case "speedFactor":
                    return !Config.speedModifierDisabled();
                default:
                    return true;
            }
        } else {
            return false;
        }
    }
}
