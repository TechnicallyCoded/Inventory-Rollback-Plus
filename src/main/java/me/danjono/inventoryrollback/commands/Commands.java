package me.danjono.inventoryrollback.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import me.inventoryrollback.danjono.data.LogType;

public class Commands extends ConfigFile implements CommandExecutor {

	@Override		
	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

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
	
								if(args.length == 1) {
									staff.openInventory(new MainMenu(staff, staff).getMenu());
								} else if(args.length == 2) {
									@SuppressWarnings("deprecation")
									OfflinePlayer rollbackPlayer = Bukkit.getOfflinePlayer(args[1]);
	
									if (!rollbackPlayer.hasPlayedBefore()) {
										sender.sendMessage(MessageData.pluginName + messages.neverOnServer(rollbackPlayer.getName()));
										break;
									}
	
									staff.openInventory(new MainMenu(staff, rollbackPlayer).getMenu());
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
					} case "forcebackup": {						
						if (sender.hasPermission("inventoryrollback.forcebackup")) {
							if (args.length == 1 || args.length > 2) {
								sender.sendMessage(MessageData.pluginName + MessageData.error);
								break;
							}
							
							@SuppressWarnings("deprecation")
							OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
							if (!player.hasPlayedBefore()) {
								sender.sendMessage(MessageData.pluginName + messages.neverOnServer(player.getName()));
								break;
							} else if (!player.isOnline()) {
								sender.sendMessage(MessageData.pluginName + messages.notOnline(player.getName()));
								break;
							}
								
							Player onlinePlayer = (Player) player;
							new SaveInventory(onlinePlayer, LogType.FORCE, null, onlinePlayer.getInventory(), onlinePlayer.getEnderChest()).createSave();
							sender.sendMessage(MessageData.pluginName + messages.forceSaved(player.getName()));

							break;
						} else {
							sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
						}
						break;
					} case "enable": {
						if (sender.hasPermission("InventoryRollback.enable")) {
							setEnabled(true);
							saveConfig();
	
							sender.sendMessage(MessageData.pluginName + MessageData.enabledMessage);
						} else {
							sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
						}
						break;
					} case "disable": { 
						if (sender.hasPermission("InventoryRollback.disable")) {
							setEnabled(false);
							saveConfig();
	
							sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
						} else {
							sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
						}
						break;
					} case "reload": { 
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