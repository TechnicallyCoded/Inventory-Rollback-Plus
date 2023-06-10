package me.danjono.inventoryrollback.gui.menu;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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
		if (data.getArmour().length > 0) {
			this.mainInventory = data.getMainInventory();
			this.armour = data.getArmour();
		} else {
			this.mainInventory = Arrays.copyOf(data.getMainInventory(), Math.max(0, data.getMainInventory().length - 5));
			this.armour = Arrays.copyOfRange(data.getMainInventory(), Math.max(0, data.getMainInventory().length - 5), data.getMainInventory().length);
		}
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

		final AtomicInteger position = new AtomicInteger(0);
		final int length = mainInventory.length;
		//If the backup file is invalid it will return null, we want to catch it here
		main.getScheduler().runAtGlobalRate(task -> {
			for (int i = 0; i < 5; i++) {
				if (position.get() >= length) {
					task.cancel();
					return;
				}
				ItemStack itemStack = mainInventory[position.get()];
				if (itemStack == null) continue;
				inventory.setItem(position.getAndIncrement(), itemStack);
			}
		}, 1, 1);

		//Add armour
		main.getScheduler().runAtGlobal(() -> {
			for (int i = 0; i < armour.length; i++) {
				ItemStack itemStack = armour[armour.length - i - 1];
				if (itemStack == null) continue;
				inventory.setItem(44 - 6 + i, itemStack);
			}
		});

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
