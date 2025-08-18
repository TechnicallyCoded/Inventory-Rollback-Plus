package me.danjono.inventoryrollback.config;

import me.danjono.inventoryrollback.InventoryRollback;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class MessageData {

    private File messagesFile;
    private FileConfiguration messages;
    private static final String configurationFileName = "messages.yml";

    public MessageData() {
        generateMessagesFile();
    }

    private void generateMessagesFile() {
        getMessagesFile();
        if(!messagesFile.exists()) {
            InventoryRollback.getInstance().saveResource(configurationFileName, false);
            getMessagesFile();
        }
        getMessagesData();
    }

    private void getMessagesFile() {
        messagesFile = new File(InventoryRollback.getInstance().getDataFolder(), configurationFileName);
    }

    private void getMessagesData() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public boolean saveConfig() {
        try {
            messages.save(messagesFile);
        } catch (IOException e) {
            InventoryRollback.getInstance().getLogger().log(Level.SEVERE, "Could not save data to messages file", e);
            return false;
        }

        saveChanges = false;

        return true;
    }

    private boolean saveChanges = false;

    // Generic messages
    private static String pluginPrefix;
    private static String adminAlerts = ChatColor.RED + "Admin Only Alert: null";
    private static String noPermission;
    private static String error;
    private static String errorInventory = "You cannot access this backup due to an error. The backup was likely generated on another Minecraft server version and a Material ID has now changed.";

    // Command (all) messages
    private static String enabledMessage;
    private static String disabledMessage;
    private static String reloadMessage;
    private static String playerOnly;

    // Command (import) messages
    private static String importSuccess;

    // Command (force backup) messages
    private static String noBackup;
    private static String notOnline;
    private static String forceSavedPlayer;
    private static String forceSavedAll;
    private static String notForcedSaved;

    // Main inventory restore gui messages
    private static String mainInventoryRestored;
    private static String mainInventoryRestoredPlayer;
    private static String mainInventoryNotOnline;
    private static String mainInventoryButton;
    private static String mainInventoryDisabledButton;

    // Ender chest GUI messages
    private static String enderChestRestored;
    private static String enderChestRestoredPlayer;
    private static String enderChestNotOnline;
    private static String enderChestButton;

    // Health GUI messages
    private static String healthRestored;
    private static String healthRestoredPlayer;
    private static String healthNotOnline;
    private static String healthButton;

    // Hunger GUI messages
    private static String hungerRestored;
    private static String hungerRestoredPlayer;
    private static String hungerNotOnline;
    private static String hungerButton;

    // XP GUI messages
    private static String experienceRestored;
    private static String experienceRestoredPlayer;
    private static String experienceNotOnline;
    private static String experienceButton;
    private static String experienceButtonLore;

    // Death logs messages
    private static String deathLocationWorld;
    private static String deathLocationX;
    private static String deathLocationY;
    private static String deathLocationZ;
    private static String deathReason;
    private static String deathTime;
    private static String deathLocationTeleportTo;
    private static String deathLocationTeleport;
    private static String deathLocationInvalidWorld;

    // Generic gui messages
    private static String mainMenuButton;
    private static String nextPageButton;
    private static String previousPageButton;
    private static String backButton;

    // Discord webhook messages
    private static String discordTitleBackupCreated;
    private static String discordTitleInventoryRestored;
    private static String discordTitleEnderChestRestored;
    private static String discordTitleHealthRestored;
    private static String discordTitleHungerRestored;
    private static String discordTitleExperienceRestored;
    private static String discordTitlePlayerDeath;
    private static String discordTitleForceBackup;

    private static String discordDescBackupCreated;
    private static String discordDescInventoryRestored;
    private static String discordDescEnderChestRestored;
    private static String discordDescHealthRestored;
    private static String discordDescHungerRestored;
    private static String discordDescExperienceRestored;
    private static String discordDescPlayerDeath;
    private static String discordDescForceBackup;

    private static String discordMsgBackupCreated;
    private static String discordMsgInventoryRestored;
    private static String discordMsgEnderChestRestored;
    private static String discordMsgHealthRestored;
    private static String discordMsgHungerRestored;
    private static String discordMsgExperienceRestored;
    private static String discordMsgPlayerDeath;
    private static String discordMsgForceBackup;

    private static String discordErrorWebhookFailed;
    private static String discordErrorInvalidWebhook;
    private static String discordErrorConnectionFailed;

    public void setMessages() {
        setPluginPrefix(convertColorCodes((String) getDefaultValue("general.prefix", "&f[&bInventoryRollbackPlus&f]&r ")));

        // Cmd generic
        setNoPermission(convertColorCodes((String) getDefaultValue("commands.no-permission", "&cYou do not have permission!")));
        setError(convertColorCodes((String) getDefaultValue("commands.error", "&cInvalid command.")));
        setPluginEnabled(convertColorCodes((String) getDefaultValue("commands.enable", "&2The plugin has been enabled.")));
        setPluginDisabled(convertColorCodes((String) getDefaultValue("commands.disable", "&2The plugin has been disabled.")));
        setPluginReload(convertColorCodes((String) getDefaultValue("commands.reload", "&2The plugin has been reloaded successfully.")));
        setPlayerOnlyError(convertColorCodes((String) getDefaultValue("commands.player-only", "&cCommand can only be run by a player.")));

        // Cmd import
        setImportSuccess(convertColorCodes((String) getDefaultValue("commands.import", "&aSuccessfully imported all backup data from the old plugin.")));

        // Cmd force backup
        setNoBackupError(convertColorCodes((String) getDefaultValue("backup.no-backup", "There is currently no backups for %NAME%.")));
        setNotOnlineError(convertColorCodes((String) getDefaultValue("backup.not-online", "%NAME% is not currently online.")));
        setForceBackupPlayer(convertColorCodes((String) getDefaultValue("backup.force-saved-player", "%NAME%'s inventory has been force saved.")));
        setForceBackupAll(convertColorCodes((String) getDefaultValue("backup.force-saved-all", "All online player inventories have been force saved.")));
        setForceBackupError(convertColorCodes((String) getDefaultValue("backup.not-forced-saved", "There was an issue with saving %NAME%'s inventory.")));

        // Main inv gui
        setMainInventoryRestored(convertColorCodes((String) getDefaultValue("attribute-restore.main-inventory.restored", "%NAME%''s main inventory has been restored.")));
        setMainInventoryRestoredPlayer(convertColorCodes((String) getDefaultValue("attribute-restore.main-inventory.restored-player", "Your inventory has been restored by %NAME%.")));
        setMainInventoryNotOnline(convertColorCodes((String) getDefaultValue("attribute-restore.main-inventory.not-online", "You can't restore %NAME%'s inventory while they are offline.")));
        setMainInventoryButton(convertColorCodes((String) getDefaultValue("attribute-restore.main-inventory.button-name", "&cOverwrite Main Inventory from Backup")));
        setMainInventoryDisabledButton(convertColorCodes((String) getDefaultValue("attribute-restore.main-inventory.button-disabled", "&cYou must enable this option in the configuration")));

        // Ender chest gui
        setEnderChestRestored(convertColorCodes((String) getDefaultValue("attribute-restore.ender-chest.restored", "%NAME%'s ender chest has been restored.")));
        setEnderChestRestoredPlayer(convertColorCodes((String) getDefaultValue("attribute-restore.ender-chest.restored-player", "Your ender chest has been restored by %NAME%.")));
        setEnderChestNotOnline(convertColorCodes((String) getDefaultValue("attribute-restore.ender-chest.not-online", "You can't restore %NAME%'s ender chest while they are offline.")));
        setEnderChestButton(convertColorCodes((String) getDefaultValue("attribute-restore.ender-chest.button-name", "&dRestore Ender Chest")));

        // HP gui
        setHealthRestored(convertColorCodes((String) getDefaultValue("attribute-restore.health.restored", "%NAME%'s health has been restored.")));
        setHealthRestoredPlayer(convertColorCodes((String) getDefaultValue("attribute-restore.health.restored-player", "Your health has been restored by %NAME%.")));
        setHealthNotOnline(convertColorCodes((String) getDefaultValue("attribute-restore.health.not-online", "You can't restore %NAME%'s health while they are offline.")));
        setHealthButton(convertColorCodes((String) getDefaultValue("attribute-restore.health.button-name", "&aRestore Health")));

        // Hunger gui
        setHungerRestored(convertColorCodes((String) getDefaultValue("attribute-restore.hunger.restored", "%NAME%'s hunger has been restored.")));
        setHungerRestoredPlayer(convertColorCodes((String) getDefaultValue("attribute-restore.hunger.restored-player", "Your hunger has been restored by %NAME%.")));
        setHungerNotOnline(convertColorCodes((String) getDefaultValue("attribute-restore.hunger.not-online", "You can't restore %NAME%'s hunger while they are offline.")));
        setHungerButton(convertColorCodes((String) getDefaultValue("attribute-restore.hunger.button-name", "&cRestore Food")));

        // XP gui
        setExperienceRestored(convertColorCodes((String) getDefaultValue("attribute-restore.experience.restored", "%NAME%'s XP has been set to level %XP%.")));
        setExperienceRestoredPlayer(convertColorCodes((String) getDefaultValue("attribute-restore.experience.restored-player", "Your XP has been restored to level %XP% by %NAME%.")));
        setExperienceNotOnlinePlayer(convertColorCodes((String) getDefaultValue("attribute-restore.experience.not-online", "You can't restore %NAME%'s experience while they are offline.")));
        setExperienceButton(convertColorCodes((String) getDefaultValue("attribute-restore.experience.button-name", "&2Restore Player XP")));
        setExperienceButtonLore(convertColorCodes((String) getDefaultValue("attribute-restore.experience.button-lore", "&rLevel %XP%")));

        // Death only gui
        setDeathLocationWorld(convertColorCodes((String) getDefaultValue("death-location.world", "&6World: &f%WORLD%")));
        setDeathLocationX(convertColorCodes((String) getDefaultValue("death-location.x", "&6X: &f%X%")));
        setDeathLocationY(convertColorCodes((String) getDefaultValue("death-location.y", "&6Y: &f%Y%")));
        setDeathLocationZ(convertColorCodes((String) getDefaultValue("death-location.z", "&6Z: &f%Z%")));
        setDeathReason(convertColorCodes((String) getDefaultValue("death-location.reason", "&6Death reason: &f%REASON%")));
        setDeathTime(convertColorCodes((String) getDefaultValue("death-location.time", "&6Time: &f%TIME%")));
        setDeathLocation(convertColorCodes((String) getDefaultValue("death-location.teleport-to", "&3Teleport to where this entry was logged.")));
        setDeathLocationTeleport(convertColorCodes((String) getDefaultValue("death-location.teleport", "You have been teleported to %LOCATION%")));
        setDeathLocationInvalidWorldError(convertColorCodes((String) getDefaultValue("death-location.invalid-world", "The world %WORLD% is not currently loaded on the server.")));

        // Generic gui buttons
        setMainMenuButton(convertColorCodes((String) getDefaultValue("menu-buttons.main-menu", "&fMain Menu")));
        setNextPageButton(convertColorCodes((String) getDefaultValue("menu-buttons.next-page", "&fNext Page")));
        setPreviousPageButton(convertColorCodes((String) getDefaultValue("menu-buttons.previous-page", "&fPrevious Page")));
        setBackButton(convertColorCodes((String) getDefaultValue("menu-buttons.back-page", "&fBack")));

        // Discord webhook messages
        setDiscordTitleBackupCreated((String) getDefaultValue("discord.titles.backup-created", "📦 Backup Created"));
        setDiscordTitleInventoryRestored((String) getDefaultValue("discord.titles.inventory-restored", "🎒 Inventory Restored"));
        setDiscordTitleEnderChestRestored((String) getDefaultValue("discord.titles.ender-chest-restored", "📦 Ender Chest Restored"));
        setDiscordTitleHealthRestored((String) getDefaultValue("discord.titles.health-restored", "❤️ Health Restored"));
        setDiscordTitleHungerRestored((String) getDefaultValue("discord.titles.hunger-restored", "🍖 Hunger Restored"));
        setDiscordTitleExperienceRestored((String) getDefaultValue("discord.titles.experience-restored", "✨ Experience Restored"));
        setDiscordTitlePlayerDeath((String) getDefaultValue("discord.titles.player-death", "💀 Player Death"));
        setDiscordTitleForceBackup((String) getDefaultValue("discord.titles.force-backup", "🔧 Force Backup"));

        setDiscordDescBackupCreated((String) getDefaultValue("discord.descriptions.backup-created", "Player **%PLAYER%** backup created\n**Type:** %TYPE%\n**Time:** %TIME%"));
        setDiscordDescInventoryRestored((String) getDefaultValue("discord.descriptions.inventory-restored", "Player **%PLAYER%** inventory restored by **%ADMIN%**\n**From backup:** %TIME%"));
        setDiscordDescEnderChestRestored((String) getDefaultValue("discord.descriptions.ender-chest-restored", "Player **%PLAYER%** ender chest restored by **%ADMIN%**\n**From backup:** %TIME%"));
        setDiscordDescHealthRestored((String) getDefaultValue("discord.descriptions.health-restored", "Player **%PLAYER%** health restored by **%ADMIN%**\n**Health:** %HEALTH%\n**From backup:** %TIME%"));
        setDiscordDescHungerRestored((String) getDefaultValue("discord.descriptions.hunger-restored", "Player **%PLAYER%** hunger restored by **%ADMIN%**\n**Hunger:** %HUNGER%\n**From backup:** %TIME%"));
        setDiscordDescExperienceRestored((String) getDefaultValue("discord.descriptions.experience-restored", "Player **%PLAYER%** experience restored by **%ADMIN%**\n**Level:** %LEVEL%\n**From backup:** %TIME%"));
        setDiscordDescPlayerDeath((String) getDefaultValue("discord.descriptions.player-death", "Player **%PLAYER%** died\n**Location:** %WORLD% (%X%, %Y%, %Z%)\n**Cause:** %CAUSE%\n**Time:** %TIME%"));
        setDiscordDescForceBackup((String) getDefaultValue("discord.descriptions.force-backup", "Force backup created for **%PLAYER%** by **%ADMIN%**\n**Time:** %TIME%"));

        setDiscordMsgBackupCreated((String) getDefaultValue("discord.messages.backup-created", "📦 Backup created for %PLAYER% (%TYPE%) at %TIME%"));
        setDiscordMsgInventoryRestored((String) getDefaultValue("discord.messages.inventory-restored", "🎒 %PLAYER% inventory restored by %ADMIN% from backup %TIME%"));
        setDiscordMsgEnderChestRestored((String) getDefaultValue("discord.messages.ender-chest-restored", "📦 %PLAYER% ender chest restored by %ADMIN% from backup %TIME%"));
        setDiscordMsgHealthRestored((String) getDefaultValue("discord.messages.health-restored", "❤️ %PLAYER% health restored by %ADMIN% (Health: %HEALTH%) from backup %TIME%"));
        setDiscordMsgHungerRestored((String) getDefaultValue("discord.messages.hunger-restored", "🍖 %PLAYER% hunger restored by %ADMIN% (Hunger: %HUNGER%) from backup %TIME%"));
        setDiscordMsgExperienceRestored((String) getDefaultValue("discord.messages.experience-restored", "✨ %PLAYER% experience restored by %ADMIN% (Level: %LEVEL%) from backup %TIME%"));
        setDiscordMsgPlayerDeath((String) getDefaultValue("discord.messages.player-death", "💀 %PLAYER% died at %WORLD% (%X%, %Y%, %Z%) - %CAUSE% at %TIME%"));
        setDiscordMsgForceBackup((String) getDefaultValue("discord.messages.force-backup", "🔧 Force backup created for %PLAYER% by %ADMIN% at %TIME%"));

        setDiscordErrorWebhookFailed(convertColorCodes((String) getDefaultValue("discord.errors.webhook-failed", "&cFailed to send Discord webhook message")));
        setDiscordErrorInvalidWebhook(convertColorCodes((String) getDefaultValue("discord.errors.invalid-webhook", "&cInvalid Discord webhook URL configured")));
        setDiscordErrorConnectionFailed(convertColorCodes((String) getDefaultValue("discord.errors.connection-failed", "&cFailed to connect to Discord webhook")));

        if (saveChanges())
            saveConfig();
    }

    private static String nameVariable = "%NAME%";
    private static String xpVariable = "%XP%";

    public static void setPluginPrefix(String message) {
        pluginPrefix = message;
    }

    public static void setNoPermission(String message) {
        noPermission = message;
    }

    public static void setError(String message) {
        error = message;
    }

    public static void setErrorInventory(String message) {
        errorInventory = message;
    }

    public static void setPluginEnabled(String message) {
        enabledMessage = message;
    }

    public static void setPluginDisabled(String message) {
        disabledMessage = message;
    }

    public static void setPluginReload(String message) {
        reloadMessage = message;
    }

    public static void setPlayerOnlyError(String message) {
        playerOnly = message;
    }

    public static void setImportSuccess(String message) {
        importSuccess = message;
    }

    public static void setNoBackupError(String message) {
        noBackup = message;
    }

    public static void setNotOnlineError(String message) {
        notOnline = message;
    }

    public static void setForceBackupPlayer(String message) {
        forceSavedPlayer = message;
    }

    public static void setForceBackupAll(String message) {
        forceSavedAll = message;
    }

    public static void setForceBackupError(String message) {
        notForcedSaved = message;
    }

    public static void setMainInventoryRestored(String message) {
        mainInventoryRestored = message;
    }

    public static void setMainInventoryRestoredPlayer(String message) {
        mainInventoryRestoredPlayer = message;
    }

    public static void setMainInventoryNotOnline(String message) {
        mainInventoryNotOnline = message;
    }
    
    public static void setMainInventoryButton(String message) {
        mainInventoryButton = message;
    }

    public static void setMainInventoryDisabledButton(String message) {
        mainInventoryDisabledButton = message;
    }

    public static void setEnderChestRestored(String message) {
        enderChestRestored = message;
    }

    public static void setEnderChestRestoredPlayer(String message) {
        enderChestRestoredPlayer = message;
    }

    public static void setEnderChestNotOnline(String message) {
        enderChestNotOnline = message;
    }
    
    public static void setEnderChestButton(String message) {
        enderChestButton = message;
    }

    public static void setHealthRestored(String message) {
        healthRestored = message;
    }

    public static void setHealthRestoredPlayer(String message) {
        healthRestoredPlayer = message;
    }

    public static void setHealthNotOnline(String message) {
        healthNotOnline = message;
    }
    
    public static void setHealthButton(String message) {
        healthButton = message;
    }

    public static void setHungerRestored(String message) {
        hungerRestored = message;
    }

    public static void setHungerRestoredPlayer(String message) {
        hungerRestoredPlayer = message;
    }

    public static void setHungerNotOnline(String message) {
        hungerNotOnline = message;
    }
    
    public static void setHungerButton(String message) {
        hungerButton = message;
    }

    public static void setExperienceRestored(String message) {
        experienceRestored = message;
    }

    public static void setExperienceRestoredPlayer(String message) {
        experienceRestoredPlayer = message;
    }

    public static void setExperienceNotOnlinePlayer(String message) {
        experienceNotOnline = message;
    }
    
    public static void setExperienceButton(String message) {
        experienceButton = message;
    }

    public static void setExperienceButtonLore(String message) {
        experienceButtonLore = message;
    }

    public static void setDeathLocationWorld(String message) {
        deathLocationWorld = message;
    }

    public static void setDeathLocationX(String message) {
        deathLocationX = message;
    }

    public static void setDeathLocationY(String message) {
        deathLocationY = message;
    }

    public static void setDeathLocationZ(String message) {
        deathLocationZ = message;
    }
    
    public static void setDeathReason(String message) {
        deathReason = message;
    }

    public static void setDeathTime(String message) {
        deathTime = message;
    }
    
    public static void setDeathLocation(String message) {
        deathLocationTeleportTo = message;
    }


    public static void setDeathLocationTeleport(String message) {
        deathLocationTeleport = message;
    }

    public static void setDeathLocationInvalidWorldError(String message) {
        deathLocationInvalidWorld = message;
    }

    public static void setMainMenuButton(String message) {
        mainMenuButton = message;
    }

    public static void setNextPageButton(String message) {
        nextPageButton = message;
    }

    public static void setPreviousPageButton(String message) {
        previousPageButton = message;
    }

    public static void setBackButton(String message) {
        backButton = message;
    }

    public static void setDiscordTitleBackupCreated(String message) {
        discordTitleBackupCreated = message;
    }

    public static void setDiscordTitleInventoryRestored(String message) {
        discordTitleInventoryRestored = message;
    }

    public static void setDiscordTitleEnderChestRestored(String message) {
        discordTitleEnderChestRestored = message;
    }

    public static void setDiscordTitleHealthRestored(String message) {
        discordTitleHealthRestored = message;
    }

    public static void setDiscordTitleHungerRestored(String message) {
        discordTitleHungerRestored = message;
    }

    public static void setDiscordTitleExperienceRestored(String message) {
        discordTitleExperienceRestored = message;
    }

    public static void setDiscordTitlePlayerDeath(String message) {
        discordTitlePlayerDeath = message;
    }

    public static void setDiscordTitleForceBackup(String message) {
        discordTitleForceBackup = message;
    }

    public static void setDiscordDescBackupCreated(String message) {
        discordDescBackupCreated = message;
    }

    public static void setDiscordDescInventoryRestored(String message) {
        discordDescInventoryRestored = message;
    }

    public static void setDiscordDescEnderChestRestored(String message) {
        discordDescEnderChestRestored = message;
    }

    public static void setDiscordDescHealthRestored(String message) {
        discordDescHealthRestored = message;
    }

    public static void setDiscordDescHungerRestored(String message) {
        discordDescHungerRestored = message;
    }

    public static void setDiscordDescExperienceRestored(String message) {
        discordDescExperienceRestored = message;
    }

    public static void setDiscordDescPlayerDeath(String message) {
        discordDescPlayerDeath = message;
    }

    public static void setDiscordDescForceBackup(String message) {
        discordDescForceBackup = message;
    }

    public static void setDiscordMsgBackupCreated(String message) {
        discordMsgBackupCreated = message;
    }

    public static void setDiscordMsgInventoryRestored(String message) {
        discordMsgInventoryRestored = message;
    }

    public static void setDiscordMsgEnderChestRestored(String message) {
        discordMsgEnderChestRestored = message;
    }

    public static void setDiscordMsgHealthRestored(String message) {
        discordMsgHealthRestored = message;
    }

    public static void setDiscordMsgHungerRestored(String message) {
        discordMsgHungerRestored = message;
    }

    public static void setDiscordMsgExperienceRestored(String message) {
        discordMsgExperienceRestored = message;
    }

    public static void setDiscordMsgPlayerDeath(String message) {
        discordMsgPlayerDeath = message;
    }

    public static void setDiscordMsgForceBackup(String message) {
        discordMsgForceBackup = message;
    }

    public static void setDiscordErrorWebhookFailed(String message) {
        discordErrorWebhookFailed = message;
    }

    public static void setDiscordErrorInvalidWebhook(String message) {
        discordErrorInvalidWebhook = message;
    }

    public static void setDiscordErrorConnectionFailed(String message) {
        discordErrorConnectionFailed = message;
    }

    // GETTERS

    public static String getAdminAlerts() {
        return adminAlerts;
    }

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

    public static String getNoPermission() {
        return noPermission;
    }

    public static String getError() {
        return error;
    }

    public static String getErrorInventory() {
        return errorInventory;
    }

    public static String getPluginEnabled() {
        return enabledMessage;
    }

    public static String getPluginDisabled() {
        return disabledMessage;
    }

    public static String getPluginReload() {
        return reloadMessage;
    }

    public static String getPlayerOnlyError() {
        return playerOnly;
    }

    public static String getImportSuccess() {
        return importSuccess;
    }

    public static String getNoBackupError(String name) {
        return noBackup.replaceAll(nameVariable, name);
    }

    public static String getNotOnlineError(String name) {
        return notOnline.replaceAll(nameVariable, name);
    }

    public static String getForceBackupPlayer(String name) {
        return forceSavedPlayer.replaceAll(nameVariable, name);
    }

    public static String getForceBackupAll() {
        return forceSavedAll;
    }

    public static String getForceBackupError(String name) {
        return notForcedSaved.replaceAll(nameVariable, name);
    }

    public static String getMainInventoryRestored(String name) {
        return mainInventoryRestored.replaceAll(nameVariable, name);
    }

    public static String getMainInventoryRestoredPlayer(String name) {
        return mainInventoryRestoredPlayer.replaceAll(nameVariable, name);
    }

    public static String getMainInventoryNotOnline(String name) {
        return mainInventoryNotOnline.replaceAll(nameVariable, name);
    }

    public static String getMainInventoryRestoreButton() {
        return mainInventoryButton;
    }

    public static String getMainInventoryDisabledButton() {
        return mainInventoryDisabledButton;
    }

    public static String getEnderChestRestored(String name) {
        return enderChestRestored.replaceAll(nameVariable, name);
    }

    public static String getEnderChestRestoredPlayer(String name) {
        return enderChestRestoredPlayer.replaceAll(nameVariable, name);
    }

    public static String getEnderChestNotOnline(String name) {
        return enderChestNotOnline.replaceAll(nameVariable, name);
    }

    public static String getEnderChestRestoreButton() {
        return enderChestButton;
    }

    public static String getHealthRestored(String name) {
        return healthRestored.replaceAll(nameVariable, name);
    }

    public static String getHealthRestoredPlayer(String name) {
        return healthRestoredPlayer.replaceAll(nameVariable, name);
    }

    public static String getHealthNotOnline(String name) {
        return healthNotOnline.replaceAll(nameVariable, name);
    }

    public static String getHealthRestoreButton() {
        return healthButton;
    }

    public static String getHungerRestored(String name) {
        return hungerRestored.replaceAll(nameVariable, name);
    }

    public static String getHungerRestoredPlayer(String name) {
        return hungerRestoredPlayer.replaceAll(nameVariable, name);
    }

    public static String getHungerNotOnline(String name) {
        return hungerNotOnline.replaceAll(nameVariable, name);
    }

    public static String getHungerRestoreButton() {
        return hungerButton;
    }

    public static String getExperienceRestored(String name, int xp) {
        return experienceRestored.replaceAll(nameVariable, name).replaceAll(xpVariable, xp + "");
    }

    public static String getExperienceRestoredPlayer(String name, int xp) {
        return experienceRestoredPlayer.replaceAll(nameVariable, name).replaceAll(xpVariable, xp + "");
    }

    public static String getExperienceNotOnlinePlayer(String name) {
        return experienceNotOnline.replaceAll(nameVariable, name);
    }

    public static String getExperienceRestoreButton() {
        return experienceButton;
    }

    public static String getExperienceRestoreLevel(int xp) {
        return experienceButtonLore.replaceAll(xpVariable, xp + "");
    }

    public static String getDeathLocationWorld(String world) {
        return deathLocationWorld.replace("%WORLD%", world);
    }

    public static String getDeathLocationX(Double x) {
        return deathLocationX.replace("%X%", Math.floor(x) + "");
    }

    public static String getDeathLocationY(Double y) {
        return deathLocationY.replace("%Y%", Math.floor(y) + "");
    }

    public static String getDeathLocationZ(Double z) {
        return deathLocationZ.replace("%Z%", Math.floor(z) + "");
    }

    public static String getDeathReason(String reason) {
        return deathReason.replace("%REASON%", reason);
    }

    public static String getDeathTime(String time) {
        return deathTime.replace("%TIME%", time);
    }

    public static String getDeathLocation() {
        return deathLocationTeleportTo;
    }

    public static String getDeathLocationTeleport(Location location) {
        return deathLocationTeleport.replace("%LOCATION%", "X:" + (int) location.getX() + " Y:" + (int) location.getY() + " Z:" + (int) location.getZ());
    }

    public static String getDeathLocationInvalidWorldError(String world) {
        return deathLocationInvalidWorld.replace("%WORLD%", world);
    }

    public static String getMainMenuButton() {
        return mainMenuButton;
    }

    public static String getNextPageButton() {
        return nextPageButton;
    }

    public static String getPreviousPageButton() {
        return previousPageButton;
    }

    public static String getBackButton() {
        return backButton;
    }

    // Discord webhook message getters
    public static String getDiscordTitleBackupCreated() {
        return discordTitleBackupCreated;
    }

    public static String getDiscordTitleInventoryRestored() {
        return discordTitleInventoryRestored;
    }

    public static String getDiscordTitleEnderChestRestored() {
        return discordTitleEnderChestRestored;
    }

    public static String getDiscordTitleHealthRestored() {
        return discordTitleHealthRestored;
    }

    public static String getDiscordTitleHungerRestored() {
        return discordTitleHungerRestored;
    }

    public static String getDiscordTitleExperienceRestored() {
        return discordTitleExperienceRestored;
    }

    public static String getDiscordTitlePlayerDeath() {
        return discordTitlePlayerDeath;
    }

    public static String getDiscordTitleForceBackup() {
        return discordTitleForceBackup;
    }

    public static String getDiscordDescBackupCreated() {
        return discordDescBackupCreated;
    }

    public static String getDiscordDescInventoryRestored() {
        return discordDescInventoryRestored;
    }

    public static String getDiscordDescEnderChestRestored() {
        return discordDescEnderChestRestored;
    }

    public static String getDiscordDescHealthRestored() {
        return discordDescHealthRestored;
    }

    public static String getDiscordDescHungerRestored() {
        return discordDescHungerRestored;
    }

    public static String getDiscordDescExperienceRestored() {
        return discordDescExperienceRestored;
    }

    public static String getDiscordDescPlayerDeath() {
        return discordDescPlayerDeath;
    }

    public static String getDiscordDescForceBackup() {
        return discordDescForceBackup;
    }

    public static String getDiscordMsgBackupCreated() {
        return discordMsgBackupCreated;
    }

    public static String getDiscordMsgInventoryRestored() {
        return discordMsgInventoryRestored;
    }

    public static String getDiscordMsgEnderChestRestored() {
        return discordMsgEnderChestRestored;
    }

    public static String getDiscordMsgHealthRestored() {
        return discordMsgHealthRestored;
    }

    public static String getDiscordMsgHungerRestored() {
        return discordMsgHungerRestored;
    }

    public static String getDiscordMsgExperienceRestored() {
        return discordMsgExperienceRestored;
    }

    public static String getDiscordMsgPlayerDeath() {
        return discordMsgPlayerDeath;
    }

    public static String getDiscordMsgForceBackup() {
        return discordMsgForceBackup;
    }

    public static String getDiscordErrorWebhookFailed() {
        return discordErrorWebhookFailed;
    }

    public static String getDiscordErrorInvalidWebhook() {
        return discordErrorInvalidWebhook;
    }

    public static String getDiscordErrorConnectionFailed() {
        return discordErrorConnectionFailed;
    }

    private static String convertColorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public Object getDefaultValue(String path, Object defaultValue) {
        Object obj = messages.get(path);

        if (obj == null) {
            obj = defaultValue;

            messages.set(path, defaultValue);
            saveChanges = true;
        }

        // Process newline characters for Discord messages
        if (obj instanceof String && path.startsWith("discord.")) {
            String str = (String) obj;
            // Convert literal \n sequences to actual newlines
            str = str.replace("\\n", "\n");
            obj = str;
        }

        return obj;
    }

    private boolean saveChanges() {
        return saveChanges;
    }
}
