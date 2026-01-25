package org.infernalstudios.shieldexp.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.infernalstudios.shieldexp.network.SyncConfig;
import org.infernalstudios.shieldexp.platform.Services;

import java.util.List;

public class ClothConfigIntegration {

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.shieldexp.title"));

        builder.setSavingRunnable(() -> {
            ShieldExpansionConfig.save();

            if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null) {
                Services.NETWORK.sendToServer(new SyncConfig());
            }
        });

        ConfigCategory shieldList = builder.getOrCreateCategory(Component.translatable("config.shieldexp.category.shield_list"));
        ConfigCategory modifiers = builder.getOrCreateCategory(Component.translatable("config.shieldexp.category.modifiers"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        shieldList.addEntry(entryBuilder.startStrList(Component.translatable("config.shieldexp.option.shield_list"), ShieldExpansionConfig.SHIELD_LIST)
                .setDefaultValue(List.of())
                .setTooltip(Component.translatable("config.shieldexp.tooltip.shield_list"))
                .setSaveConsumer(list -> ShieldExpansionConfig.SHIELD_LIST = list)
                .build());

        shieldList.addEntry(entryBuilder.startStrList(Component.translatable("config.shieldexp.option.shield_blacklist"), ShieldExpansionConfig.SHIELD_BLACKLIST)
                .setDefaultValue(List.of())
                .setTooltip(Component.translatable("config.shieldexp.tooltip.shield_blacklist"))
                .setSaveConsumer(list -> ShieldExpansionConfig.SHIELD_BLACKLIST = list)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.lowering_cooldown"), ShieldExpansionConfig.STASHING_COOLDOWN)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.lowering_cooldown"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.STASHING_COOLDOWN = newValue)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.cooldown"), ShieldExpansionConfig.GENERAL_COOLDOWN)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.cooldown"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.GENERAL_COOLDOWN = newValue)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.speed_modifier"), ShieldExpansionConfig.SPEED_MODIFICATION)
                .setDefaultValue(true)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.speed_modifier"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.SPEED_MODIFICATION = newValue)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.harder_explosions"), ShieldExpansionConfig.ADVANCED_EXPLOSIONS)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.harder_explosions"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.ADVANCED_EXPLOSIONS = newValue)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.lenient_parry_mode"), ShieldExpansionConfig.LENIENT_PARRY)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.lenient_parry_mode"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.LENIENT_PARRY = newValue)
                .build());

        modifiers.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.shieldexp.option.lenient_stamina_mode"), ShieldExpansionConfig.LENIENT_STAMINA)
                .setDefaultValue(false)
                .setTooltip(Component.translatable("config.shieldexp.tooltip.lenient_stamina_mode"))
                .setSaveConsumer(newValue -> ShieldExpansionConfig.LENIENT_STAMINA = newValue)
                .build());

        return builder.build();
    }
}