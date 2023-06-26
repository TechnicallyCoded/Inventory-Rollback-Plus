package me.danjono.inventoryrollback.gui.menu;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainInventoryBackupMenu {

	private final InventoryRollbackPlus main;

	private final Player staff;
	private final UUID playerUUID;
	private final LogType logType;
	private final Long timestamp;
	private final ItemStack[] mainInventory;
	private final ItemStack[] armour;
	private final ItemStack[] enderChest;
	private final String location;
	private final double health;
	private final int hunger;
	private final float saturation;
	private final float xp;
	
    private final Buttons buttons;
    private Inventory inventory;
	
	public MainInventoryBackupMenu(Player staff, PlayerData data, String location) {
		this.main = InventoryRollbackPlus.getInstance();

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
		// Make sure we are not running this on the main thread
		assert !Bukkit.isPrimaryThread();

		int item = 0;
		int position = 0;

		//If the backup file is invalid it will return null, we want to catch it here
		try {
    		// Add items, 5 per tick
			new UniversalRunnable() {

				int invPosition = 0;
				int itemPos = 0;
				final int max = mainInventory.length - 5; // excluded

				@Override
				public void run() {
					for (int i = 0; i < 6; i++) {
						// If hit max item position, stop
						if (itemPos >= max) {
							this.cancel();
							return;
						}

						ItemStack itemStack = mainInventory[itemPos];
						if (itemStack != null) {
							inventory.setItem(invPosition, itemStack);
							// Don't change inv position if there was nothing to place
							invPosition++;
						}
						// Move to next item stack
						itemPos++;
					}
				}
			}.runTaskTimer(main, 0, 1);
		} catch (NullPointerException e) {
		    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getErrorInventory());
		    return;
		}

		item = 36;
		position = 44;
		
		//Add armour
		if (armour.length > 0) {
			try {
				for (int i = 0; i < armour.length; i++) {
					// Place item safely
					final int finalPos = position;
					final int finalItem = i;
					Future<Void> placeItemFuture = InventoryRollbackPlus.getScheduler().callSyncMethod(
							() -> {
								inventory.setItem(finalPos, armour[finalItem]);
								return null;
							});
					placeItemFuture.get();
					position--;
				}
			} catch (ExecutionException | InterruptedException ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				for (int i = 36; i < mainInventory.length; i++) {
					if (mainInventory[item] != null) {
						// Place item safely
						final int finalPos = position;
						final int finalItem = item;
						Future<Void> placeItemFuture = InventoryRollbackPlus.getScheduler().callSyncMethod(
								() -> {
									inventory.setItem(finalPos, mainInventory[finalItem]);
									return null;
								});
						placeItemFuture.get();
						position--;
					}
					item++;
				}
			} catch (ExecutionException | InterruptedException ex) {
				ex.printStackTrace();
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
