package me.danjono.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.UpdateChecker;
import com.nuclyon.technicallycoded.inventoryrollback.listeners.ClickGUI;
import me.danjono.inventoryrollback.commands.Commands;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.listeners.EventLogs;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public class InventoryRollback extends JavaPlugin {
    public static final Logger logger = Logger.getLogger("Minecraft");

    private static InventoryRollback instance;

    private ConfigFile configFile;

    private static String packageVersion;

    public static InventoryRollback getInstance() {
        return instance;
    }

    public static String getPluginVersion() {
        return instance.getDescription().getVersion();
    }

    public static String getPackageVersion() {
        return packageVersion;
    }

    public void onEnable() {
        instance = this;
        packageVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if (!isCompatible()) {
            logger.log(Level.WARNING, ChatColor.RED + " ** WARNING... Plugin may not be compatible with this version of Minecraft. **");
            logger.log(Level.WARNING, ChatColor.RED + " ** Tested versions: 1.8.8 to 1.16.4 **");
            logger.log(Level.WARNING, ChatColor.RED + " ** Please fully test the plugin before using on your server as features may be broken. **");
        }
        startupTasks();
        if (ConfigFile.bStatsEnabled)
            bStats();
        PluginCommand plCmd = getCommand("inventoryrollback");
        Commands cmds = new Commands(this);
        plCmd.setExecutor((CommandExecutor)cmds);
        plCmd.setTabCompleter((TabCompleter)cmds);
        getServer().getPluginManager().registerEvents((Listener)new ClickGUI(), (Plugin)instance);
        getServer().getPluginManager().registerEvents((Listener)new EventLogs(), (Plugin)instance);
    }

    public void onDisable() {
        instance = null;
    }

    public void startupTasks() {
        this.configFile = new ConfigFile();
        this.configFile.setVariables();
        this.configFile.createStorageFolders();
        checkUpdate(ConfigFile.updateChecker);
    }

    private enum CompatibleVersions {
        v1_8_R1, v1_8_R2, v1_8_R3,
        v1_9_R1, v1_9_R2,
        v1_10_R1,
        v1_11_R1,
        v1_12_R1,
        v1_13_R1, v1_13_R2,
        v1_14_R1,
        v1_15_R1,
        V1_16_R1, V1_16_R2, v1_16_R3;
    }

    public enum VersionName {
        v1_8, v1_9_v1_12, v1_13_PLUS;
    }

    private static VersionName version = VersionName.v1_13_PLUS;

    public static VersionName getVersion() {
        return version;
    }

    public ConfigFile getConfigFile() {
        return this.configFile;
    }

    private boolean isCompatible() {
        for (CompatibleVersions v : CompatibleVersions.values()) {
            if (v.name().equalsIgnoreCase(packageVersion)) {
                if (v.name().equalsIgnoreCase("v1_8_R1") || v
                        .name().equalsIgnoreCase("v1_8_R2") || v
                        .name().equalsIgnoreCase("v1_8_R3")) {
                    version = VersionName.v1_8;
                } else if (v.name().equalsIgnoreCase("v1_9_R1") || v
                        .name().equalsIgnoreCase("v1_9_R2") || v
                        .name().equalsIgnoreCase("v1_10_R1") || v
                        .name().equalsIgnoreCase("v1_11_R1") || v
                        .name().equalsIgnoreCase("v1_12_R1")) {
                    version = VersionName.v1_9_v1_12;
                }
                return true;
            }
        }
        return false;
    }

    private void bStats() {
        Metrics metrics = new Metrics((Plugin)this);
    }

    public static void checkUpdate(boolean enabled) {
        if (!enabled)
            return;
        logger.log(Level.INFO, "Checking for updates...");
        com.nuclyon.technicallycoded.inventoryrollback.UpdateChecker.UpdateResult result = (new UpdateChecker(instance, Integer.valueOf(85811), enabled)).getResult();
        switch (result) {
            case FAIL_SPIGOT:
                logger.log(Level.INFO, "Could not contact Spigot.");
                break;
            case UPDATE_AVAILABLE:
                logger.log(Level.INFO, ChatColor.AQUA + "======================================================================================");
                logger.log(Level.INFO, ChatColor.AQUA + "An update to InventoryRollbackPlus is available!");
                logger.log(Level.INFO, ChatColor.AQUA + "Download at https://www.spigotmc.org/resources/inventoryrollbackplus-1-8-1-16-x.85811/");
                logger.log(Level.INFO, ChatColor.AQUA + "======================================================================================");
                break;
            case NO_UPDATE:
                logger.log(Level.INFO, ChatColor.AQUA + "You are running the latest version.");
                break;
        }
    }
}
