package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.*;
import me.danjono.inventoryrollback.config.MessageData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Commands implements CommandExecutor, TabCompleter {

    private InventoryRollbackPlus main;

    private String[] defaultOptions = new String[] {"restore", "forcebackup", "enable", "disable", "reload", "version", "import", "help"};
    private String[] backupOptions = new String[] {"all", "player"};
    private String[] importOptions = new String[] {"confirm"};

    private HashMap<String, IRPCommand> subCommands = new HashMap<>();

    public Commands(InventoryRollbackPlus mainIn) {
        this.main = mainIn;
        this.subCommands.put("restore", new RestoreSubCmd(mainIn));
        this.subCommands.put("enable", new EnableSubCmd(mainIn));
        this.subCommands.put("disable", new DisableSubCmd(mainIn));
        this.subCommands.put("reload", new ReloadSubCmd(mainIn));
        this.subCommands.put("version", new VersionSubCmd(mainIn));
        this.subCommands.put("forcebackup", new ForceBackupSubCmd(mainIn));
        this.subCommands.put("import", new ImportSubCmd(mainIn));
        this.subCommands.put("help", new HelpSubCmd(mainIn));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("inventoryrollback") ||
                label.equalsIgnoreCase("ir") ||
                label.equalsIgnoreCase("irp") ||
                label.equalsIgnoreCase("inventoryrollbackplus")
        ) {
            if (args.length == 0) {
                ((HelpSubCmd) this.subCommands.get("help")).sendHelp(sender);
                return true;
            }
            IRPCommand irpCmd = this.subCommands.get(args[0]);
            if (irpCmd != null) {
                irpCmd.onCommand(sender, cmd, label, args);
                return true;
            }
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
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

            } else if (args[0].equalsIgnoreCase("import") &&
                    (ImportSubCmd.shouldShowConfirmOption() || args[1].toLowerCase().startsWith("c"))) {
                opts = this.importOptions;

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
