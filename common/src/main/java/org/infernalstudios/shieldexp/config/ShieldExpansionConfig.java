package org.infernalstudios.shieldexp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import org.infernalstudios.shieldexp.Constants;
import org.infernalstudios.shieldexp.init.ShieldDataLoader;
import org.infernalstudios.shieldexp.platform.Services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShieldExpansionConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static List<String> SHIELD_LIST = new ArrayList<>();
    public static List<String> SHIELD_BLACKLIST = new ArrayList<>();

    public static boolean ITEM_ONLY_MODE = false;

    public static boolean TOOLTIPS = true;

    public static boolean TOOLTIP_COOLDOWN = true;
    public static boolean TOOLTIP_STAMINA = false;
    public static boolean TOOLTIP_SPEED = false;
    public static boolean TOOLTIP_PARRY_DAMAGE = false;
    public static boolean TOOLTIP_PARRY_WINDOW = true;
    public static boolean TOOLTIP_BLAST_RESISTANCE = false;

    public static boolean STASHING_COOLDOWN = true;
    public static boolean GENERAL_COOLDOWN = true;
    public static boolean SPEED_MODIFICATION = true;

    public static boolean ADVANCED_EXPLOSIONS = false;
    public static boolean LENIENT_PARRY = false;
    public static boolean LENIENT_STAMINA = false;
    private static File CONFIG_FILE;

    public static void load() {
        if (CONFIG_FILE == null) {
            CONFIG_FILE = Services.PLATFORM.getConfigDirectory().resolve(Constants.MOD_ID + "-common.json").toFile();
        }

        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                SHIELD_LIST = data.shieldList;
                SHIELD_BLACKLIST = data.shieldBlacklist;
                ITEM_ONLY_MODE = data.itemOnlyMode;
                STASHING_COOLDOWN = data.stashingCooldown;
                GENERAL_COOLDOWN = data.generalCooldown;
                SPEED_MODIFICATION = data.speedModification;
                ADVANCED_EXPLOSIONS = data.advancedExplosions;
                LENIENT_PARRY = data.lenientParry;
                LENIENT_STAMINA = data.lenientStamina;
                TOOLTIPS = data.tooltips;
                TOOLTIP_COOLDOWN = data.tooltipCooldown;
                TOOLTIP_STAMINA = data.tooltipStamina;
                TOOLTIP_SPEED = data.tooltipSpeed;
                TOOLTIP_PARRY_DAMAGE = data.tooltipParryDamage;
                TOOLTIP_PARRY_WINDOW = data.tooltipParryWindow;
                TOOLTIP_BLAST_RESISTANCE = data.tooltipBlastResistance;
            }
        } catch (IOException e) {
            Constants.LOG.error("Failed to load config", e);
        }
    }

    public static void save() {
        ConfigData data = new ConfigData();
        data.shieldList = SHIELD_LIST;
        data.shieldBlacklist = SHIELD_BLACKLIST;
        data.itemOnlyMode = ITEM_ONLY_MODE;
        data.stashingCooldown = STASHING_COOLDOWN;
        data.generalCooldown = GENERAL_COOLDOWN;
        data.speedModification = SPEED_MODIFICATION;
        data.advancedExplosions = ADVANCED_EXPLOSIONS;
        data.lenientParry = LENIENT_PARRY;
        data.lenientStamina = LENIENT_STAMINA;

        data.tooltipCooldown = TOOLTIP_COOLDOWN;
        data.tooltipStamina = TOOLTIP_STAMINA;
        data.tooltipSpeed = TOOLTIP_SPEED;
        data.tooltipParryDamage = TOOLTIP_PARRY_DAMAGE;
        data.tooltipParryWindow = TOOLTIP_PARRY_WINDOW;
        data.tooltipBlastResistance = TOOLTIP_BLAST_RESISTANCE;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            Constants.LOG.error("Failed to save config", e);
        }
    }

    public static Boolean isShield(Item item) {
        String itemID = BuiltInRegistries.ITEM.getKey(item).toString();
        for (String s : SHIELD_BLACKLIST) if (s.equals(itemID)) return false;

        for (String s : SHIELD_LIST) if (s.equals(itemID)) return true;

        return SHIELD_LIST.contains(itemID) ||
                (item instanceof ShieldItem && (BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Constants.MOD_ID) || ShieldDataLoader.SHIELD_STATS.containsKey(itemID)));
    }

    public static void extendList(String id) {
        if (!SHIELD_LIST.contains(id)) {
            SHIELD_LIST.add(id);
        }
    }

    public static Boolean stashingCooldownEnabled() {
        return STASHING_COOLDOWN;
    }

    public static Boolean cooldownEnabled() {
        return GENERAL_COOLDOWN;
    }

    public static Boolean speedModifierEnabled() {
        return SPEED_MODIFICATION;
    }

    public static Boolean advancedExplosionsEnabled() {
        return ADVANCED_EXPLOSIONS;
    }

    public static Boolean lenientParryEnabled() {
        return LENIENT_PARRY;
    }

    public static Boolean lenientStaminaEnabled() {
        return LENIENT_STAMINA;
    }

    private static class ConfigData {
        List<String> shieldList = new ArrayList<>();
        List<String> shieldBlacklist = new ArrayList<>();
        boolean itemOnlyMode = false;
        boolean stashingCooldown = true;
        boolean generalCooldown = true;
        boolean speedModification = true;
        boolean advancedExplosions = false;
        boolean lenientParry = false;
        boolean lenientStamina = false;

        boolean tooltips = true;
        boolean tooltipCooldown = true;
        boolean tooltipStamina = false;
        boolean tooltipSpeed = false;
        boolean tooltipParryDamage = false;
        boolean tooltipParryWindow = true;
        boolean tooltipBlastResistance = false;
    }
}