package me.danjono.inventoryrollback.commands;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands extends ConfigFile implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

            MessageData messages = new MessageData();

            if (args.length == 0) {
                //Give version information
                sender.sendMessage(MessageData.pluginName + "Server is running v" + InventoryRollback.getPluginVersion() + " - Created by danjono");
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
                        if (sender.hasPermission("InventoryRollback.enable")) {
                            setEnabled(true);
                            saveConfig();

                            sender.sendMessage(MessageData.pluginName + MessageData.enabledMessage);
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                    case "disable": {
                        if (sender.hasPermission("InventoryRollback.disable")) {
                            setEnabled(false);
                            saveConfig();

                            sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
                        } else {
                            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
                        }
                        break;
                    }
                    case "reload": {
                        if (sender.hasPermission("InventoryRollback.reload")) {
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
}