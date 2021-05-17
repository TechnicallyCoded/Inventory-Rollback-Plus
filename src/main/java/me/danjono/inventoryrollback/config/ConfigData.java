package me.danjono.inventoryrollback.config;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.TimeZone;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.danjono.inventoryrollback.InventoryRollback;

public class ConfigData {

    private File configurationFile;
    private FileConfiguration configuration;
    private static String configurationFileName = "config.yml";

    public ConfigData() {
        generateConfigFile();
    }

    private void generateConfigFile() {
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

        private SaveType(String name) {
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
    private static String mysqlPrefix;
    private static String mysqlUsername;
    private static String mysqlPassword;
    private static boolean mysqlUseSSL;
    private static boolean mysqlVerifyCertificate;
    private static boolean mysqlPubKeyRetrieval;

    private static boolean restoreToPlayerButton;
    private static int backupLinesVisible;

    private static int maxSavesJoin;
    private static int maxSavesQuit;
    private static int maxSavesDeath;
    private static int maxSavesWorldChange;
    private static int maxSavesForce;

    private static TimeZone timeZone;
    private static SimpleDateFormat timeFormat;

    private static boolean updateChecker;

    private static boolean bStatsEnabled;

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
        setSaveType(SaveType.YAML);

        setMySQLEnabled((boolean) getDefaultValue("mysql.enabled", false));
        if (isMySQLEnabled())
            setSaveType(SaveType.MYSQL);            

        setMySQLHost((String) getDefaultValue("mysql.details.host", "127.0.0.1"));
        setMySQLPort((int) getDefaultValue("mysql.details.port", 3306));
        setMySQLDatabase((String) getDefaultValue("mysql.details.database", "inventory_rollback"));
        setMySQLPrefix((String) getDefaultValue("mysql.details.prefix", ""));
        setMySQLUsername((String) getDefaultValue("mysql.details.username", "username"));
        setMySQLPassword((String) getDefaultValue("mysql.details.password", "password"));
        setMySQLUseSSL((boolean) getDefaultValue("mysql.details.use-SSL", true));
        setMySQLVerifyCertificate((boolean) getDefaultValue("mysql.details.verifyCertificate", true));
        setMysqlPubKeyRetrievalAllowed((boolean) getDefaultValue("mysql.details.allowPubKeyRetrieval", false));

        setRestoreToPlayerButton((boolean) getDefaultValue("restore-to-player-button", false));
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

    public static void setMySQLPrefix(String value) {
        mysqlPrefix = value;
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
            timeZone = TimeZone.getTimeZone(ZoneId.of(zone));
        } catch (ZoneRulesException e) {
            timeZone = TimeZone.getTimeZone("GMT");
            InventoryRollback.getInstance().getLogger().log(Level.WARNING, ("Time zone ID \"" + zone + "\" in config.yml is not valid. Defaulting to \"GMT\""));
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

    public static String getMySQLPrefix() {
        return mysqlPrefix;
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
