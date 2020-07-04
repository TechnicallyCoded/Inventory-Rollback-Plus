package me.danjono.inventoryrollback;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
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

public class InventoryRollback extends JavaPlugin {

    private static final Logger logger = Logger.getLogger("Minecraft");
    private static InventoryRollback instance;
    private static String packageVersion;

    public static Logger getPluginLogger() {
        return logger;
    }

    private static void setInstance(InventoryRollback plugin) {
        instance = plugin;
    }

    public static InventoryRollback getInstance() {
        return instance;
    }

    private static void setPackageVerison(String version) {
        packageVersion = version;
    }

    public static String getPackageVersion() {
        return packageVersion;
    }

    public static String getPluginVersion() {
        return instance.getDescription().getVersion();  
    }

    @Override
    public void onEnable() {
        setInstance(this);
        setPackageVerison(Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3]);

        if (!isCompatible()) {        
            logger.log(Level.WARNING, MessageData.getPluginName() + ChatColor.RED + " ** WARNING... Plugin may not be compatible with this version of Minecraft. **");
            logger.log(Level.WARNING, MessageData.getPluginName() + ChatColor.RED + " ** Please fully test the plugin before using on your server as features may be broken. **");
        }

        startupTasks();

        if (ConfigData.isbStatsEnabled())
            bStats();

        this.getCommand("inventoryrollback").setExecutor(new Commands());

        this.getServer().getPluginManager().registerEvents(new ClickGUI(), instance);
        this.getServer().getPluginManager().registerEvents(new EventLogs(), instance);
    }

    @Override
    public void onDisable() {
        setInstance(null);
    }

    public static void startupTasks() {
        ConfigData config = new ConfigData();

        config.setVariables();

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

        logger.log(Level.INFO, () -> MessageData.getPluginName() + "Inventory backup data is set to save to: " + ConfigData.getSaveType().getName());

        if (ConfigData.isUpdateCheckerEnabled())
            checkUpdate();
    }

    private enum CompatibleVersions {
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
        V1_16_R1
    }

    public enum VersionName {
        V1_8,
        V1_9_V1_12,
        V1_13_PLUS
    }

    private static VersionName version = VersionName.V1_13_PLUS;

    private static void setVersion(VersionName versionName) {
        version = versionName;
    }

    public static VersionName getVersion() {
        return version;
    }

    private boolean isCompatible() {
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
    }

    private void bStats() {
        Metrics metrics = new Metrics(this);

        if (metrics.isEnabled())
            logger.log(Level.INFO, MessageData.getPluginName() + "bStats are enabled");

        metrics.addCustomChart(new Metrics.SimplePie("database_type", new Callable<String>() {
            @Override
            public String call() throws Exception {
                return ConfigData.getSaveType().getName();
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("restore_to_player_enabled", new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (ConfigData.isRestoreToPlayerButton()) {
                    return "Enabled";
                } else {
                    return "Disabled";
                }
            }
        }));

        metrics.addCustomChart(new Metrics.SimplePie("save_location", new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (ConfigData.getFolderLocation() == InventoryRollback.getInstance().getDataFolder()) {
                    return "Default";
                } else {
                    return "Not Default";
                }
            }
        }));
    }

    public static void checkUpdate() {
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            logger.log(Level.INFO, MessageData.getPluginName() + "Checking for updates...");

            final UpdateResult result = new UpdateChecker(instance, 48074).getResult();

            switch (result) {
            case FAIL_SPIGOT:
                logger.log(Level.INFO, MessageData.getPluginName() + "Could not contact Spigot to check if an update is available.");
                break;
            case UPDATE_AVAILABLE:		
                logger.log(Level.INFO, ChatColor.AQUA + "===============================================================================");
                logger.log(Level.INFO, ChatColor.AQUA + "An update to InventoryRollback is available!");
                logger.log(Level.INFO, ChatColor.AQUA + "Download at https://www.spigotmc.org/resources/inventoryrollback.48074/");
                logger.log(Level.INFO, ChatColor.AQUA + "===============================================================================");		
                break;
            case NO_UPDATE:
                logger.log(Level.INFO, MessageData.getPluginName() + ChatColor.AQUA + "You are running the latest version.");
                break;
            default:
                break;
            }
        });
    }

}
