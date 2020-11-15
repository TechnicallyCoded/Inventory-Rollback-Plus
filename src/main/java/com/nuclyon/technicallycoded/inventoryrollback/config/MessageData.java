package com.nuclyon.technicallycoded.inventoryrollback.config;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MessageData extends ConfigFile {

    public static String pluginName;
    public static String reload;
    public static String noPermission;
    public static String error;
    public static String errorInventory;

    public static String enabledMessage;
    public static String disabledMessage;
    public static String reloadMessage;
    public static String playerOnly;

    private static String noBackup;
    private static String notOnline;
    private static String forceSaved;
    private static String notForcedSaved;

    private static String inventoryNotEmpty;
    private static String inventoryRestored;
    private static String inventoryRestoredPlayer;
    private static String inventoryNotOnline;
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
    private static String deathLocationTeleport;
    private static String deathLocationInvalidWorld;
    public static String deathLocationMessage;
    private static String deathReason;
    private static String deathTime;

    public static String mainMenuButton;
    public static String nextPageButton;
    public static String previousPageButton;
    public static String backButton;

    public static String restoreInventory;
    public static String restoreEnderChest;
    public static String restoreFood;
    public static String restoreHunger;
    public static String restoreExperience;
    private static String restoreExperienceLevel;

    public void setMessages() {
        pluginName = ChatColor.WHITE + "[" + ChatColor.AQUA + "InventoryRollbackPlus" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

        reload = convertColorCodes((String) getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
        noPermission = convertColorCodes((String) getDefaultValue("messages.noPermission", "&cYou do not have permission!"));
        error = convertColorCodes((String) getDefaultValue("messages.error", "&cInvalid command"));
        errorInventory = convertColorCodes((String) getDefaultValue("messages.errorInventory", "&cYou cannot access this backup due to an error. The backup was likely generated on another Minecraft server version and a Material ID has now changed."));

        enabledMessage = convertColorCodes((String) getDefaultValue("messages.enable", "&2The plugin has been enabled"));
        disabledMessage = convertColorCodes((String) getDefaultValue("messages.disable", "&2The plugin has been disabled"));
        reloadMessage = convertColorCodes((String) getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
        playerOnly = convertColorCodes((String) getDefaultValue("messages.playerOnly", "&cCommand can only be run by a player"));

        noBackup = convertColorCodes((String) getDefaultValue("messages.noBackup", "There is currently no backup for %NAME%"));
        notOnline = convertColorCodes((String) getDefaultValue("messages.notOnline", "%NAME% is not currently online"));
        forceSaved = convertColorCodes((String) getDefaultValue("messages.forceSaved", "%NAME%'s inventory has been force saved"));
        notForcedSaved = convertColorCodes((String) getDefaultValue("messages.notForcedSaved", "There was an issue with saving %NAME%'s inventory"));

        inventoryNotEmpty = convertColorCodes((String) getDefaultValue("messages.inventoryNotEmpty", "Cannot rollback %NAME%'s inventory yet as they have items that would be lost. Get the player to remove the items from their inventory."));
        inventoryRestored = convertColorCodes((String) getDefaultValue("messages.inventoryRestored", "%NAME%'s inventory has been restored."));
        inventoryRestoredPlayer = convertColorCodes((String) getDefaultValue("messages.inventoryRestoredPlayer", "Your inventory has been restored by %NAME%."));
        inventoryNotOnline = convertColorCodes((String) getDefaultValue("messages.inventoryNotOnline", "Cannot restore %NAME%'s inventory while they are offline."));
        enderChestNotEmpty = convertColorCodes((String) getDefaultValue("messages.enderChestNotEmpty", "Cannot rollback %NAME%'s ender chest yet as they have items that would be lost. Get the player to remove the items first from their Ender Chest."));
        enderChestRestored = convertColorCodes((String) getDefaultValue("messages.enderChestRestored", "%NAME%'s ender chest has been restored."));
        enderChestRestoredPlayer = convertColorCodes((String) getDefaultValue("messages.enderChestRestoredPlayer", "Your ender chest has been restored by %NAME%"));
        enderChestNotOnline = convertColorCodes((String) getDefaultValue("messages.enderChestNotOnline", "Cannot restore %NAME%'s ender chest while they are offline."));
        healthRestored = convertColorCodes((String) getDefaultValue("messages.healthRestored", "%NAME%'s health has been restored."));
        healthRestoredPlayer = convertColorCodes((String) getDefaultValue("messages.healthRestoredPlayer", "Your health has been restored by %NAME%"));
        healthNotOnline = convertColorCodes((String) getDefaultValue("messages.healthNotOnline", "%NAME% is not online to have their health set."));
        hungerRestored = convertColorCodes((String) getDefaultValue("messages.hungerRestored", "%NAME%'s hunger has been restored."));
        hungerRestoredPlayer = convertColorCodes((String) getDefaultValue("messages.hungerRestoredPlayer", "Your hunger has been restored by %NAME%"));
        hungerNotOnline = convertColorCodes((String) getDefaultValue("messages.hungerNotOnline", "%NAME% is not online to have their hunger set."));
        experienceRestored = convertColorCodes((String) getDefaultValue("messages.experienceRestored", "%NAME%'s XP has been set to level %XP%"));
        experienceRestoredPlayer = convertColorCodes((String) getDefaultValue("messages.experienceRestoredPlayer", "Your XP has been restored to level %XP% by %NAME%"));
        experienceNotOnline = convertColorCodes((String) getDefaultValue("messages.experienceNotOnline", "%NAME% is not online to have their XP set."));

        deathIconName = convertColorCodes((String) getDefaultValue("icons.mainMenu.deathIcon.name", "&cDeaths"));
        joinIconName = convertColorCodes((String) getDefaultValue("icons.mainMenu.joinIcon.name", "&aJoins"));
        quitIconName = convertColorCodes((String) getDefaultValue("icons.mainMenu.quitIcon.name", "&6Quits"));
        worldChangeIconName = convertColorCodes((String) getDefaultValue("icons.mainMenu.worldChangeIcon.name", "&eWorld Changes"));
        forceSaveIconName = convertColorCodes((String) getDefaultValue("icons.mainMenu.forceSaveIcon.name", "&bForce Saves"));

        deathLocationWorld = convertColorCodes((String) getDefaultValue("messages.deathLocationWorld", "&6World: &f%WORLD%"));
        deathLocationX = convertColorCodes((String) getDefaultValue("messages.deathLocationX", "&6X: &f%X%"));
        deathLocationY = convertColorCodes((String) getDefaultValue("messages.deathLocationY", "&6Y: &f%Y%"));
        deathLocationZ = convertColorCodes((String) getDefaultValue("messages.deathLocationZ", "&6Z: &f%Z%"));
        deathLocationTeleport = convertColorCodes((String) getDefaultValue("messages.deathLocationTeleport", "You have been teleported to %LOCATION%"));
        deathLocationInvalidWorld = convertColorCodes((String) getDefaultValue("messages.deathLocationInvalidWorld", "The world %WORLD% is not currently loaded on the server."));
        deathLocationMessage = convertColorCodes((String) getDefaultValue("messages.deathLocationMessage", "&3Teleport to where this entry was logged."));
        deathReason = convertColorCodes((String) getDefaultValue("messages.deathReason", "&6Death reason: &f%REASON%"));
        deathTime = convertColorCodes((String) getDefaultValue("messages.deathTime", "&6Time: &f%TIME%"));

        mainMenuButton = convertColorCodes((String) getDefaultValue("messages.mainMenuButton", "&fMain Menu"));
        nextPageButton = convertColorCodes((String) getDefaultValue("messages.nextPageButton", "&fNext Page"));
        previousPageButton = convertColorCodes((String) getDefaultValue("messages.previousPageButton", "&fPrevious Page"));
        backButton = convertColorCodes((String) getDefaultValue("messages.backButton", "&fBack"));

        restoreInventory = convertColorCodes((String) getDefaultValue("messages.restoreInventory", "&6Restore Inventory"));
        restoreEnderChest = convertColorCodes((String) getDefaultValue("messages.restoreEnderChest", "&dRestore Ender Chest"));
        restoreFood = convertColorCodes((String) getDefaultValue("messages.restoreFood", "&aRestore Health"));
        restoreHunger = convertColorCodes((String) getDefaultValue("messages.restoreHunger", "&cRestore Food"));
        restoreExperience = convertColorCodes((String) getDefaultValue("messages.restoreExperience", "&2Restore Player XP"));
        restoreExperienceLevel = convertColorCodes((String) getDefaultValue("messages.restoreExperienceLevel", "&rLevel %XP%"));
    }

    private static String convertColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String noBackup(String name) {
        return noBackup.replaceAll("%NAME%", name);
    }

    public String notOnline(String name) {
        return notOnline.replaceAll("%NAME%", name);
    }

    public String forceSaved(String name) {
        return forceSaved.replaceAll("%NAME%", name);
    }

    public String notForcedSaved(String name) {
        return notForcedSaved.replaceAll("%NAME%", name);
    }

    public String inventoryNotEmpty(String name) {
        return inventoryNotEmpty.replaceAll("%NAME%", name);
    }

    public String inventoryRestored(String name) {
        return inventoryRestored.replaceAll("%NAME%", name);
    }

    public String inventoryRestoredPlayer(String name) {
        return inventoryRestoredPlayer.replaceAll("%NAME%", name);
    }

    public String inventoryNotOnline(String name) {
        return inventoryNotOnline.replaceAll("%NAME%", name);
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

    public String deathLocationTeleport(Location location) {
        return deathLocationTeleport.replaceAll("%LOCATION%", "X:" + location.getX() + " Y:" + location.getY() + " Z:" + location.getZ());
    }

    public String deathLocationInvalidWorld(String world) {
        return deathLocationInvalidWorld.replaceAll("%WORLD%", world);
    }

    public String deathReason(String reason) {
        return deathReason.replaceAll("%REASON%", reason);
    }

    public String deathTime(String time) {
        return deathTime.replaceAll("%TIME%", time);
    }

    public String restoreExperienceLevel(String xp) {
        return restoreExperienceLevel.replaceAll("%XP%", xp);
    }
}
