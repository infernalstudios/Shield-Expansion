package org.infernalstudios.shieldexp.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class Config  {
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SHIELD_LIST;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SHIELD_BLACKLIST;

    public static ForgeConfigSpec.BooleanValue STASHING_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue GENERAL_COOLDOWN;
    public static ForgeConfigSpec.BooleanValue SPEED_MODIFICATION;
    public static ForgeConfigSpec.BooleanValue ADVANCED_EXPLOSIONS;
    public static ForgeConfigSpec.BooleanValue LENIENT_PARRY;
    public static ForgeConfigSpec.BooleanValue LENIENT_STAMINA;

    static {
        ForgeConfigSpec.Builder SHIELD_BUILDER = new ForgeConfigSpec.Builder();

        SHIELD_BUILDER.push("ShieldList");

        SHIELD_BUILDER.comment("","List of items to considered as shields, any item with right-click functionality will theoretically work", "Automatically populated with items that have parrying stats set by active datapacks");
        SHIELD_LIST = SHIELD_BUILDER.defineList("shieldList", List.of(), entry -> true);

        SHIELD_BUILDER.comment("","List of items to not consider as shields, even if they have stats set by a datapack");
        SHIELD_BLACKLIST = SHIELD_BUILDER.defineList("shieldBlacklist", List.of(), entry -> true);

        SHIELD_BUILDER.pop();

        SHIELD_BUILDER.push("Modifiers");

        SHIELD_BUILDER.comment("","Disable the cooldown when the player lowers their shield");
        STASHING_COOLDOWN = SHIELD_BUILDER.define("noLoweringCooldown", false);

        SHIELD_BUILDER.comment("","Disable putting shields on cooldown at all");
        GENERAL_COOLDOWN = SHIELD_BUILDER.define("noCooldown", false);

        SHIELD_BUILDER.comment("","Make all shields have vanilla movement speed");
        SPEED_MODIFICATION = SHIELD_BUILDER.define("noSpeedModifier", false);

        SHIELD_BUILDER.comment("","Make shields only block partial damage from explosions when not parrying");
        ADVANCED_EXPLOSIONS = SHIELD_BUILDER.define("harderExplosions", false);

        SHIELD_BUILDER.comment("","Double the parry window for a more relaxed experience");
        LENIENT_PARRY = SHIELD_BUILDER.define("lenientParryMode", false);

        SHIELD_BUILDER.comment("","Double the shield stamina for a more relaxed experience");
        LENIENT_STAMINA = SHIELD_BUILDER.define("lenientStaminaMode", false);

        SHIELD_BUILDER.pop();

        CONFIG = SHIELD_BUILDER.build();
    }

    public static Boolean isShield(Item item) {
        String itemID = ForgeRegistries.ITEMS.getKey(item).toString();
        for (String s : SHIELD_BLACKLIST.get()) if (s.equals(itemID)) return false;
        for (String s : SHIELD_LIST.get()) if (s.equals(itemID)) return true;
        return false;
    }

    //automatically adds shields to the shield list and config file on datapack load
    public static void extendList(String id) {
        List<String> newList = new java.util.ArrayList<>(List.of());
        newList.addAll(SHIELD_LIST.get());
        if (!newList.contains(id)) newList.add(id);
        SHIELD_LIST.set(newList);
    }

    public static Boolean stashingCooldownDisabled() { return STASHING_COOLDOWN.get(); }

    public static Boolean cooldownDisabled() { return GENERAL_COOLDOWN.get(); }

    public static Boolean speedModifierDisabled() { return SPEED_MODIFICATION.get(); }

    public static Boolean advancedExplosionsEnabled() { return ADVANCED_EXPLOSIONS.get(); }

    public static Boolean lenientParryEnabled() { return LENIENT_PARRY.get(); }

    public static Boolean lenientStaminaEnabled() { return LENIENT_STAMINA.get(); }
}