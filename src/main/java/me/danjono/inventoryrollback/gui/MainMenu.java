package me.danjono.inventoryrollback.gui;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.Messages;
import me.danjono.inventoryrollback.config.PlayerData;

public class MainMenu {
	
	private Player staff;
	private OfflinePlayer player;
	
	public MainMenu(Player staff,  OfflinePlayer player) {
		this.staff = staff;
		this.player = player;
	}

	public Inventory getMenu() {				
		Inventory mainMenu = Bukkit.createInventory(staff, 9, InventoryNames.mainMenu);
		Buttons buttons = new Buttons();

		UUID uuid = player.getUniqueId();

		File joinsFile = new PlayerData(player, "JOIN").getFile();
		File quitsFile = new PlayerData(player, "QUIT").getFile();
		File deathsFile = new PlayerData(player, "DEATH").getFile();
		File worldChangeFile = new PlayerData(player, "WORLDCHANGE").getFile();
		File forceSaveFile = new PlayerData(player, "FORCE").getFile();

		mainMenu.setItem(0, buttons.playerHead(player.getName(), null));

		int position = 2;

		if (deathsFile.exists()) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(Material.getMaterial(ConfigFile.deathIcon)), uuid, Messages.deathIconName, "DEATH", null));
			position++;
		}

		if (joinsFile.exists()) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(Material.getMaterial(ConfigFile.joinIcon)), uuid, Messages.joinIconName, "JOIN", null));
			position++;
		}

		if (quitsFile.exists()) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(Material.getMaterial(ConfigFile.quitIcon)), uuid, Messages.quitIconName, "QUIT", null));
			position++;
		}

		if (worldChangeFile.exists()) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(Material.getMaterial(ConfigFile.worldChangeIcon)), uuid, Messages.worldChangeIconName, "WORLDCHANGE", null));
			position++;
		}
		
		if (forceSaveFile.exists()) {
			mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(Material.getMaterial(ConfigFile.forceSaveIcon)), uuid, Messages.forceSaveIconName, "FORCE", null));
			position++;
		}

		return mainMenu;
	}

}
