package me.danjono.inventoryrollback.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages extends ConfigFile {
		
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
	public static String forceSaveIconName;
	
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
    	ConfigFile c = new ConfigFile(config);
    	
    	pluginName = ChatColor.WHITE + "[" + ChatColor.AQUA + "InventoryRollback" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";
    	
    	reload = convertColourCodes((String) c.getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
    	noPermission = convertColourCodes((String) c.getDefaultValue("messages.noPermission", "&cYou do not have permission!"));
    	error = convertColourCodes((String) c.getDefaultValue("messages.error", "&cInvalid command"));
    	
    	enabledMessage = convertColourCodes((String) c.getDefaultValue("messages.enable", "&2The plugin has been enabled"));
		disabledMessage = convertColourCodes((String) c.getDefaultValue("messages.disable", "&2The plugin has been disabled"));
		reloadMessage = convertColourCodes((String) c.getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
		playerOnly = convertColourCodes((String) c.getDefaultValue("messages.playerOnly", "&cCommand can only be run by a player"));
		
		neverOnServer = convertColourCodes((String) c.getDefaultValue("messages.neverOnServer", "%NAME% has never played on this server"));
		
		enderChestNotEmpty = convertColourCodes((String) c.getDefaultValue("messages.enderChestNotEmpty", "Cannot rollback %NAME%'s ender chest yet as they have items in it that would be lost. Get the player to remove the items first from their Ender Chest."));
		enderChestRestored = convertColourCodes((String) c.getDefaultValue("messages.enderChestRestored", "%NAME%''s ender chest has been restored."));
		enderChestRestoredPlayer = convertColourCodes((String) c.getDefaultValue("messages.enderChestRestoredPlayer", "Your ender chest has been restored by %NAME%"));
		enderChestNotOnline = convertColourCodes((String) c.getDefaultValue("messages.enderChestNotOnline", "%NAME% is not online to have their ender chest set."));
		healthRestored = convertColourCodes((String) c.getDefaultValue("messages.healthRestored", "%NAME%''s health has been restored."));
		healthRestoredPlayer = convertColourCodes((String) c.getDefaultValue("messages.healthRestoredPlayer", "Your health has been restored by %NAME%"));
		healthNotOnline = convertColourCodes((String) c.getDefaultValue("messages.healthNotOnline", "%NAME% is not online to have their health set."));
		hungerRestored = convertColourCodes((String) c.getDefaultValue("messages.hungerRestored", "%NAME%''s hunger has been restored."));
		hungerRestoredPlayer = convertColourCodes((String) c.getDefaultValue("messages.hungerRestoredPlayer", "Your hunger has been restored by %NAME%"));
		hungerNotOnline = convertColourCodes((String) c.getDefaultValue("messages.hungerNotOnline", "%NAME% is not online to have their hunger set."));
		experienceRestored = convertColourCodes((String) c.getDefaultValue("messages.experienceRestored", "%NAME%''s XP has been set to level %XP%"));
		experienceRestoredPlayer = convertColourCodes((String) c.getDefaultValue("messages.experienceRestoredPlayer", "Your XP has been restored to level %XP% by %NAME%"));
		experienceNotOnline = convertColourCodes((String) c.getDefaultValue("messages.experienceNotOnline", "%NAME% is not online to have their XP set."));
		
		deathIconName = convertColourCodes((String) c.getDefaultValue("icons.mainMenu.deathIcon.name", "&cDeaths"));
		joinIconName = convertColourCodes((String) c.getDefaultValue("icons.mainMenu.joinIcon.name", "&aJoins"));
		quitIconName = convertColourCodes((String) c.getDefaultValue("icons.mainMenu.quitIcon.name", "&6Quits"));
		worldChangeIconName = convertColourCodes((String) c.getDefaultValue("icons.mainMenu.worldChangeIcon.name", "&eWorld Changes"));
		forceSaveIconName = convertColourCodes((String) c.getDefaultValue("icons.mainMenu.forceSaveIcon.name", "&bForce Saves"));
		
		deathLocationWorld = convertColourCodes((String) c.getDefaultValue("messages.deathLocationWorld", "&6World: &f%WORLD%"));
		deathLocationX = convertColourCodes((String) c.getDefaultValue("messages.deathLocationX", "&6X: &f%X%"));
		deathLocationY = convertColourCodes((String) c.getDefaultValue("messages.deathLocationY", "&6Y: &f%Y%"));
		deathLocationZ = convertColourCodes((String) c.getDefaultValue("messages.deathLocationZ", "&6Z: &f%Z%"));
		deathReason = convertColourCodes((String) c.getDefaultValue("messages.deathReason", "&6Death reason: &f%REASON%"));
		deathTime = convertColourCodes((String) c.getDefaultValue("messages.deathTime", "&6Time: &f%TIME%"));
		
		mainMenuButton = convertColourCodes((String) c.getDefaultValue("messages.mainMenuButton", "&fMain Menu"));
		nextPageButton = convertColourCodes((String) c.getDefaultValue("messages.nextPageButton", "&fNext Page"));
		previousPageButton = convertColourCodes((String) c.getDefaultValue("messages.previousPageButton", "&fPrevious Page"));
		backButton  = convertColourCodes((String) c.getDefaultValue("messages.backButton", "&fBack"));
		
		restoreEnderChest = convertColourCodes((String) c.getDefaultValue("messages.restoreEnderChest", "&dRestore Ender Chest"));
		restoreFood = convertColourCodes((String) c.getDefaultValue("messages.restoreFood", "&aRestore Health"));
		restoreHunger = convertColourCodes((String) c.getDefaultValue("messages.restoreHunger", "&cRestore Food"));
		restoreExperience = convertColourCodes((String) c.getDefaultValue("messages.restoreExperience", "&2Restore Player XP"));
		restoreExperienceLevel = convertColourCodes((String) c.getDefaultValue("messages.restoreExperienceLevel", "&rLevel %XP%"));
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
