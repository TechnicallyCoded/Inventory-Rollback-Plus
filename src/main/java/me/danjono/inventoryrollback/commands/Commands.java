package me.danjono.inventoryrollback.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.MySQL;
import me.danjono.inventoryrollback.data.YAML;
import me.danjono.inventoryrollback.gui.menu.MainMenu;
import me.danjono.inventoryrollback.gui.menu.PlayerMenu;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class Commands extends ConfigData implements CommandExecutor, TabCompleter {

    @Override		
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

            if (args.length == 0) {
                restoreCommand(sender, args);
                return true;
            }

            switch (args[0]) {
            case "restore":
                restoreCommand(sender, args);
                break;

            case "forcebackup":
            case "forcesave":
                forceBackupCommand(sender, args);
                break;

            case "enable":
                enableCommand(sender);
                break;

            case "disable":
                disableCommand(sender);
                break;

            case "reload": 
                reloadCommand(sender);
                break;

            case "help":
                helpCommand();
                break;

            case "version":
                versionCommand(sender);
                break;

            case "convertmysql":
                convertMySQL(sender);
                break;                                

            case "convertyaml":
                convertYAML(sender);
                break;

            default:
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
            }
        }

        return true;

    }

    private void restoreCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("inventoryrollback.restore")) {
                if (!ConfigData.isEnabled()) {
                    sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginDisabled());
                    return;
                }

                openBackupMenu(sender, (Player) sender, args);

            } else {
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
            }
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPlayerOnlyError());
        }
    }

    private void openBackupMenu(CommandSender sender, Player staff, String[] args) {
        if (args.length <= 0 || args.length == 1) {
            try {
                openMainMenu(staff);
            } catch (NullPointerException e) {}
        } else if(args.length == 2) {
            @SuppressWarnings("deprecation")
            OfflinePlayer rollbackPlayer = Bukkit.getOfflinePlayer(args[1]);

            try {
                openPlayerMenu(staff, rollbackPlayer);
            } catch (NullPointerException e) {}
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
        }
    }

    private void openMainMenu(Player staff) {
        MainMenu menu = new MainMenu(staff, 1);

        staff.openInventory(menu.getInventory());
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getMainMenu);
    }

    private void openPlayerMenu(Player staff, OfflinePlayer offlinePlayer) {
        PlayerMenu menu = new PlayerMenu(staff, offlinePlayer);

        staff.openInventory(menu.getInventory());
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getPlayerMenu);
    }

    private void forceBackupCommand(CommandSender sender, String[] args) {
        if (sender.hasPermission("inventoryrollback.forcebackup")) {
            if (args.length == 1 || args.length > 3) {
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
                return;
            }

            if (args[1].equalsIgnoreCase("all")) {
                forceBackupAll(sender);
            } else if (args[1].equalsIgnoreCase("player")) {
                forceBackupPlayer(sender, args);
            } else {
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
            }
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

    private void forceBackupAll(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new SaveInventory(player, LogType.FORCE, null, null, player.getInventory(), player.getEnderChest()).createSave(true);
        }

        sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getForceBackupAll());
    }

    private void forceBackupPlayer(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[2]);

        if (offlinePlayer == null) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNotOnlineError(args[2]));
            return;
        }

        if (!offlinePlayer.isOnline()) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNotOnlineError(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        new SaveInventory(player, LogType.FORCE, null, null, player.getInventory(), player.getEnderChest()).createSave(true);

        sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getForceBackupPlayer(offlinePlayer.getName()));
    }

    private void enableCommand(CommandSender sender) {
        if (sender.hasPermission("inventoryrollback.enable")) {
            setEnabled(true);
            saveConfig();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginEnabled());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

    private void disableCommand(CommandSender sender) {
        if (sender.hasPermission("inventoryrollback.disable")) {
            setEnabled(false);
            saveConfig();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginDisabled());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

    private void reloadCommand(CommandSender sender) {
        if (sender.hasPermission("inventoryrollback.reload")) {                                     
            InventoryRollback.getInstance().startupTasks();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginReload());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

    private void helpCommand() {
        //TODO Write up a help command
    }

    private void versionCommand(CommandSender sender) {
        if (sender.hasPermission("inventoryrollback.version"))
            sender.sendMessage(MessageData.getPluginPrefix() + "Server is running v" + InventoryRollback.getPluginVersion() + " - Created by danjono");
    }

    private void convertMySQL(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender && sender.isOp()) {
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), MySQL::convertYAMLToMySQL);
        }
    }

    private void convertYAML(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender && sender.isOp()) {
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), YAML::convertOldBackupData);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> commands = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("ir") || cmd.getName().equalsIgnoreCase("inventoryrollback")) {
            switch (args[0]) {
            case "restore":
                if (sender.hasPermission("inventoryrollback.restore"))
                    return restoreAutoComplete(args);
                break;

            case "forcebackup":
            case "forcesave":
                if (sender.hasPermission("inventoryrollback.forcebackup"))    
                    return forceBackupAutoComplete(args);
                break;

            default:
                return defaultCommandsAutoComplete(sender, args);
            }
        }

        return commands;
    }

    private List<String> defaultCommandsAutoComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();
        Set<String> commands = new HashSet<>();

        if (args.length > 1)
            return completions;

        if (sender.hasPermission("inventoryrollback.disable"))
            commands.add("disable"); 

        if (sender.hasPermission("inventoryrollback.enable"))
            commands.add("enable");

        if (sender.hasPermission("inventoryrollback.forcebackup"))
            commands.add("forcebackup");

        if (sender.hasPermission("inventoryrollback.reload"))
            commands.add("reload");

        if (sender.hasPermission("inventoryrollback.restore"))
            commands.add("restore");

        if (sender.hasPermission("inventoryrollback.version"))
            commands.add("version");

        if (!commands.isEmpty())
            StringUtil.copyPartialMatches(args[0], commands, completions);

        Collections.sort(completions);
        return completions;
    }

    private List<String> restoreAutoComplete(String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 2)
            commands = playerListAutoComplete();

        return commands;
    }

    private List<String> forceBackupAutoComplete(String[] args) {
        List<String> completions = new ArrayList<>();
        Set<String> commands = new HashSet<>();

        if (args.length == 3 && args[1].equalsIgnoreCase("player"))
            return playerListAutoComplete();

        if (args.length == 2) {
            commands.add("all");
            commands.add("player");
        }

        if (!commands.isEmpty())
            StringUtil.copyPartialMatches(args[1], commands, completions);

        Collections.sort(completions);
        return completions;
    }

    private List<String> playerListAutoComplete() {
        List<String> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }

        Collections.sort(players);
        return players;
    }

}