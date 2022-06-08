package me.danjono.inventoryrollback;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.danjono.inventoryrollback.UpdateChecker.UpdateResult;
import me.danjono.inventoryrollback.commands.Commands;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.ConfigData.SaveType;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.config.SoundData;
import me.danjono.inventoryrollback.data.MySQL;
import me.danjono.inventoryrollback.data.YAML;
import me.danjono.inventoryrollback.listeners.ClickGUI;
import me.danjono.inventoryrollback.listeners.EventLogs;

public abstract class InventoryRollback extends JavaPlugin {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private static InventoryRollback instance;
    private static String packageVersion;
    private ConfigData configData;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static void setInstance(InventoryRollback plugin) {
        instance = plugin;
    }

    public static InventoryRollback getInstance() {
        return InventoryRollbackPlus.getInstance();
    }

    public static void setPackageVersion(String version) {
        packageVersion = version;
    }

    public static String getPackageVersion() {
        return packageVersion;
    }

    public static String getPluginVersion() {
        return instance.getDescription().getVersion();  
    }

    public ConfigData getConfigData() {
        return configData;
    }

    @Override
    public void onEnable() {
        // !!!! WARNING !!!! This method is never used since it's overridden by the InventoryRollbackPlus onEnable()
        setInstance(this);
        setPackageVersion(Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3]);

        if (!isCompatible()) {        
            logger.log(Level.WARNING, MessageData.getPluginPrefix() + ChatColor.RED + " ** WARNING... Plugin may not be compatible with this version of Minecraft. **");
            logger.log(Level.WARNING, MessageData.getPluginPrefix() + ChatColor.RED + " ** Please fully test the plugin before using on your server as features may be broken. **");
        }

        startupTasks();

        if (ConfigData.isbStatsEnabled())
            bStats();

        Objects.requireNonNull(this.getCommand("inventoryrollback")).setExecutor(new Commands());

        this.getServer().getPluginManager().registerEvents(new ClickGUI(), instance);
        this.getServer().getPluginManager().registerEvents(new EventLogs(), instance);
    }

    @Override
    public void onDisable() {
        setInstance(null);
    }

    public void startupTasks() {
        configData = new ConfigData();

        configData.setVariables();

        if (ConfigData.getSaveType() == SaveType.YAML) {
            YAML.createStorageFolders();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            try {
                new MySQL(null, null, (long) 0).createTables();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        new MessageData().setMessages();    
        new SoundData().setSounds();

        InventoryRollbackPlus.getInstance().getConsoleSender().sendMessage(MessageData.getPluginPrefix() + "Inventory backup data is set to save to: " + ConfigData.getSaveType().getName());

        if (ConfigData.isUpdateCheckerEnabled())
            getInstance().checkUpdate();
    }

    /*public enum CompatibleVersions {
        V1_8_R1,
        V1_8_R2,
        V1_8_R3,
        V1_9_R1,
        V1_9_R2,
        V1_10_R1,
        V1_11_R1,
        V1_12_R1,
        V1_13_R1,
        V1_13_R2,
        V1_14_R1,
        V1_15_R1,
        V1_16_R1,
        V1_16_R2,
        V1_16_R3
    }*/

    /*public enum VersionName {
        V1_8,
        V1_9_V1_12,
        V1_13_PLUS
    }*/

    private static EnumNmsVersion version = EnumNmsVersion.v1_13_R1;

    public abstract void setVersion(EnumNmsVersion versionName);
    /*{
        version = versionName;
    }*/

    public abstract EnumNmsVersion getVersion();
    /*{
        return version;
    }*/

    public abstract boolean isCompatible();
    /* {
        for (CompatibleVersions v : CompatibleVersions.values()) {
            if (v.name().equalsIgnoreCase(packageVersion)) {
                //Check if 1.8
                if (v.name().equalsIgnoreCase("v1_8_R1") 
                        || v.name().equalsIgnoreCase("v1_8_R2")
                        || v.name().equalsIgnoreCase("v1_8_R3")) {
                    setVersion(VersionName.V1_8);
                } 
                //Check if 1.9 - 1.12.2
                else if (v.name().equalsIgnoreCase("v1_9_R1") 
                        || v.name().equalsIgnoreCase("v1_9_R2")
                        || v.name().equalsIgnoreCase("v1_10_R1")
                        || v.name().equalsIgnoreCase("v1_11_R1")
                        || v.name().equalsIgnoreCase("v1_12_R1")) {
                    setVersion(VersionName.V1_9_V1_12);
                }
                //Else it is 1.13+
                return true;
            }
        }

        return false;
    }*/

    public void bStats() {
        // Override by IRP
    }

    public void checkUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            logger.log(Level.INFO, MessageData.getPluginPrefix() + "Checking for updates...");

            final UpdateResult result = new UpdateChecker(getInstance(), 85811).getResult();

            switch (result) {
            case FAIL_SPIGOT:
                logger.log(Level.INFO, MessageData.getPluginPrefix() + "Could not contact Spigot to check if an update is available.");
                break;
            case UPDATE_AVAILABLE:		
                logger.log(Level.INFO, ChatColor.AQUA + "======================================================================================");
                logger.log(Level.INFO, ChatColor.AQUA + "An update to InventoryRollbackPlus is available!");
                logger.log(Level.INFO, ChatColor.AQUA + "Download at https://www.spigotmc.org/resources/inventoryrollback-plus-1-8-1-16-x.85811/");
                logger.log(Level.INFO, ChatColor.AQUA + "======================================================================================");
                break;
            case NO_UPDATE:
                logger.log(Level.INFO, MessageData.getPluginPrefix() + ChatColor.AQUA + "You are running the latest version.");
                break;
            default:
                break;
            }
        });
    }

}
