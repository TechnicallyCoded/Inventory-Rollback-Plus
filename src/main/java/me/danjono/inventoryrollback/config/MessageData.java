package me.danjono.inventoryrollback.config;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class MessageData extends ConfigFile {

    public static String pluginName;
    public static String reload;
    public static String noPermission;
    public static String error;
    public static String errorInventory;
    public static String inventoryLoadFailKickMessage;

    public static String enabledMessage;
    public static String disabledMessage;
    public static String reloadMessage;
    public static String playerOnly;

    private static String noBackup;
    private static String notOnline;
    private static String forceSaved;
    private static String notForcedSaved;

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

    public static String restoreEnderChest;
    public static String restoreFood;
    public static String restoreHunger;
    public static String restoreExperience;
    private static String restoreExperienceLevel;

    public void setMessages() {
        pluginName = ChatColor.WHITE + "[" + ChatColor.AQUA + "InventoryRollback" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";

        reload = convertColourCodes((String) getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
        noPermission = convertColourCodes((String) getDefaultValue("messages.noPermission", "&cYou do not have permission!"));
        error = convertColourCodes((String) getDefaultValue("messages.error", "&cInvalid command"));
        errorInventory = convertColourCodes((String) getDefaultValue("messages.errorInventory", "&cYou cannot access this backup due to an error. The backup was likely generated on another Minecraft server version and a Material ID has now changed."));
        inventoryLoadFailKickMessage = convertColourCodes((String) getDefaultValue("messages.inventoryLoadFailKickMessage", "Failed to load your inventory! Please try again by rejoining the server."));

        enabledMessage = convertColourCodes((String) getDefaultValue("messages.enable", "&2The plugin has been enabled"));
        disabledMessage = convertColourCodes((String) getDefaultValue("messages.disable", "&2The plugin has been disabled"));
        reloadMessage = convertColourCodes((String) getDefaultValue("messages.reload", "&2The plugin has been reloaded successfully"));
        playerOnly = convertColourCodes((String) getDefaultValue("messages.playerOnly", "&cCommand can only be run by a player"));

        noBackup = convertColourCodes((String) getDefaultValue("messages.noBackup", "There is currently no backup for %NAME%"));
        notOnline = convertColourCodes((String) getDefaultValue("messages.notOnline", "%NAME% is not currently online"));
        forceSaved = convertColourCodes((String) getDefaultValue("messages.forceSaved", "%NAME%'s inventory has been force saved"));
        notForcedSaved = convertColourCodes((String) getDefaultValue("messages.notForcedSaved", "There was an issue with saving %NAME%'s inventory"));

        enderChestNotEmpty = convertColourCodes((String) getDefaultValue("messages.enderChestNotEmpty", "Cannot rollback %NAME%'s ender chest yet as they have items in it that would be lost. Get the player to remove the items first from their Ender Chest."));
        enderChestRestored = convertColourCodes((String) getDefaultValue("messages.enderChestRestored", "%NAME%'s ender chest has been restored."));
        enderChestRestoredPlayer = convertColourCodes((String) getDefaultValue("messages.enderChestRestoredPlayer", "Your ender chest has been restored by %NAME%"));
        enderChestNotOnline = convertColourCodes((String) getDefaultValue("messages.enderChestNotOnline", "%NAME% is not online to have their ender chest set."));
        healthRestored = convertColourCodes((String) getDefaultValue("messages.healthRestored", "%NAME%'s health has been restored."));
        healthRestoredPlayer = convertColourCodes((String) getDefaultValue("messages.healthRestoredPlayer", "Your health has been restored by %NAME%"));
        healthNotOnline = convertColourCodes((String) getDefaultValue("messages.healthNotOnline", "%NAME% is not online to have their health set."));
        hungerRestored = convertColourCodes((String) getDefaultValue("messages.hungerRestored", "%NAME%'s hunger has been restored."));
        hungerRestoredPlayer = convertColourCodes((String) getDefaultValue("messages.hungerRestoredPlayer", "Your hunger has been restored by %NAME%"));
        hungerNotOnline = convertColourCodes((String) getDefaultValue("messages.hungerNotOnline", "%NAME% is not online to have their hunger set."));
        experienceRestored = convertColourCodes((String) getDefaultValue("messages.experienceRestored", "%NAME%'s XP has been set to level %XP%"));
        experienceRestoredPlayer = convertColourCodes((String) getDefaultValue("messages.experienceRestoredPlayer", "Your XP has been restored to level %XP% by %NAME%"));
        experienceNotOnline = convertColourCodes((String) getDefaultValue("messages.experienceNotOnline", "%NAME% is not online to have their XP set."));

        deathIconName = convertColourCodes((String) getDefaultValue("icons.mainMenu.deathIcon.name", "&cDeaths"));
        joinIconName = convertColourCodes((String) getDefaultValue("icons.mainMenu.joinIcon.name", "&aJoins"));
        quitIconName = convertColourCodes((String) getDefaultValue("icons.mainMenu.quitIcon.name", "&6Quits"));
        worldChangeIconName = convertColourCodes((String) getDefaultValue("icons.mainMenu.worldChangeIcon.name", "&eWorld Changes"));
        forceSaveIconName = convertColourCodes((String) getDefaultValue("icons.mainMenu.forceSaveIcon.name", "&bForce Saves"));

        deathLocationWorld = convertColourCodes((String) getDefaultValue("messages.deathLocationWorld", "&6World: &f%WORLD%"));
        deathLocationX = convertColourCodes((String) getDefaultValue("messages.deathLocationX", "&6X: &f%X%"));
        deathLocationY = convertColourCodes((String) getDefaultValue("messages.deathLocationY", "&6Y: &f%Y%"));
        deathLocationZ = convertColourCodes((String) getDefaultValue("messages.deathLocationZ", "&6Z: &f%Z%"));
        deathLocationTeleport = convertColourCodes((String) getDefaultValue("messages.deathLocationTeleport", "You have been teleported to %LOCATION%"));
        deathLocationInvalidWorld = convertColourCodes((String) getDefaultValue("messages.deathLocationInvalidWorld", "The world %WORLD% is not currently loaded on the server."));
        deathLocationMessage = convertColourCodes((String) getDefaultValue("messages.deathLocationMessage", "&3Teleport to where this entry was logged."));
        deathReason = convertColourCodes((String) getDefaultValue("messages.deathReason", "&6Death reason: &f%REASON%"));
        deathTime = convertColourCodes((String) getDefaultValue("messages.deathTime", "&6Time: &f%TIME%"));

        mainMenuButton = convertColourCodes((String) getDefaultValue("messages.mainMenuButton", "&fMain Menu"));
        nextPageButton = convertColourCodes((String) getDefaultValue("messages.nextPageButton", "&fNext Page"));
        previousPageButton = convertColourCodes((String) getDefaultValue("messages.previousPageButton", "&fPrevious Page"));
        backButton = convertColourCodes((String) getDefaultValue("messages.backButton", "&fBack"));

        restoreEnderChest = convertColourCodes((String) getDefaultValue("messages.restoreEnderChest", "&dRestore Ender Chest"));
        restoreFood = convertColourCodes((String) getDefaultValue("messages.restoreFood", "&aRestore Health"));
        restoreHunger = convertColourCodes((String) getDefaultValue("messages.restoreHunger", "&cRestore Food"));
        restoreExperience = convertColourCodes((String) getDefaultValue("messages.restoreExperience", "&2Restore Player XP"));
        restoreExperienceLevel = convertColourCodes((String) getDefaultValue("messages.restoreExperienceLevel", "&rLevel %XP%"));
    }

    private static String convertColourCodes(String text) {
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
