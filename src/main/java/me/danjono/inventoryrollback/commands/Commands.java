package me.danjono.inventoryrollback.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.Messages;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class Commands implements CommandExecutor {

	@Override		
	public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("inventoryrollback") || cmd.getName().equalsIgnoreCase("ir")) {

			Messages messages = new Messages();
			ConfigFile config = new ConfigFile();

			if (args.length == 0) {
				//Give version information
				sender.sendMessage(Messages.pluginName + "Server is running v" + InventoryRollback.version + " - Created by danjono");
				return true;
			} else {
				switch (args[0]) {
					case "restore": {
						if (sender instanceof Player) {
							if (sender.hasPermission("inventoryrollback.restore")) {
								if (!ConfigFile.enabled) {
									sender.sendMessage(Messages.pluginName + Messages.disabledMessage);
									break;
								}
								
								Player staff = (Player) sender;
	
								if(args.length == 1) {
									staff.openInventory(new MainMenu(staff, staff).getMenu());
								} else if(args.length == 2) {
									@SuppressWarnings("deprecation")
									OfflinePlayer rollbackPlayer = Bukkit.getOfflinePlayer(args[1]);
	
									if (!rollbackPlayer.hasPlayedBefore()) {
										sender.sendMessage(Messages.pluginName + messages.neverOnServer(rollbackPlayer.getName()));
										break;
									}
	
									staff.openInventory(new MainMenu(staff, rollbackPlayer).getMenu());
								} else {
									sender.sendMessage(Messages.pluginName + Messages.error);
								}
							} else {
								sender.sendMessage(Messages.pluginName + Messages.noPermission);
							}
						} else {
							sender.sendMessage(Messages.pluginName + Messages.playerOnly);
						}
						break;
					} case "forcebackup": {						
						if (sender.hasPermission("inventoryrollback.forcebackup")) {
							if (args.length == 1 || args.length > 2) {
								sender.sendMessage(Messages.pluginName + Messages.error);
								break;
							}
							
							@SuppressWarnings("deprecation")
							OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
							if (!player.hasPlayedBefore()) {
								sender.sendMessage(Messages.pluginName + messages.neverOnServer(player.getName()));
								break;
							} else if (!player.isOnline()) {
								sender.sendMessage(Messages.pluginName + messages.notOnline(player.getName()));
								break;
							}
								
							Player onlinePlayer = (Player) player;
							new SaveInventory(onlinePlayer, "FORCE", null, onlinePlayer.getInventory(), onlinePlayer.getEnderChest()).createSave();
							sender.sendMessage(Messages.pluginName + messages.forceSaved(player.getName()));

							break;
						} else {
							sender.sendMessage(Messages.pluginName + Messages.noPermission);
						}
						break;
					} case "enable": {
						if (sender.hasPermission("InventoryRollback.enable")) {
							config.setEnabled(true);
							config.saveConfig();
	
							sender.sendMessage(Messages.pluginName + Messages.enabledMessage);
						} else {
							sender.sendMessage(Messages.pluginName + Messages.noPermission);
						}
						break;
					} case "disable": { 
						if (sender.hasPermission("InventoryRollback.disable")) {
							config.setEnabled(false);
							config.saveConfig();
	
							sender.sendMessage(Messages.pluginName + Messages.disabledMessage);
						} else {
							sender.sendMessage(Messages.pluginName + Messages.noPermission);
						}
						break;
					} case "reload": { 
						if (sender.hasPermission("InventoryRollback.reload")) {										
							new InventoryRollback().startupTasks();
	
							sender.sendMessage(Messages.pluginName + Messages.reloadMessage);
						} else {
							sender.sendMessage(Messages.pluginName + Messages.noPermission);
						}
						break;
					}
				}
			}
		}
			
		return true;

	}
}