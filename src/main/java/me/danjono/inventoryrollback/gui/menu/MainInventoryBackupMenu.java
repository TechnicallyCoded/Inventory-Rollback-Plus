package me.danjono.inventoryrollback.gui.menu;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;

public class MainInventoryBackupMenu {
	
	private Player staff;
	private UUID playerUUID;
	private LogType logType;
	private Long timestamp;
	private ItemStack[] mainInventory;
	private ItemStack[] armour;
	private ItemStack[] enderChest;
	private String location;
	private double health;
	private int hunger;
	private float saturation;
	private float xp;
	
    private Buttons buttons;
    private Inventory inventory;
	
	public MainInventoryBackupMenu(Player staff, PlayerData data, String location) {
		this.staff = staff;
		this.playerUUID = data.getOfflinePlayer().getUniqueId();
		this.logType = data.getLogType();
		this.timestamp = data.getTimestamp();
		this.mainInventory = data.getMainInventory();
		this.armour = data.getArmour();
	    this.enderChest = data.getEnderChest();
		this.location = location;
		this.health = data.getHealth();
		this.hunger = data.getFoodLevel();
		this.saturation = data.getSaturation();
		this.xp = data.getXP();
		
		this.buttons = new Buttons(playerUUID);
		
		createInventory();
	}
	
	public void createInventory() {
	    inventory = Bukkit.createInventory(staff, InventoryName.MAIN_BACKUP.getSize(), InventoryName.MAIN_BACKUP.getName());
	    
	    //Add back button
        inventory.setItem(46, buttons.inventoryMenuBackButton(MessageData.getBackButton(), logType, timestamp));
	}
	
	public Inventory getInventory() {
	    return this.inventory;
	}
		
	public void showBackupItems() {
		int item = 0;
		int position = 0;

		//If the backup file is invalid it will return null, we want to catch it here
		try {
    		//Add items
    		for (int i = 0; i < mainInventory.length - 5; i++) {
    			if (mainInventory[item] != null) {	
    				inventory.setItem(position, mainInventory[item]);
    				position++;
    			}
    			
    			item++;
    		}
		} catch (NullPointerException e) {
		    staff.sendMessage(MessageData.getPluginName() + MessageData.getErrorInventory());
		    return;
		}

		item = 36;
		position = 44;
		
		//Add armour
		if (armour.length > 0) {
			for (int i = 0; i < armour.length; i++) {
			    inventory.setItem(position, armour[i]);
				position--;
			}
		} else {
			for (int i = 36; i < mainInventory.length; i++) {
				if (mainInventory[item] != null) {	
				    inventory.setItem(position, mainInventory[item]);
					position--;
				}
				
				item++;
			}
		}
				
		// Add restore all player inventory button
		if (ConfigData.isRestoreToPlayerButton())
		    inventory.setItem(48, buttons.restoreAllInventory(logType, timestamp));
		 else
			inventory.setItem(48, buttons.restoreAllInventoryDisabled(logType, timestamp));

		//Add teleport back button
		inventory.setItem(49, buttons.enderPearlButton(logType, location));
		
		//Add Enderchest icon	
		inventory.setItem(50, buttons.enderChestButton(logType, timestamp, enderChest));
		
		//Add health icon
		inventory.setItem(51, buttons.healthButton(logType, health));
		
		//Add hunger icon
		inventory.setItem(52, buttons.hungerButton(logType, hunger, saturation));
		
		//Add Experience Bottle			
		inventory.setItem(53, buttons.experiencePotion(logType, xp));
	}
		
}
