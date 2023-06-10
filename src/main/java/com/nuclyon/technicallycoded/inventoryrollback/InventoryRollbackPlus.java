package com.nuclyon.technicallycoded.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.commands.Commands;
import com.nuclyon.technicallycoded.inventoryrollback.UpdateChecker.UpdateResult;

import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import com.nuclyon.technicallycoded.inventoryrollback.util.TimeZoneUtil;
import io.papermc.lib.PaperLib;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import me.danjono.inventoryrollback.listeners.ClickGUI;
import me.danjono.inventoryrollback.listeners.EventLogs;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryRollbackPlus extends InventoryRollback {

    private static InventoryRollbackPlus instancePlus;

    private TimeZoneUtil timeZoneUtil = null;

    private ConfigData configData;
    private EnumNmsVersion version = EnumNmsVersion.v1_13_R1;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public static InventoryRollbackPlus getInstance() {
        return instancePlus;
    }

    @Override
    public void onEnable() {
        instancePlus = this;
        InventoryRollback.setInstance(instancePlus);

        // Load Utils
        this.timeZoneUtil = new TimeZoneUtil();

        // Load Config
        configData = new ConfigData();
        configData.setVariables(); // requires TimeZoneUtil

        // Init NMS
        InventoryRollback.setPackageVersion(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
        if (ConfigData.isDebugEnabled()) getLogger().info("Found NMS Package Version: " + getPackageVersion());

        if (!this.isCompatible()) {
            getLogger().warning(MessageData.getPluginPrefix() + "\n" +
                    " ** WARNING... Plugin may not be compatible with this version of Minecraft. **\n" +
                    " ** Please fully test the plugin before using on your server as features may be broken. **\n" +
                    MessageData.getPluginPrefix()
            );
        }

        // Storage Init & Update checker
        super.startupTasks();

        // bStats
        if (ConfigData.isbStatsEnabled()) initBStats();

        // Commands
        PluginCommand plCmd = getCommand("inventoryrollbackplus");
        Commands cmds = new Commands(this);
        if (plCmd == null) return;
        plCmd.setExecutor(cmds);
        plCmd.setTabCompleter(cmds);

        // Events
        getServer().getPluginManager().registerEvents(new ClickGUI(), this);
        getServer().getPluginManager().registerEvents(new EventLogs(), this);

        // PaperLib
        PaperLib.suggestPaper(this);
    }

    @Override
    public void onDisable() {
        // Signal to the plugin that new tasks cannot be scheduled
        getLogger().info("Setting shutdown state");
        shuttingDown.set(true);

        // Save all inventories
        getLogger().info("Saving player inventories...");
        for (Player player : this.getServer().getOnlinePlayers()) {
            if (player.hasPermission("inventoryrollbackplus.leavesave")) {
                new SaveInventory(player, LogType.QUIT, null, null, player.getInventory(), player.getEnderChest())
                        .createSave(false);
            }
        }
        getLogger().info("Done saving player inventories!");

        // Unregister event listeners
        HandlerList.unregisterAll(this);

        // Cancel tasks
        this.getScheduler().cancelTasks();

        // Clear instance references
        instancePlus = null;
        super.onDisable();

        getLogger().info("Plugin is disabled!");
    }

    public void setVersion(EnumNmsVersion versionName) {
        version = versionName;
    }

    public boolean isCompatible() {
        for (EnumNmsVersion v : EnumNmsVersion.values()) {
            if (v.name().equalsIgnoreCase(getPackageVersion())) {
                this.setVersion(v);
                return true;
            }
        }

        return false;
    }

    public void checkUpdate() {
        this.getScheduler().runAsync(() -> {
            InventoryRollbackPlus.getInstance().getConsoleSender().sendMessage(MessageData.getPluginPrefix() + "Checking for updates...");

            final UpdateResult result = new UpdateChecker(getInstance(), 85811).getResult();

            int prioLevel = 0;
            String prioColor = ChatColor.AQUA.toString();
            String prioLevelName = "null";

            switch (result.getType()) {
                case FAIL_SPIGOT:
                    getConsoleSender().sendMessage(MessageData.getPluginPrefix() + ChatColor.GOLD + "Warning: Could not contact Spigot to check if an update is available.");
                    break;
                case UPDATE_LOW:
                    prioLevel = 1;
                    prioLevelName = "minor";
                    break;
                case UPDATE_MEDIUM:
                    prioLevel = 2;
                    prioLevelName = "feature";
                    prioColor = ChatColor.GOLD.toString();
                    break;
                case UPDATE_HIGH:
                    prioLevel = 3;
                    prioLevelName = "MAJOR";
                    prioColor = ChatColor.RED.toString();
                    break;
                case DEV_BUILD:
                    getConsoleSender().sendMessage(MessageData.getPluginPrefix() + ChatColor.GOLD + "Warning: You are running an experimental/development build! Proceed with caution.");
                    break;
                case NO_UPDATE:
                    getConsoleSender().sendMessage(MessageData.getPluginPrefix() + ChatColor.RESET + "You are running the latest version.");
                    break;
                default:
                    break;
            }

            if (prioLevel > 0) {
                getConsoleSender().sendMessage( "\n" + prioColor +
                        "===============================================================================\n" +
                        "A " + prioLevelName + " update to InventoryRollbackPlus is available!\n" +
                        "Download at https://www.spigotmc.org/resources/inventoryrollbackplus-1-8-1-16-x.85811/\n" +
                        "(current: " + result.getCurrentVer() + ", latest: " + result.getLatestVer() + ")\n" +
                        "===============================================================================\n");
            }

        });
    }

    public void initBStats() {
        bStats();
    }

    @Override
    public void bStats() {
        Metrics metrics = new Metrics(this,  	9437);

        if (ConfigData.isbStatsEnabled())
            InventoryRollbackPlus.getInstance().getConsoleSender().sendMessage(MessageData.getPluginPrefix() + "bStats are enabled");

        metrics.addCustomChart(new SimplePie("database_type", () -> ConfigData.getSaveType().getName()));

        metrics.addCustomChart(new SimplePie("restore_to_player_enabled", () -> {
            if (ConfigData.isRestoreToPlayerButton()) {
                return "Enabled";
            } else {
                return "Disabled";
            }
        }));

        metrics.addCustomChart(new SimplePie("save_location", () -> {
            if (ConfigData.getFolderLocation() == InventoryRollback.getInstance().getDataFolder()) {
                return "Default";
            } else {
                return "Not Default";
            }
        }));

        metrics.addCustomChart(new SimplePie("storage_type", () -> {
            if (ConfigData.isMySQLEnabled()) {
                return "MySQL";
            } else {
                return "YAML";
            }
        }));
    }

    // GETTERS

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    public EnumNmsVersion getVersion() {
        return version;
    }

    public ConsoleCommandSender getConsoleSender() {
        return this.getServer().getConsoleSender();
    }

    public TimeZoneUtil getTimeZoneUtil() {
        return this.timeZoneUtil;
    }

    public ConfigData getConfigData() {
        return configData;
    }
}
