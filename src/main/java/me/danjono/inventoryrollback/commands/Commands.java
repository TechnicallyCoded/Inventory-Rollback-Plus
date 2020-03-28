package me.danjono.inventoryrollback.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.MySQL;
import me.danjono.inventoryrollback.data.YAML;
import me.danjono.inventoryrollback.gui.menu.MainMenu;
import me.danjono.inventoryrollback.gui.menu.PlayerMenu;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class Commands extends ConfigData implements CommandExecutor {

    private CommandSender sender;

    @Override		
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

            this.sender = sender;

            if (args.length == 0) {
                restoreCommand(args);
                return true;
            }

            switch (args[0]) {
            case "restore":
                restoreCommand(args);
                break;

            case "forcebackup":
            case "forcesave":
                forceBackupCommand(args);
                break;

            case "enable":
                enableCommand();
                break;

            case "disable":
                disableCommand();
                break;

            case "reload": 
                reloadCommand();
                break;
                
            case "help":
                helpCommand();
                break;
                
            case "version":
                sender.sendMessage(MessageData.getPluginName() + "Server is running v" + InventoryRollback.getPluginVersion() + " - Created by danjono");
                break;

            case "convertmysql":
                convertMySQL();
                break;                                

            case "convertyaml":
                convertYAML();
                break; 

            default:
                sender.sendMessage(MessageData.getPluginName() + MessageData.getError());
            }
        }

        return true;

    }

    private void restoreCommand(String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("inventoryrollback.restore")) {
                if (!ConfigData.isEnabled()) {
                    sender.sendMessage(MessageData.getPluginName() + MessageData.getPluginDisabled());
                    return;
                }

                openBackupMenu((Player) sender, args);
                
            } else {
                sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
            }
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getPlayerOnlyError());
        }
    }
    
    private void openBackupMenu(Player staff, String[] args) {
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
            sender.sendMessage(MessageData.getPluginName() + MessageData.getError());
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

    private void forceBackupCommand(String[] args) {
        if (sender.hasPermission("inventoryrollback.forcebackup")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(MessageData.getPluginName() + MessageData.getError());
                return;
            }

            OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[1]);

            if (!offlinePlayer.isOnline()) {
                sender.sendMessage(MessageData.getPluginName() + MessageData.getNotOnlineError(offlinePlayer.getName()));
                return;
            }

            Player player = (Player) offlinePlayer;
            new SaveInventory(player, LogType.FORCE, null, player.getInventory(), player.getEnderChest()).createSave();

            sender.sendMessage(MessageData.getPluginName() + MessageData.getForceBackup(offlinePlayer.getName()));
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
        }
    }

    private void enableCommand() {
        if (sender.hasPermission("inventoryrollback.enable")) {
            setEnabled(true);
            saveConfig();

            sender.sendMessage(MessageData.getPluginName() + MessageData.getPluginEnabled());
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
        }
    }

    private void disableCommand() {
        if (sender.hasPermission("inventoryrollback.disable")) {
            setEnabled(false);
            saveConfig();

            sender.sendMessage(MessageData.getPluginName() + MessageData.getPluginDisabled());
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
        }
    }

    private void reloadCommand() {
        if (sender.hasPermission("inventoryrollback.reload")) {                                     
            InventoryRollback.startupTasks();

            sender.sendMessage(MessageData.getPluginName() + MessageData.getPluginReload());
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
        }
    }
    
    private void helpCommand() {
        //TODO Write up a help command
    }
    
    private void convertMySQL() {
        if (sender instanceof ConsoleCommandSender && sender.isOp()) {
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), MySQL::convertYAMLToMySQL);
        }
    }
    
    private void convertYAML() {
        if (sender instanceof ConsoleCommandSender && sender.isOp()) {
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), YAML::convertOldBackupData);
        }
    }

}