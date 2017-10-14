package me.danjono.inventoryrollback.config;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class Sounds {

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
		//If sounds are invalid they will be disabled.
		try {
			enderChest = Sound.valueOf(config.getString("sounds.enderChest.sound"));
			enderChestEnabled = config.getBoolean("sounds.enderChest.enabled");
			enderChestVolume = Float.parseFloat(config.getString("sounds.enderChest.volume"));
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			food = Sound.valueOf(config.getString("sounds.food.sound"));
			foodEnabled = config.getBoolean("sounds.food.enabled");
			foodVolume = Float.parseFloat(config.getString("sounds.food.volume"));
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			hunger = Sound.valueOf(config.getString("sounds.hunger.sound"));
			hungerEnabled = config.getBoolean("sounds.hunger.enabled");
			hungerVolume = Float.parseFloat(config.getString("sounds.hunger.volume"));
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}

		try {
			experience = Sound.valueOf(config.getString("sounds.xp.sound"));
			experienceEnabled = config.getBoolean("sounds.xp.enabled");
			experienceVolume = Float.parseFloat(config.getString("sounds.xp.volume"));
		} catch (IllegalArgumentException e) {
			enderChestEnabled = false;
		}
	}

}
