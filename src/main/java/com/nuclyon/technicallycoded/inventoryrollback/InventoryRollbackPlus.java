package com.nuclyon.technicallycoded.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.commands.Commands;
import com.nuclyon.technicallycoded.inventoryrollback.UpdateChecker.UpdateResult;

import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import io.papermc.lib.PaperLib;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.listeners.ClickGUI;
import me.danjono.inventoryrollback.listeners.EventLogs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;

import java.util.logging.Level;

public class InventoryRollbackPlus extends InventoryRollback {

    private static InventoryRollbackPlus instancePlus;
    private ConfigData configData;
    private EnumNmsVersion version = EnumNmsVersion.v1_13_R1;

    public static InventoryRollbackPlus getInstance() {
        return instancePlus;
    }

    @Override
    public void onEnable() {
        instancePlus = this;
        InventoryRollback.setInstance(instancePlus);

        InventoryRollback.setPackageVersion(Bukkit.getServer().getClass().getPackage().getName()
                .replace(".",  ",").split(",")[3]);

        if (!this.isCompatible()) {
            getLogger().log(Level.WARNING, MessageData.getPluginName() + "\n" + ChatColor.RED +
                    " ** WARNING... Plugin may not be compatible with this version of Minecraft. **\n" +
                    " ** Please fully test the plugin before using on your server as features may be broken. **\n" +
                    MessageData.getPluginName()
            );
        }

        super.startupTasks();

        if (ConfigData.isbStatsEnabled()) initBStats();

        PluginCommand plCmd = getCommand("inventoryrollbackplus");
        Commands cmds = new Commands(this);
        if (plCmd == null) return;
        plCmd.setExecutor(cmds);
        plCmd.setTabCompleter(cmds);
        getServer().getPluginManager().registerEvents(new ClickGUI(), this);
        getServer().getPluginManager().registerEvents(new EventLogs(), this);
        PaperLib.suggestPaper(this);
    }

    @Override
    public void onDisable() {
        instancePlus = null;
        super.onDisable();
    }

    public void setVersion(EnumNmsVersion versionName) {
        version = versionName;
    }

    public EnumNmsVersion getVersion() {
        return version;
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
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            getPluginLogger().log(Level.INFO, MessageData.getPluginName() + "Checking for updates...");

            final UpdateResult result = new UpdateChecker(getInstance(), 85811).getResult();

            int prioLevel = 0;
            String prioColor = ChatColor.AQUA.toString();
            String prioLevelName = "null";

            switch (result.getType()) {
                case FAIL_SPIGOT:
                    getPluginLogger().log(Level.INFO, MessageData.getPluginName() + ChatColor.GOLD + "Warning: Could not contact Spigot to check if an update is available.");
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
                    getPluginLogger().log(Level.INFO, MessageData.getPluginName() + ChatColor.GOLD + "Warning: You are running an experimental/development build! Proceed with caution.");
                    break;
                case NO_UPDATE:
                    getPluginLogger().log(Level.INFO, MessageData.getPluginName() + ChatColor.RESET + "You are running the latest version.");
                    break;
                default:
                    break;
            }

            if (prioLevel > 0) {
                getPluginLogger().log(Level.INFO, "\n" + prioColor +
                        "===============================================================================\n" +
                        "A " + prioLevelName + " update to InventoryRollbackPlus is available!\n" +
                        "Download at https://www.spigotmc.org/resources/inventoryrollbackplus-1-8-1-16-x.85811/\n" +
                        "(current: " + result.getCurrentVer() + ", latest: " + result.getLatestVer() + ")\n" +
                        "===============================================================================");
            }

        });
    }

    public void initBStats() {
        super.bStats();
    }

}
