package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.Disable;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.Enable;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.Reload;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.Restore;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Commands implements CommandExecutor, TabCompleter {
    private String[] options = new String[] { "restore", "forcebackup", "enable", "disable", "reload" };

    private HashMap<String, IRPCommand> subCommands = new HashMap<>();

    public Commands(InventoryRollback mainIn) {
        this.subCommands.put("restore", new Restore(mainIn));
        this.subCommands.put("enable", new Enable(mainIn));
        this.subCommands.put("disable", new Disable(mainIn));
        this.subCommands.put("reload", new Reload(mainIn));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("inventoryrollback") || label.equalsIgnoreCase("ir")) {
            if (args.length == 0) {
                sender.sendMessage(MessageData.pluginName + "Server is running v" +
                        InventoryRollback.getPluginVersion() + " - Maintained by TechnicallyCoded\n" + MessageData.pluginName + "Available Commands:\n" + MessageData.pluginName + "/ir restore [player]" + ChatColor.GRAY + " - Open rollback GUI for optional [player]\n" + MessageData.pluginName + "/ir forcebackup <player>" + ChatColor.GRAY + " - Create a forced save of a player's inventory\n" + MessageData.pluginName + "/ir enable" + ChatColor.GRAY + " - Enable the plugin\n" + MessageData.pluginName + "/ir disable" + ChatColor.GRAY + " - Disable the plugin\n" + MessageData.pluginName + "/ir reload" + ChatColor.GRAY + " - Reload the plugin\n");
                return true;
            }
            IRPCommand irpCmd = this.subCommands.get(args[0]);
            if (irpCmd != null)
                irpCmd.onCommand(sender, cmd, label, args);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();
            for (String option : this.options) {
                if (option.startsWith(strings[0].toLowerCase()))
                    suggestions.add(option);
            }
            return suggestions;
        }
        return null;
    }
}
