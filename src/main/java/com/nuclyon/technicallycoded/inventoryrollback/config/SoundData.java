package com.nuclyon.technicallycoded.inventoryrollback.config;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import org.bukkit.Sound;

public class SoundData extends ConfigFile {

    public static Sound enderPearl;
    public static boolean enderPearlEnabled;
    public static float enderPearlVolume;

    public static Sound inventory;
    public static boolean inventoryEnabled;
    public static float inventoryVolume;

    public static Sound enderChest;
    public static boolean enderChestEnabled;
    public static float enderChestVolume;

    public static Sound food;
    public static boolean foodEnabled;
    public static float foodVolume;

    public static Sound hunger;
    public static boolean hungerEnabled;
    public static float hungerVolume;

    public static Sound experience;
    public static boolean experienceEnabled;
    public static float experienceVolume;

    public void setSounds() {

        //If sounds are invalid they will be disabled.
        try {
            enderPearl = Sound.valueOf((String) getDefaultValue("sounds.enderPearl.sound", "ENTITY_ENDERMEN_TELEPORT"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                enderPearl = Sound.valueOf("ENDERMAN_TELEPORT");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                enderPearl = Sound.valueOf("ENTITY_ENDERMEN_TELEPORT");
            } else {
                enderPearl = Sound.valueOf("ENTITY_ENDERMAN_TELEPORT");
            }
        }
        enderPearlEnabled = (boolean) getDefaultValue("sounds.enderPearl.enabled", true);
        enderPearlVolume = ((Double) getDefaultValue("sounds.enderPearl.volume", 0.5)).floatValue();

        try {
            inventory = Sound.valueOf((String) getDefaultValue("sounds.inventory.sound", "BLOCK_CHEST_CLOSE"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                inventory = Sound.valueOf("CHEST_CLOSE");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                inventory = Sound.valueOf("CHEST_CLOSE");
            } else {
                inventory = Sound.valueOf("BLOCK_CHEST_CLOSE");
            }
        }
        inventoryEnabled = (boolean) getDefaultValue("sounds.inventory.enabled", true);
        inventoryVolume = ((Double) getDefaultValue("sounds.inventory.volume", 0.5)).floatValue();

        try {
            enderChest = Sound.valueOf((String) getDefaultValue("sounds.enderChest.sound", "ENTITY_ENDER_DRAGON_FLAP"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                enderChest = Sound.valueOf("ENDERDRAGON_WINGS");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                enderChest = Sound.valueOf("ENTITY_ENDERDRAGON_FLAP");
            } else {
                enderChest = Sound.valueOf("ENTITY_ENDER_DRAGON_FLAP");
            }
        }
        enderChestEnabled = (boolean) getDefaultValue("sounds.enderChest.enabled", true);
        enderChestVolume = ((Double) getDefaultValue("sounds.enderChest.volume", 0.5)).floatValue();

        try {
            food = Sound.valueOf((String) getDefaultValue("sounds.food.sound", "ENTITY_GENERIC_EAT"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                food = Sound.valueOf("EAT");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                food = Sound.valueOf("ENTITY_GENERIC_EAT");
            } else {
                food = Sound.valueOf("ENTITY_GENERIC_EAT");
            }
        }
        foodEnabled = (boolean) getDefaultValue("sounds.food.enabled", true);
        foodVolume = ((Double) getDefaultValue("sounds.food.volume", 0.5)).floatValue();

        try {
            hunger = Sound.valueOf((String) getDefaultValue("sounds.hunger.sound", "ENTITY_HORSE_EAT"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                hunger = Sound.valueOf("HORSE_IDLE");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                hunger = Sound.valueOf("ENTITY_HORSE_EAT");
            } else {
                hunger = Sound.valueOf("ENTITY_HORSE_EAT");
            }
        }
        hungerEnabled = (boolean) getDefaultValue("sounds.hunger.enabled", true);
        hungerVolume = ((Double) getDefaultValue("sounds.hunger.volume", 0.5)).floatValue();

        try {
            experience = Sound.valueOf((String) getDefaultValue("sounds.xp.sound", "ENTITY_PLAYER_LEVELUP"));
        } catch (IllegalArgumentException e) {
            if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_8)) {
                experience = Sound.valueOf("LEVEL_UP");
            } else if (InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_9_v1_12)) {
                experience = Sound.valueOf("ENTITY_PLAYER_LEVELUP");
            } else {
                experience = Sound.valueOf("ENTITY_PLAYER_LEVELUP");
            }
        }
        experienceEnabled = (boolean) getDefaultValue("sounds.xp.enabled", true);
        experienceVolume = ((Double) getDefaultValue("sounds.xp.volume", 0.5)).floatValue();

    }

}
