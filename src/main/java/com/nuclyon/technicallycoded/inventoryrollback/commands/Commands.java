package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.config.ConfigFile;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import com.nuclyon.technicallycoded.inventoryrollback.gui.MainMenu;
import com.nuclyon.technicallycoded.inventoryrollback.inventory.SaveInventory;
import com.nuclyon.technicallycoded.inventoryrollback.data.LogType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands extends ConfigFile implements CommandExecutor, TabCompleter {

    private String[] options = new String[] {"restore", "forcebackup", "enable", "disable", "reload"};

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

            MessageData messages = new MessageData();

            if (args.length == 0) {
                //Give version information
                sender.sendMessage(
                        MessageData.pluginName + "Server is running v" + InventoryRollback.getPluginVersion() + " - Maintained by TechnicallyCoded\n" +
                        MessageData.pluginName + "Available Commands:\n" +
                        MessageData.pluginName + "/ir restore [player]" + ChatColor.GRAY + " - Open rollback GUI for optional [player]\n" +
                        MessageData.pluginName + "/ir forcebackup <player>" + ChatColor.GRAY + " - Create a forced save of a player's inventory\n" +
                        MessageData.pluginName + "/ir enable" + ChatColor.GRAY + " - Enable the plugin\n" +
                        MessageData.pluginName + "/ir disable" + ChatColor.GRAY + " - Disable the plugin\n" +
                        MessageData.pluginName + "/ir reload" + ChatColor.GRAY + " - Reload the plugin\n"
                );
                return true;
            } else {
                switch (args[0]) {
                    case "restore": {
                        if (sender instanceof Player) {
                            if (sender.hasPermission("inventoryrollback.restore")) {
                                if (!ConfigFile.enabled) {
                                    sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
                                    break;
                                }

                                Player staff = (Player) sender;

                                if (args.length == 1) {
                                    try {
                                        staff.openInventory(new MainMenu(staff, staff).getMenu());
                                    } catch (NullPointerException ignored) {
                                    }
                                } else if (args.length == 2) {
                                    @SuppressWarnings("deprecation")
                                    OfflinePlayer rollbackPlayer = Bukkit.getOfflinePlayer(args[1]);

                                    try {
                                        staff.openInventory(new MainMenu(staff, rollbackPlayer).getMenu());
                                    } catch (NullPointerException ignored) {
                                    }
                                } else {
                                    sender.sendMessage(MessageData.pluginName + MessageData.error);
                                }
                            } else {
                                sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                            }
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.playerOnly);
                        }
                        break;
                    }
                    case "forcebackup": {
                        if (sender.hasPermission("inventoryrollback.forcebackup")) {
                            if (args.length == 1 || args.length > 2) {
                                sender.sendMessage(MessageData.pluginName + MessageData.error);
                                break;
                            }

                            @SuppressWarnings("deprecation")
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

                            if (!offlinePlayer.isOnline()) {
                                sender.sendMessage(MessageData.pluginName + messages.notOnline(offlinePlayer.getName()));
                                break;
                            }

                            Player player = (Player) offlinePlayer;
                            new SaveInventory(player, LogType.FORCE, null, player.getInventory(), player.getEnderChest()).createSave();
                            sender.sendMessage(MessageData.pluginName + messages.forceSaved(offlinePlayer.getName()));

                            break;
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                    case "enable": {
                        if (sender.hasPermission("inventoryrollback.enable")) {
                            setEnabled(true);
                            saveConfig();

                            sender.sendMessage(MessageData.pluginName + MessageData.enabledMessage);
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                    case "disable": {
                        if (sender.hasPermission("inventoryrollback.disable")) {
                            setEnabled(false);
                            saveConfig();

                            sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                    case "reload": {
                        if (sender.hasPermission("inventoryrollback.reload")) {
                            InventoryRollback.startupTasks();

                            sender.sendMessage(MessageData.pluginName + MessageData.reloadMessage);
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                }
            }
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();
            for (String option : options) {
                if (option.startsWith(strings[0].toLowerCase()))
                    suggestions.add(option);
            }
            return suggestions;
        }
        return null;
    }
}