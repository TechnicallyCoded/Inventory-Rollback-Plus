package me.danjono.inventoryrollback.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
		
	public static String pluginName;
	public static String reload;
	public static String noPermission;
	public static String error;
	
	public static String enabledMessage;
	public static String disabledMessage;
	public static String reloadMessage;
	public static String playerOnly;
	
	private static String neverOnServer;
	
	private static String enderChestNotEmpty;
	private static String enderChestRestored;
	private static String enderChestRestoredPlayer;
	private static String enderChestNotOnline;
	private static String healthRestored;
	private static String healthRestoredPlayer;
	private static String healthNotOnline;
	private static String hungerRestored;
	private static String hungerRestoredPlayer;
	private static String hungerNotOnline;
	private static String experienceRestored;
	private static String experienceRestoredPlayer;
	private static String experienceNotOnline;
	
	public static String deathIconName;
	public static String joinIconName;
	public static String quitIconName;
	public static String worldChangeIconName;
	
	private static String deathLocationWorld;
	private static String deathLocationX;
	private static String deathLocationY;
	private static String deathLocationZ;
	private static String deathReason;
	private static String deathTime;
	
	public static String mainMenuButton;
	public static String nextPageButton;
	public static String previousPageButton;
	public static String backButton;

	public static String restoreEnderChest;
	public static String restoreFood;
	public static String restoreHunger;
	public static String restoreExperience;
	private static String restoreExperienceLevel;
	
    public void setMessages(FileConfiguration config) {       	
    	pluginName = ChatColor.WHITE + "[" + ChatColor.AQUA + "InventoryRollback" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";
    	reload = convertColourCodes(config.getString("messages.reload"));
    	noPermission = convertColourCodes(config.getString("messages.noPermission"));
    	error = convertColourCodes(config.getString("messages.error"));
    	
    	enabledMessage = convertColourCodes(config.getString("messages.enable"));
		disabledMessage = convertColourCodes(config.getString("messages.disable"));
		reloadMessage = convertColourCodes(config.getString("messages.reload"));
		playerOnly = convertColourCodes(config.getString("messages.playerOnly"));
		
		neverOnServer = convertColourCodes(config.getString("messages.neverOnServer"));
		
		enderChestNotEmpty = convertColourCodes(config.getString("messages.enderChestNotEmpty"));
		enderChestRestored = convertColourCodes(config.getString("messages.enderChestRestored"));
		enderChestRestoredPlayer = convertColourCodes(config.getString("messages.enderChestRestoredPlayer"));
		enderChestNotOnline = convertColourCodes(config.getString("messages.enderChestNotOnline"));
		healthRestored = convertColourCodes(config.getString("messages.healthRestored"));
		healthRestoredPlayer = convertColourCodes(config.getString("messages.healthRestoredPlayer"));
		healthNotOnline = convertColourCodes(config.getString("messages.healthNotOnline"));
		hungerRestored = convertColourCodes(config.getString("messages.hungerRestored"));
		hungerRestoredPlayer = convertColourCodes(config.getString("messages.hungerRestoredPlayer"));
		hungerNotOnline = convertColourCodes(config.getString("messages.hungerNotOnline"));
		experienceRestored = convertColourCodes(config.getString("messages.experienceRestored"));
		experienceRestoredPlayer = convertColourCodes(config.getString("messages.experienceRestoredPlayer"));
		experienceNotOnline = convertColourCodes(config.getString("messages.experienceNotOnline"));
		
		deathIconName = convertColourCodes(config.getString("icons.mainMenu.deathIcon.name"));
		joinIconName = convertColourCodes(config.getString("icons.mainMenu.joinIcon.name"));
		quitIconName = convertColourCodes(config.getString("icons.mainMenu.quitIcon.name"));
		worldChangeIconName = convertColourCodes(config.getString("icons.mainMenu.worldChangeIcon.name"));
		
		deathLocationWorld = convertColourCodes(config.getString("messages.deathLocationWorld"));
		deathLocationX = convertColourCodes(config.getString("messages.deathLocationX"));
		deathLocationY = convertColourCodes(config.getString("messages.deathLocationY"));
		deathLocationZ = convertColourCodes(config.getString("messages.deathLocationZ"));
		deathReason = convertColourCodes(config.getString("messages.deathReason"));
		deathTime = convertColourCodes(config.getString("messages.deathTime"));
		
		mainMenuButton = convertColourCodes(config.getString("messages.mainMenuButton"));
		nextPageButton = convertColourCodes(config.getString("messages.nextPageButton"));
		previousPageButton = convertColourCodes(config.getString("messages.previousPageButton"));
		backButton  = convertColourCodes(config.getString("messages.backButton"));
		
		restoreEnderChest = convertColourCodes(config.getString("messages.restoreEnderChest"));
		restoreFood = convertColourCodes(config.getString("messages.restoreFood"));
		restoreHunger = convertColourCodes(config.getString("messages.restoreHunger"));
		restoreExperience = convertColourCodes(config.getString("messages.restoreExperience"));
		restoreExperienceLevel = convertColourCodes(config.getString("messages.restoreExperienceLevel"));
    }
    
    private static String convertColourCodes(String text) {
    	return ChatColor.translateAlternateColorCodes('&', text);
    }
    
    public String neverOnServer(String name) {
    	return neverOnServer.replaceAll("%NAME%", name);
    }
    
    public String enderChestNotEmpty(String name) {
    	return enderChestNotEmpty.replaceAll("%NAME%", name);
    }
    
    public String enderChestRestored(String name) {
    	return enderChestRestored.replaceAll("%NAME%", name);
    }
    
    public String enderChestRestoredPlayer(String name) {
    	return enderChestRestoredPlayer.replaceAll("%NAME%", name);
    }
    
    public String enderChestNotOnline(String name) {
    	return enderChestNotOnline.replaceAll("%NAME%", name);
    }
    
    public String healthRestored(String name) {
    	return healthRestored.replaceAll("%NAME%", name);
    }
    
    public String healthRestoredPlayer(String name) {
    	return healthRestoredPlayer.replaceAll("%NAME%", name);
    }
    
    public String healthNotOnline(String name) {
    	return healthNotOnline.replaceAll("%NAME%", name);
    }
    
    public String hungerRestored(String name) {
    	return hungerRestored.replaceAll("%NAME%", name);
    }
    
    public String hungerRestoredPlayer(String name) {
    	return hungerRestoredPlayer.replaceAll("%NAME%", name);
    }
    
    public String hungerNotOnline(String name) {
    	return hungerNotOnline.replaceAll("%NAME%", name);
    }
    
    public String experienceRestored(String name, int xp) {
    	return experienceRestored.replaceAll("%NAME%", name).replaceAll("%XP%", xp + "");
    }
    
    public String experienceRestoredPlayer(String name, int xp) {
    	return experienceRestoredPlayer.replaceAll("%NAME%", name).replaceAll("%XP%", xp + "");
    }
    
    public String experienceNotOnline(String name) {
    	return experienceNotOnline.replaceAll("%NAME%", name);
    }
    
    public String deathLocationWorld(String world) {
    	return deathLocationWorld.replaceAll("%WORLD%", world);
    }
    
    public String deathLocationX(String x) {
    	return deathLocationX.replaceAll("%X%", x);
    }
    
    public String deathLocationY(String y) {
    	return deathLocationY.replaceAll("%Y%", y);
    }
    
    public String deathLocationZ(String z) {
    	return deathLocationZ.replaceAll("%Z%", z);
    }
    
    public String deathReason(String reason) {
    	return deathReason.replaceAll("%REASON%", reason);
    }

    public String deathTime (String time) {
    	return deathTime.replaceAll("%TIME%", time);
    }
    
    public String restoreExperienceLevel (String xp) {
    	return restoreExperienceLevel.replaceAll("%XP%", xp);
    }
}
