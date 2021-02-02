package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.*;
import me.danjono.inventoryrollback.config.MessageData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Commands implements CommandExecutor, TabCompleter {

    private InventoryRollbackPlus main;

    private String[] defaultOptions = new String[] {"restore", "forcebackup", "enable", "disable", "reload", "version", "help"};
    private String[] backupOptions = new String[] {"all", "player"};

    private HashMap<String, IRPCommand> subCommands = new HashMap<>();

    public Commands(InventoryRollbackPlus mainIn) {
        this.main = mainIn;
        this.subCommands.put("restore", new Restore(mainIn));
        this.subCommands.put("enable", new Enable(mainIn));
        this.subCommands.put("disable", new Disable(mainIn));
        this.subCommands.put("reload", new Reload(mainIn));
        this.subCommands.put("version", new Version(mainIn));
        this.subCommands.put("forcebackup", new ForceBackup(mainIn));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("inventoryrollback") ||
                label.equalsIgnoreCase("ir") ||
                label.equalsIgnoreCase("irp") ||
                label.equalsIgnoreCase("inventoryrollbackplus")
        ) {
            if (args.length == 0) {
                sender.sendMessage(
                        MessageData.getPluginName() + "Server is running v" + InventoryRollbackPlus.getPluginVersion() + " - Maintained by TechnicallyCoded\n" +
                        MessageData.getPluginName() + "Available Commands:\n" +
                        MessageData.getPluginName() + "/ir restore [player]" + ChatColor.GRAY + " - Open rollback GUI for optional [player]\n" +
                        MessageData.getPluginName() + "/ir forcebackup <all/player> [player]" + ChatColor.GRAY + " - Create a forced save of a player's inventory\n" +
                        MessageData.getPluginName() + "/ir enable" + ChatColor.GRAY + " - Enable the plugin\n" +
                        MessageData.getPluginName() + "/ir disable" + ChatColor.GRAY + " - Disable the plugin\n" +
                        MessageData.getPluginName() + "/ir reload" + ChatColor.GRAY + " - Reload the plugin\n" +
                        MessageData.getPluginName() + "/ir version" + ChatColor.GRAY + " - Reload the plugin\n");
                return true;
            }
            IRPCommand irpCmd = this.subCommands.get(args[0]);
            if (irpCmd != null) {
                irpCmd.onCommand(sender, cmd, label, args);
                return true;
            }
            sender.sendMessage(MessageData.getPluginName() + MessageData.getError());
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String name, String[] args) {
        if (args.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();
            for (String option : this.defaultOptions) {
                if (option.startsWith(args[0].toLowerCase()))
                    suggestions.add(option);
            }
            return suggestions;
        } else if (args.length == 2) {
            String[] opts;
            if (args[0].equalsIgnoreCase("forcebackup") ||
                    args[0].equalsIgnoreCase("forcesave")) {
                opts = this.backupOptions;
            } else {
                opts = null;
            }
            if (opts == null) return null;
            ArrayList<String> suggestions = new ArrayList<>();
            for (String option : opts) {
                if (option.startsWith(args[1].toLowerCase()))
                    suggestions.add(option);
            }
            return suggestions;
        }
        return null;
    }
}
