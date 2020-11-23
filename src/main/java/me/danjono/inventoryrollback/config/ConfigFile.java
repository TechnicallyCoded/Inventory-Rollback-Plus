package me.danjono.inventoryrollback.config;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import java.io.File;
import java.io.IOException;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile {
    public static boolean enabled;

    public static File folderLocation = InventoryRollback.getInstance().getDataFolder();

    public static int maxSavesJoin;

    public static int maxSavesQuit;

    public static int maxSavesDeath;

    public static int maxSavesWorldChange;

    public static int maxSavesForce;

    public static Material deathIcon;

    public static Material joinIcon;

    public static Material quitIcon;

    public static Material worldChangeIcon;

    public static Material forceSaveIcon;

    public static String timeZone;

    public static String timeFormat;

    public static boolean updateChecker;

    public static boolean bStatsEnabled;

    private boolean saveChanges = false;

    private File configFile;

    private final FileConfiguration config;

    private MessageData msgData;

    private SoundData soundData;

    public ConfigFile() {
        this.configFile = new File(folderLocation, "config.yml");
        if (!this.configFile.exists())
            InventoryRollback.getInstance().saveDefaultConfig();
        this.configFile = new File(folderLocation, "config.yml");
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
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

    public MessageData getMsgData() {
        return this.msgData;
    }

    public SoundData getSoundData() {
        return this.soundData;
    }

    public boolean saveConfig() {
        try {
            this.config.save(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setEnabled(boolean enabled) {
        ConfigFile.enabled = enabled;
        this.config.set("enabled", Boolean.valueOf(enabled));
        saveConfig();
    }

    public void setVariables() {
        String folder = (String)getDefaultValue("folderLocation", "DEFAULT");
        if (folder == null || folder.equalsIgnoreCase("DEFAULT") || folder.isEmpty()) {
            folderLocation = InventoryRollback.getInstance().getDataFolder();
        } else {
            try {
                folderLocation = new File(folder);
            } catch (NullPointerException e) {
                folderLocation = InventoryRollback.getInstance().getDataFolder();
            }
        }
        enabled = ((Boolean)getDefaultValue("enabled", Boolean.valueOf(true))).booleanValue();
        maxSavesJoin = ((Integer)getDefaultValue("maxSaves.join", Integer.valueOf(10))).intValue();
        maxSavesQuit = ((Integer)getDefaultValue("maxSaves.quit", Integer.valueOf(10))).intValue();
        maxSavesDeath = ((Integer)getDefaultValue("maxSaves.death", Integer.valueOf(50))).intValue();
        maxSavesWorldChange = ((Integer)getDefaultValue("maxSaves.worldChange", Integer.valueOf(10))).intValue();
        maxSavesForce = ((Integer)getDefaultValue("maxSaves.force", Integer.valueOf(10))).intValue();
        try {
            deathIcon = Material.valueOf((String)getDefaultValue("icons.mainMenu.deathIcon.item", "BONE"));
        } catch (IllegalArgumentException e) {
            deathIcon = Material.valueOf("BONE");
        }
        try {
            joinIcon = Material.valueOf((String)getDefaultValue("icons.mainMenu.joinIcon.item", "SAPLING"));
        } catch (IllegalArgumentException e) {
            joinIcon = Material.valueOf(InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_13_PLUS) ? "OAK_SAPLING" : "SAPLING");
        }
        try {
            quitIcon = Material.valueOf((String)getDefaultValue("icons.mainMenu.quitIcon.item", "BED"));
        } catch (IllegalArgumentException e) {
            quitIcon = Material.valueOf(InventoryRollback.getVersion().equals(InventoryRollback.VersionName.v1_13_PLUS) ? "RED_BED" : "BED");
        }
        try {
            worldChangeIcon = Material.valueOf((String)getDefaultValue("icons.mainMenu.worldChangeIcon.item", "COMPASS"));
        } catch (IllegalArgumentException e) {
            worldChangeIcon = Material.valueOf("COMPASS");
        }
        try {
            forceSaveIcon = Material.valueOf((String)getDefaultValue("icons.mainMenu.forceSaveIcon.item", "DIAMOND"));
        } catch (IllegalArgumentException e) {
            forceSaveIcon = Material.valueOf("DIAMOND");
        }
        timeZone = (String)getDefaultValue("icons.rollbackMenu.time.timeZone", "UTC");
        timeFormat = (String)getDefaultValue("icons.rollbackMenu.time.timeFormat", "dd/MM/yyyy HH:mm:ss a");
        updateChecker = ((Boolean)getDefaultValue("updateChecker", Boolean.valueOf(true))).booleanValue();
        bStatsEnabled = ((Boolean)getDefaultValue("bStats", Boolean.valueOf(true))).booleanValue();
        this.msgData = new MessageData();
        this.msgData.setMessages();
        this.soundData = new SoundData();
        this.soundData.setSounds();
        if (this.saveChanges) {
            saveConfig();
            this.saveChanges = false;
        }
    }

    public void createStorageFolders() {
        File savesFolder = new File(folderLocation, "saves");
        if (!savesFolder.exists())
            savesFolder.mkdir();
        File joinsFolder = new File(folderLocation, "saves/joins");
        if (!joinsFolder.exists())
            joinsFolder.mkdir();
        File quitsFolder = new File(folderLocation, "saves/quits");
        if (!quitsFolder.exists())
            quitsFolder.mkdir();
        File deathsFolder = new File(folderLocation, "saves/deaths");
        if (!deathsFolder.exists())
            deathsFolder.mkdir();
        File worldChangesFolder = new File(folderLocation, "saves/worldChanges");
        if (!worldChangesFolder.exists())
            worldChangesFolder.mkdir();
        File forceSavesFolder = new File(folderLocation, "saves/force");
        if (!forceSavesFolder.exists())
            forceSavesFolder.mkdir();
    }

    public Object getDefaultValue(String path, Object defaultValue) {
        Object obj = this.config.get(path);
        if (obj == null) {
            obj = defaultValue;
            this.config.set(path, defaultValue);
            this.saveChanges = true;
        }
        return obj;
    }
}
