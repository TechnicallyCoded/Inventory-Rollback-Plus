package me.danjono.inventoryrollback.config;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.InventoryRollback.VersionName;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private File configFile;
    private final FileConfiguration config;

    public ConfigFile() {
        this.configFile = new File(folderLocation, "config.yml");
        if (!configFile.exists())
            InventoryRollback.getInstance().saveDefaultConfig();

        this.configFile = new File(folderLocation, "config.yml");
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public ConfigFile(FileConfiguration config) {
        this.configFile = new File(folderLocation, "config.yml");
        this.config = config;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public boolean saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean enabled;

    public void setEnabled(boolean enabled) {
        ConfigFile.enabled = enabled;
        config.set("enabled", enabled);
        saveConfig();
    }

    public static File folderLocation = InventoryRollback.getInstance().getDataFolder();

    public static int maxSavesJoin;
    public static int maxSavesQuit;
    public static int maxSavesDeath;
    public static int maxSavesWorldChange;
    public static int maxSavesForce;

    public static long maxMillisAsyncLoadTime;

    public static Material deathIcon;
    public static Material joinIcon;
    public static Material quitIcon;
    public static Material worldChangeIcon;
    public static Material forceSaveIcon;

    public static String timeZone;
    public static String timeFormat;

    public static boolean updateChecker;

    public static boolean bStatsEnabled;

    public void setVariables() {
        String folder = (String) getDefaultValue("folderLocation", "DEFAULT");

        if (folder.equalsIgnoreCase("DEFAULT") || folder.isEmpty() || folder == null) {
            folderLocation = InventoryRollback.getInstance().getDataFolder();
        } else {
            try {
                folderLocation = new File(folder);
            } catch (NullPointerException e) {
                folderLocation = InventoryRollback.getInstance().getDataFolder();
            }
        }

        enabled = (boolean) getDefaultValue("enabled", true);

        maxSavesJoin = (int) getDefaultValue("maxSaves.join", 10);
        maxSavesQuit = (int) getDefaultValue("maxSaves.quit", 10);
        maxSavesDeath = (int) getDefaultValue("maxSaves.death", 50);
        maxSavesWorldChange = (int) getDefaultValue("maxSaves.worldChange", 10);
        maxSavesForce = (int) getDefaultValue("maxSaves.force", 10);

        maxMillisAsyncLoadTime = ((Number) getDefaultValue("joinMaxAsyncDelayMillis", 1000L)).longValue();

        try {
            deathIcon = Material.valueOf((String) getDefaultValue("icons.mainMenu.deathIcon.item", "BONE"));
        } catch (IllegalArgumentException e) {
            deathIcon = Material.valueOf("BONE");
        }

        try {
            joinIcon = Material.valueOf((String) getDefaultValue("icons.mainMenu.joinIcon.item", "SAPLING"));
        } catch (IllegalArgumentException e) {
            joinIcon = Material.valueOf(InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS) ? "OAK_SAPLING" : "SAPLING");
        }

        try {
            quitIcon = Material.valueOf((String) getDefaultValue("icons.mainMenu.quitIcon.item", "BED"));
        } catch (IllegalArgumentException e) {
            quitIcon = Material.valueOf(InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS) ? "RED_BED" : "BED");
        }

        try {
            worldChangeIcon = Material.valueOf((String) getDefaultValue("icons.mainMenu.worldChangeIcon.item", "COMPASS"));
        } catch (IllegalArgumentException e) {
            worldChangeIcon = Material.valueOf("COMPASS");
        }

        try {
            forceSaveIcon = Material.valueOf((String) getDefaultValue("icons.mainMenu.forceSaveIcon.item", "DIAMOND"));
        } catch (IllegalArgumentException e) {
            forceSaveIcon = Material.valueOf("DIAMOND");
        }

        timeZone = (String) getDefaultValue("icons.rollbackMenu.time.timeZone", "UTC");
        timeFormat = (String) getDefaultValue("icons.rollbackMenu.time.timeFormat", "dd/MM/yyyy HH:mm:ss a");

        updateChecker = (boolean) getDefaultValue("updateChecker", true);

        bStatsEnabled = (boolean) getDefaultValue("bStats", true);

        new MessageData().setMessages();
        new SoundData().setSounds();

        if (saveChanges) {
            saveConfig();
            saveChanges = false;
        }
    }

    public void createStorageFolders() {
        //Create folder for where player inventories will be saved
        File savesFolder = new File(folderLocation, "saves");
        if (!savesFolder.exists())
            savesFolder.mkdir();

        //Create folder for joins
        File joinsFolder = new File(folderLocation, "saves/joins");
        if (!joinsFolder.exists())
            joinsFolder.mkdir();

        //Create folder for quits
        File quitsFolder = new File(folderLocation, "saves/quits");
        if (!quitsFolder.exists())
            quitsFolder.mkdir();

        //Create folder for deaths
        File deathsFolder = new File(folderLocation, "saves/deaths");
        if (!deathsFolder.exists())
            deathsFolder.mkdir();

        //Create folder for world changes
        File worldChangesFolder = new File(folderLocation, "saves/worldChanges");
        if (!worldChangesFolder.exists())
            worldChangesFolder.mkdir();

        //Create folder for force saves
        File forceSavesFolder = new File(folderLocation, "saves/force");
        if (!forceSavesFolder.exists())
            forceSavesFolder.mkdir();
    }

    private boolean saveChanges = false;

    public Object getDefaultValue(String path, Object defaultValue) {
        Object obj = config.get(path);

        if (obj == null) {
            obj = defaultValue;

            config.set(path, defaultValue);
            saveChanges = true;
        }

        return obj;
    }

}
