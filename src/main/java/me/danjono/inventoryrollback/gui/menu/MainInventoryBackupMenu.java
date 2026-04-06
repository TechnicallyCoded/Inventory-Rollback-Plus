package me.danjono.inventoryrollback.gui.menu;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.folia.FoliaRunnable;
import com.nuclyon.technicallycoded.inventoryrollback.folia.SchedulerUtils;
import com.tcoded.lightlibs.bukkitversion.MCVersion;
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
import java.util.concurrent.atomic.AtomicInteger;

public class MainInventoryBackupMenu {

	public static final int GIVE_SHULKERS_BUTTON_SLOT = 47;
	private final InventoryRollbackPlus main;

	private final Player staff;
	private final UUID playerUUID;
	private final LogType logType;
	private final Long timestamp;
	private final ItemStack[] mainInventory;
	private final ItemStack[] armor;
	private final ItemStack[] enderChest;
	private final String location;
	private final double health;
	private final int hunger;
	private final float saturation;
	private final float xp;
	
    private final Buttons buttons;
    private Inventory inventory;

	private int mainInvLen;
	
	public MainInventoryBackupMenu(Player staff, PlayerData data, String location) {
		this.main = InventoryRollbackPlus.getInstance();

		this.staff = staff;
		this.playerUUID = data.getOfflinePlayer().getUniqueId();
		this.logType = data.getLogType();
		this.timestamp = data.getTimestamp();
		this.mainInventory = data.getMainInventory();
		this.armor = data.getArmour();
	    this.enderChest = data.getEnderChest();
		this.location = location;
		this.health = data.getHealth();
		this.hunger = data.getFoodLevel();
		this.saturation = data.getSaturation();
		this.xp = data.getXP();
		
		this.buttons = new Buttons(playerUUID);

		this.mainInvLen = mainInventory == null ? 0 : mainInventory.length;
		
		createInventory();
	}
	
	public void createInventory() {
	    inventory = Bukkit.createInventory(staff, InventoryName.MAIN_BACKUP.getSize(), InventoryName.MAIN_BACKUP.getName());
	    
	    //Add back button
        inventory.setItem(45, buttons.inventoryMenuBackButton(MessageData.getBackButton(), logType, timestamp));

		// Add get shulker button
		if (main.getVersion().greaterOrEqThan(MCVersion.v1_11.toBukkitVersion()))
			inventory.setItem(GIVE_SHULKERS_BUTTON_SLOT, buttons.giveShulkerBox(logType, timestamp));

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
	
	public Inventory getInventory() {
	    return this.inventory;
	}
		
	public void showBackupItems() {
		// Make sure we are not running this on the main thread
		assert !Bukkit.isPrimaryThread();

		int item = 0;
		AtomicInteger position = new AtomicInteger();

		//If the backup file is invalid it will return null, we want to catch it here
		try {
    		// Add items, 5 per tick
			SchedulerUtils.runTaskTimer(null, new FoliaRunnable() {

				boolean processedHotbar;
				int menuPos = 27;
				int backupPos = 0;
				final int max = Math.min(mainInvLen, 36); // excluded

				@Override
				public void run() {
					for (int i = 0; i < 6; i++) {
						// If hit max item position, stop
						if (backupPos >= max) {
							this.cancel();
							return;
						}

						ItemStack itemStack = mainInventory[backupPos];
						if (itemStack != null) {
							inventory.setItem(menuPos, itemStack);
						}

						// Move to next menu slot
						menuPos++;
						// We were incrementing the hotbar position (bottom of the UI) first. Once we reach that
						// slot, we move back to the top of the UI for the rest of the inventory
						if (menuPos >= 36 && !processedHotbar) {
							menuPos = 0;
						}

						// Move to next item stack
						backupPos++;
					}
				}
			}, 1, 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getErrorInventory());
		    return;
		}

		item = 36;
		position.set(44);
		
		//Add armor
		if (armor != null && armor.length > 0) {
            for (int i = 0; i < armor.length; i++) {
                // Place item safely
                final int finalPos = position.getAndDecrement();
                final int finalItem = i;
                SchedulerUtils.callSyncMethod(null, () -> {
                    inventory.setItem(finalPos, armor[finalItem]);
                    return null;
                }).whenComplete((res, ex) -> {
                    if (ex != null) ex.printStackTrace();
                });
            }
		} else {
            for (; item < mainInvLen; item++) {
                if (mainInventory[item] != null) {
                    // Place item safely
                    final int finalPos = position.getAndDecrement();
                    final int finalItem = item;
                    SchedulerUtils.callSyncMethod(null, () -> {
                        inventory.setItem(finalPos, mainInventory[finalItem]);
                        return null;
                    }).whenComplete((res, ex) -> {
                        if (ex != null) ex.printStackTrace();
                    });
                } else {
                    position.getAndDecrement();
                }
            }
		}
	}
		
}
