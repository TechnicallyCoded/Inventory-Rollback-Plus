package me.danjono.inventoryrollback.config;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import me.danjono.inventoryrollback.InventoryRollback;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Level;

public class ConfigData {

    private File configurationFile;
    private FileConfiguration configuration;
    private static final String configurationFileName = "config.yml";

    public ConfigData() {
        generateConfigFile();
    }

    public void generateConfigFile() {
        getConfigurationFile();
        if(!configurationFile.exists()) {
            InventoryRollback.getInstance().saveResource(configurationFileName, false);
            getConfigurationFile();
        }
        getConfigData();
    }

    private void getConfigurationFile() {
        configurationFile = new File(InventoryRollback.getInstance().getDataFolder(), configurationFileName);
    }

    private void getConfigData() {
        configuration = YamlConfiguration.loadConfiguration(configurationFile);
    }

    public boolean saveConfig() {
        try {
            configuration.save(configurationFile);
        } catch (IOException e) {
            InventoryRollback.getInstance().getLogger().log(Level.SEVERE, "Could not save data to config file", e);
            return false;
        }

        saveChanges = false;

        return true;
    }

    public enum SaveType {
        YAML("YAML"),
        MYSQL("MySQL");
        
        private final String name;

        SaveType(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }

    private static boolean pluginEnabled;

    private static SaveType saveType = SaveType.YAML;
    private static File folderLocation = InventoryRollback.getInstance().getDataFolder();   

    private static boolean mysqlEnabled;
    private static String mysqlHost;
    private static int mysqlPort;
    private static String mysqlDatabase;
    private static String mysqlTablePrefix;
    private static String mysqlUsername;
    private static String mysqlPassword;
    private static boolean mysqlUseSSL;
    private static boolean mysqlVerifyCertificate;
    private static boolean mysqlPubKeyRetrieval;

    private static boolean allowOtherPluginEditDeathInventory;
    private static boolean restoreToPlayerButton;
    private static int backupLinesVisible;

    private static int maxSavesJoin;
    private static int maxSavesQuit;
    private static int maxSavesDeath;
    private static int maxSavesWorldChange;
    private static int maxSavesForce;

    private static long timeZoneOffsetMillis;
    private static TimeZone timeZone;
    private static String timeZoneName;
    private static SimpleDateFormat timeFormat;

    private static boolean updateChecker;
    private static boolean bStatsEnabled;
    private static boolean debugEnabled;

    // Discord webhook configuration
    private static boolean discordEnabled;
    private static String discordWebhookUrl;
    private static boolean discordBackupCreated;
    private static boolean discordInventoryRestored;
    private static boolean discordEnderChestRestored;
    private static boolean discordHealthRestored;
    private static boolean discordHungerRestored;
    private static boolean discordExperienceRestored;
    private static boolean discordPlayerDeath;
    private static boolean discordForceBackup;
    private static boolean discordIncludeServerName;
    private static String discordServerName;
    private static boolean discordUseEmbeds;
    private static String discordColorBackup;
    private static String discordColorRestore;
    private static String discordColorDeath;
    private static String discordColorWarning;

    public void setVariables() {
        setEnabled((boolean) getDefaultValue("enabled", true));

        String folder = (String) getDefaultValue("folder-location", "DEFAULT");
        if (folder.equalsIgnoreCase("DEFAULT") || folder.isEmpty()) {
            setFolderLocation(InventoryRollback.getInstance().getDataFolder());
        } else {
            try {
                setFolderLocation(new File(folder));
            } catch (NullPointerException e) {
                InventoryRollback.getInstance().getLogger().log(Level.WARNING, "Could not save set data folder to \"" + folder + "\". Setting to default location in plugin folder.", e);
                setFolderLocation(InventoryRollback.getInstance().getDataFolder());
            }
        }

        setMySQLEnabled((boolean) getDefaultValue("mysql.enabled", false));
        if (isMySQLEnabled())
            setSaveType(SaveType.MYSQL);
        else
            setSaveType(SaveType.YAML);

        setMySQLHost((String) getDefaultValue("mysql.details.host", "127.0.0.1"));
        setMySQLPort((int) getDefaultValue("mysql.details.port", 3306));
        setMySQLDatabase((String) getDefaultValue("mysql.details.database", "inventory_rollback"));
        setMySQLTablePrefix((String) getDefaultValue("mysql.details.table-prefix", "backup_"));
        setMySQLUsername((String) getDefaultValue("mysql.details.username", "username"));
        setMySQLPassword((String) getDefaultValue("mysql.details.password", "password"));
        setMySQLUseSSL((boolean) getDefaultValue("mysql.details.use-SSL", true));
        setMySQLVerifyCertificate((boolean) getDefaultValue("mysql.details.verifyCertificate", true));
        setMysqlPubKeyRetrievalAllowed((boolean) getDefaultValue("mysql.details.allowPubKeyRetrieval", false));

        setAllowOtherPluginEditDeathInventory((boolean) getDefaultValue("allow-other-plugins-edit-death-inventory", false));
        setRestoreToPlayerButton((boolean) getDefaultValue("restore-to-player-button", true));
        setBackupLinesVisible((int) getDefaultValue("backup-lines-visible", 1));

        setMaxSavesJoin((int) getDefaultValue("max-saves.join", 10));
        setMaxSavesQuit((int) getDefaultValue("max-saves.quit", 10));	
        setMaxSavesDeath((int) getDefaultValue("max-saves.death", 50));
        setMaxSavesWorldChange((int) getDefaultValue("max-saves.world-change", 10));	
        setMaxSavesForce((int) getDefaultValue("max-saves.force", 10));

        setTimeZone((String) getDefaultValue("time-zone", "GMT"));
        setTimeFormat((String) getDefaultValue("time-format", "dd/MM/yyyy HH:mm:ss a"));

        setUpdateChecker((boolean) getDefaultValue("update-checker", true));
        setbStatsEnabled((boolean) getDefaultValue("bStats", true));
        setDebugEnabled((boolean) getDefaultValue("debug", false));

        // Discord webhook settings
        setDiscordEnabled((boolean) getDefaultValue("discord.enabled", false));
        setDiscordWebhookUrl((String) getDefaultValue("discord.webhook-url", ""));
        setDiscordBackupCreated((boolean) getDefaultValue("discord.events.backup-created", true));
        setDiscordInventoryRestored((boolean) getDefaultValue("discord.events.inventory-restored", true));
        setDiscordEnderChestRestored((boolean) getDefaultValue("discord.events.ender-chest-restored", true));
        setDiscordHealthRestored((boolean) getDefaultValue("discord.events.health-restored", true));
        setDiscordHungerRestored((boolean) getDefaultValue("discord.events.hunger-restored", true));
        setDiscordExperienceRestored((boolean) getDefaultValue("discord.events.experience-restored", true));
        setDiscordPlayerDeath((boolean) getDefaultValue("discord.events.player-death", true));
        setDiscordForceBackup((boolean) getDefaultValue("discord.events.force-backup", true));
        setDiscordIncludeServerName((boolean) getDefaultValue("discord.settings.include-server-name", true));
        setDiscordServerName((String) getDefaultValue("discord.settings.server-name", "My Server"));
        setDiscordUseEmbeds((boolean) getDefaultValue("discord.settings.use-embeds", true));
        setDiscordColorBackup((String) getDefaultValue("discord.settings.colors.backup", "#00ff00"));
        setDiscordColorRestore((String) getDefaultValue("discord.settings.colors.restore", "#0099ff"));
        setDiscordColorDeath((String) getDefaultValue("discord.settings.colors.death", "#ff3300"));
        setDiscordColorWarning((String) getDefaultValue("discord.settings.colors.warning", "#ffcc00"));

        if (saveChanges())
            saveConfig();
    }

    public static void setEnabled(boolean enabled) {        
        pluginEnabled = enabled;
    }

    public static void setSaveType(SaveType value) {
        saveType = value;
    }

    public static void setFolderLocation(File folder) {
        folderLocation = folder;
    }

    public static void setMySQLEnabled(boolean value) {
        mysqlEnabled = value;
    }

    public static void setMySQLHost(String value) {
        mysqlHost = value;
    }

    public static void setMySQLPort(int value) {
        mysqlPort = value;
    }

    public static void setMySQLDatabase(String value) {
        mysqlDatabase = value;
    }

    public static void setMySQLTablePrefix(String value) {
        mysqlTablePrefix = value;
    }

    public static void setMySQLUsername(String value) {
        mysqlUsername = value;
    }

    public static void setMySQLPassword(String value) {
        mysqlPassword = value;
    }

    public static void setMySQLUseSSL(boolean value) {
        mysqlUseSSL = value;
    }

    public static void setMySQLVerifyCertificate(boolean value) {
        mysqlVerifyCertificate = value;
    }

    public static void setMysqlPubKeyRetrievalAllowed(boolean value) {
        mysqlPubKeyRetrieval = value;
    }

    public static void setRestoreToPlayerButton(boolean value) {
        restoreToPlayerButton = value;
    }

    public static void setAllowOtherPluginEditDeathInventory(boolean value) {
        allowOtherPluginEditDeathInventory = value;
    }

    public static void setBackupLinesVisible(int value) {
        if (value <= 0) {
            backupLinesVisible = 1;
        } else if (value > 5) {
            backupLinesVisible = 5;
        } else {
            backupLinesVisible = value;
        }
    }

    public static void setMaxSavesJoin(int value) {
        maxSavesJoin = value;
    }

    public static void setMaxSavesQuit(int value) {
        maxSavesQuit = value;
    }

    public static void setMaxSavesDeath(int value) {
        maxSavesDeath = value;
    }

    public static void setMaxSavesWorldChange(int value) {
        maxSavesWorldChange = value;
    }

    public static void setMaxSavesForce(int value) {
        maxSavesForce = value;
    }

    public static void setTimeZone(String zone) {
        try {
            // Allow UTC offsets
            if (zone.length() > 3 && zone.startsWith("UTC")) {
                zone = "GMT" + zone.substring(3);
            }

            timeZone = TimeZone.getTimeZone(zone);
            timeZoneName = zone;
            timeZoneOffsetMillis = InventoryRollbackPlus.getInstance().getTimeZoneUtil().getMillisOffsetAtTimeZone(zone);
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            timeZoneOffsetMillis = 0L;
            InventoryRollback.getInstance().getLogger().log(Level.WARNING, ("Time zone \"" + zone + "\" in config.yml is invalid. Defaulting to \"UTC\""));
        }
    }

    public static void setTimeFormat(String format) {
        try {
            timeFormat = new SimpleDateFormat(format);
        } catch (IllegalArgumentException e) {
            timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a z");
            InventoryRollback.getInstance().getLogger().log(Level.WARNING, ("Time zone format \"" + format + "\" in config.yml is not valid. Defaulting to \"dd/MM/yyyy hh:mm:ss a z\""));
        }
    }

    public static void setUpdateChecker(boolean enabled) {
        updateChecker = enabled;
    }

    public static void setbStatsEnabled(boolean enabled) {
        bStatsEnabled = enabled;
    }

    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void setDiscordEnabled(boolean enabled) {
        discordEnabled = enabled;
    }

    public static void setDiscordWebhookUrl(String url) {
        discordWebhookUrl = url;
    }

    public static void setDiscordBackupCreated(boolean enabled) {
        discordBackupCreated = enabled;
    }

    public static void setDiscordInventoryRestored(boolean enabled) {
        discordInventoryRestored = enabled;
    }

    public static void setDiscordEnderChestRestored(boolean enabled) {
        discordEnderChestRestored = enabled;
    }

    public static void setDiscordHealthRestored(boolean enabled) {
        discordHealthRestored = enabled;
    }

    public static void setDiscordHungerRestored(boolean enabled) {
        discordHungerRestored = enabled;
    }

    public static void setDiscordExperienceRestored(boolean enabled) {
        discordExperienceRestored = enabled;
    }

    public static void setDiscordPlayerDeath(boolean enabled) {
        discordPlayerDeath = enabled;
    }

    public static void setDiscordForceBackup(boolean enabled) {
        discordForceBackup = enabled;
    }

    public static void setDiscordIncludeServerName(boolean enabled) {
        discordIncludeServerName = enabled;
    }

    public static void setDiscordServerName(String name) {
        discordServerName = name;
    }

    public static void setDiscordUseEmbeds(boolean enabled) {
        discordUseEmbeds = enabled;
    }

    public static void setDiscordColorBackup(String color) {
        discordColorBackup = color;
    }

    public static void setDiscordColorRestore(String color) {
        discordColorRestore = color;
    }

    public static void setDiscordColorDeath(String color) {
        discordColorDeath = color;
    }

    public static void setDiscordColorWarning(String color) {
        discordColorWarning = color;
    }

    public static boolean isEnabled() {
        return pluginEnabled;
    }

    public static SaveType getSaveType() {
        return saveType;
    }

    public static File getFolderLocation() {
        return folderLocation;
    }

    public static boolean isMySQLEnabled() {
        return mysqlEnabled;
    }

    public static String getMySQLHost() {
        return mysqlHost;
    }

    public static int getMySQLPort() {
        return mysqlPort;
    }

    public static String getMySQLDatabase() {
        return mysqlDatabase;
    }

    public static String getMySQLTablePrefix() {
        return mysqlTablePrefix;
    }

    public static String getMySQLUsername() {
        return mysqlUsername;
    }

    public static String getMySQLPassword() {
        return mysqlPassword;
    }

    public static boolean isMySQLUseSSL() {
        return mysqlUseSSL;
    }

    public static boolean isMySQLVerifyCertificate() {
        return mysqlVerifyCertificate;
    }

    public static boolean isMySQLPubKeyRetrievalAllowed() {
        return mysqlPubKeyRetrieval;
    }

    public static boolean isRestoreToPlayerButton() {
        return restoreToPlayerButton;
    }

    public static boolean isAllowOtherPluginEditDeathInventory() {
        return allowOtherPluginEditDeathInventory;
    }

    public static int getBackupLinesVisible() {
        return backupLinesVisible;
    }

    public static int getMaxSavesJoin() {
        return maxSavesJoin;
    }

    public static int getMaxSavesQuit() {
        return maxSavesQuit;
    }

    public static int getMaxSavesDeath() {
        return maxSavesDeath;
    }

    public static int getMaxSavesWorldChange() {
        return maxSavesWorldChange;
    }

    public static int getMaxSavesForce() {
        return maxSavesForce;
    }

    public static long getTimeZoneOffsetMillis() {
        return timeZoneOffsetMillis;
    }

    public static TimeZone getTimeZone() {
        return timeZone;
    }

    public static SimpleDateFormat getTimeFormat() {
        return timeFormat;
    }

    public static boolean isUpdateCheckerEnabled() {
        return updateChecker;
    }

    public static boolean isbStatsEnabled() {
        return bStatsEnabled;
    }

    public static boolean isDebugEnabled() {
        return debugEnabled;
    }

    public static boolean isDiscordEnabled() {
        return discordEnabled;
    }

    public static String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public static boolean isDiscordBackupCreated() {
        return discordBackupCreated;
    }

    public static boolean isDiscordInventoryRestored() {
        return discordInventoryRestored;
    }

    public static boolean isDiscordEnderChestRestored() {
        return discordEnderChestRestored;
    }

    public static boolean isDiscordHealthRestored() {
        return discordHealthRestored;
    }

    public static boolean isDiscordHungerRestored() {
        return discordHungerRestored;
    }

    public static boolean isDiscordExperienceRestored() {
        return discordExperienceRestored;
    }

    public static boolean isDiscordPlayerDeath() {
        return discordPlayerDeath;
    }

    public static boolean isDiscordForceBackup() {
        return discordForceBackup;
    }

    public static boolean isDiscordIncludeServerName() {
        return discordIncludeServerName;
    }

    public static String getDiscordServerName() {
        return discordServerName;
    }

    public static boolean isDiscordUseEmbeds() {
        return discordUseEmbeds;
    }

    public static String getDiscordColorBackup() {
        return discordColorBackup;
    }

    public static String getDiscordColorRestore() {
        return discordColorRestore;
    }

    public static String getDiscordColorDeath() {
        return discordColorDeath;
    }

    public static String getDiscordColorWarning() {
        return discordColorWarning;
    }

    private boolean saveChanges = false;
    public Object getDefaultValue(String path, Object defaultValue) {
        Object obj = configuration.get(path);

        if (obj == null) {
            obj = defaultValue;

            configuration.set(path, defaultValue);
            saveChanges = true;
        }

        return obj;
    }

    private boolean saveChanges() {
        return saveChanges;
    }

}
