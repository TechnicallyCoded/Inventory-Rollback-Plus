package me.danjono.inventoryrollback.config;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class Sounds extends ConfigFile {

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

	public void setSounds(FileConfiguration config) {
		ConfigFile c = new ConfigFile(config);
		
		//If sounds are invalid they will be disabled.
		try {
			enderChest = Sound.valueOf((String) c.getDefaultValue("sounds.enderChest.sound", "ENTITY_ENDERDRAGON_FLAP"));
			enderChestEnabled = (boolean) c.getDefaultValue("sounds.enderChest.enabled", true);
			enderChestVolume = ((Double) c.getDefaultValue("sounds.enderChest.volume", 0.5)).floatValue();
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			food = Sound.valueOf((String) c.getDefaultValue("sounds.food.sound", "ENTITY_GENERIC_EAT"));
			foodEnabled = (boolean) c.getDefaultValue("sounds.food.enabled", true);
			foodVolume = ((Double) c.getDefaultValue("sounds.food.volume", 0.5)).floatValue();
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			hunger = Sound.valueOf((String) c.getDefaultValue("sounds.hunger.sound", "ENTITY_HORSE_EAT"));
			hungerEnabled = (boolean) c.getDefaultValue("sounds.hunger.enabled", true);
			hungerVolume = ((Double) c.getDefaultValue("sounds.hunger.volume", 0.5)).floatValue();
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			experience = Sound.valueOf((String) c.getDefaultValue("sounds.xp.sound", "ENTITY_PLAYER_LEVELUP"));
			experienceEnabled = (boolean) c.getDefaultValue("sounds.xp.enabled", true);
			experienceVolume = ((Double) c.getDefaultValue("sounds.xp.volume", 0.5)).floatValue();
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}
	}

}
