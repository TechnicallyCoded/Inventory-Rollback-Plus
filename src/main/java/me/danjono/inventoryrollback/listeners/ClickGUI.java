package me.danjono.inventoryrollback.listeners;

import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.config.SoundData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.BackupMenu;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.gui.RollbackListMenu;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBT;

public class ClickGUI extends Buttons implements Listener {

	@EventHandler
	private void onMainMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(InventoryName.MAIN_MENU.getName()))
			return;

		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;

		if (e.getInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();

		if ((e.getRawSlot() >= 0 && e.getRawSlot() < 9) && e.getView().getTitle().equals(InventoryName.MAIN_MENU.getName())) {				
			//Clicked in menu area	
			if (currentItem.getType() == Material.AIR && cursorItem.getType() == Material.AIR) {
				return;
			}

			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			LogType logType = LogType.valueOf(nbt.getString("logType"));
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

			staff.openInventory(new RollbackListMenu(staff, player, logType, 1).showBackups());
		} else if (!e.isShiftClick()){
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void onRollbackListMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(InventoryName.ROLLBACK_LIST.getName()))
			return;

		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;

		if (e.getInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();

		if ((e.getRawSlot() >= 0 && e.getRawSlot() < 45) && e.getView().getTitle().equals(InventoryName.ROLLBACK_LIST.getName())) {
			//Clicked in menu area	
			if (currentItem.getType() == Material.AIR && cursorItem.getType() == Material.AIR)
				return;

			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			if (currentItem.getType().equals(Material.CHEST)) {
				UUID uuid = UUID.fromString(nbt.getString("uuid"));
				Long timestamp = nbt.getLong("timestamp");
				LogType logType = LogType.valueOf(nbt.getString("logType"));
				String location = nbt.getString("location");

				FileConfiguration playerData = new PlayerData(uuid, logType).getData();
				
				RestoreInventory restore = new RestoreInventory(playerData, timestamp);
				
				
				ItemStack[] inventory = restore.retrieveMainInventory();
				ItemStack[] armour = restore.retrieveArmour();

				boolean enderchest = false;				
				for (ItemStack item : restore.retrieveEnderChestInventory()) {
					if (item != null)
						enderchest = true;
				}

				Float xp = restore.getXP();
				Double health = restore.getHealth();
				int hunger = restore.getHunger();
				float saturation = restore.getSaturation();

				//If the backup file is invalid it will return null, we want to catch it here
				try {
				    staff.openInventory(new BackupMenu(staff, uuid, logType, timestamp, inventory, armour, location, enderchest, health, hunger, saturation, xp).showItems());
				} catch (NullPointerException e1) {}
			} else if (currentItem.getType().equals(getPageSelectorIcon().getType())) {
				int page = nbt.getInt("page");

				if (page == 0) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

					staff.openInventory(new MainMenu(staff, player).getMenu());
				} else {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
					LogType logType = LogType.valueOf(nbt.getString("logType"));

					staff.openInventory(new RollbackListMenu(staff, player, logType, page).showBackups());
				}
			}	
		} else if (!e.isShiftClick()) {
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void onBackupMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(InventoryName.BACKUP.getName()))
			return;

		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;

		if (e.getInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		MessageData messages = new MessageData();

		if ((e.getRawSlot() >= 45 && e.getRawSlot() < 54) && e.getView().getTitle().equals(InventoryName.BACKUP.getName())) {
			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
			LogType logType = LogType.valueOf(nbt.getString("logType"));
			Long timestamp = nbt.getLong("timestamp");

			PlayerData data = new PlayerData(offlinePlayer, logType);		
			FileConfiguration playerData = data.getData();

			RestoreInventory restore = new RestoreInventory(playerData, timestamp);

			if (currentItem.getType().equals(getPageSelectorIcon().getType())) {
				//Click on back button
				staff.openInventory(new RollbackListMenu(staff, offlinePlayer, logType, 1).showBackups());
			} else if (currentItem.getType().equals(getEnderPearlIcon().getType())) {			    
			    //Clicked Ender Pearl
				String[] location = nbt.getString("location").split(",");			
				World world = Bukkit.getWorld(location[0]);
				
				if (world == null) {
					//World is not available
				    staff.sendMessage(MessageData.pluginName + new MessageData().deathLocationInvalidWorld(location[0]));
				    return;
				}
				
				Location loc = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])).add(0.5, 0, 0.5);				
				staff.teleport(loc);

				if (SoundData.enderPearlEnabled)
					staff.playSound(loc, SoundData.enderPearl, SoundData.enderPearlVolume, 1);

				staff.sendMessage(MessageData.pluginName + messages.deathLocationTeleport(loc));
				
                //Stop the function working on shift click as cancelling the event seems to not stop the item being moved to player inventory
                if (e.isShiftClick())
                    return;
			} else if (currentItem.getType().equals(getEnderChestIcon().getType())) {
				//Clicked Ender Chest

				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	

					ItemStack[] enderchest = restore.retrieveEnderChestInventory();

					if (emptyEnderChest(player)) {
						player.getEnderChest().setContents(enderchest);

						if (SoundData.enderChestEnabled)
							player.playSound(player.getLocation(), SoundData.enderChest, SoundData.enderChestVolume, 1);
					} else {
						staff.sendMessage(MessageData.pluginName + messages.enderChestNotEmpty(player.getName()));
						e.setCancelled(true);
						return;
					}

					staff.sendMessage(MessageData.pluginName + messages.enderChestRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(MessageData.pluginName + messages.enderChestRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(MessageData.pluginName + messages.enderChestNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(getHealthIcon().getType())) {
				//Clicked health button

				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	
					Double health = nbt.getDouble("health");

					player.setHealth(health);

					if (SoundData.foodEnabled)
						player.playSound(player.getLocation(), SoundData.food, SoundData.foodVolume, 1);

					staff.sendMessage(MessageData.pluginName + messages.healthRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(MessageData.pluginName + messages.healthRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(MessageData.pluginName + messages.healthNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(getHungerIcon().getType())) {
				//Clicked hunger button	

				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	
					int hunger = nbt.getInt("hunger");
					Float saturation = nbt.getFloat("saturation");

					player.setFoodLevel(hunger);
					player.setSaturation(saturation);

					if (SoundData.hungerEnabled)
						player.playSound(player.getLocation(), SoundData.hunger, SoundData.hungerVolume, 1);

					staff.sendMessage(MessageData.pluginName + messages.hungerRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(MessageData.pluginName + messages.hungerRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(MessageData.pluginName + messages.hungerNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(getExperienceIcon().getType())) {
				//Clicked XP button

				if (offlinePlayer.isOnline()) {				
					Player player = (Player) offlinePlayer;	
					Float xp = nbt.getFloat("xp");

					RestoreInventory.setTotalExperience(player, xp);

					if (SoundData.experienceEnabled)
						player.playSound(player.getLocation(), SoundData.experience, SoundData.experienceVolume, 1);

					staff.sendMessage(MessageData.pluginName + messages.experienceRestored(player.getName(), (int) RestoreInventory.getLevel(xp)));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(MessageData.pluginName + messages.experienceRestoredPlayer(staff.getName(), xp.intValue()));
				} else {				    
					staff.sendMessage(MessageData.pluginName + messages.experienceNotOnline(offlinePlayer.getName()));
				}
			}
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void blockMenuDrags(InventoryDragEvent e) {
		if (!e.getView().getTitle().equals(InventoryName.MAIN_MENU.getName()) && !e.getView().getTitle().equals(InventoryName.ROLLBACK_LIST.getName()))
			return;

		e.setCancelled(true);
	}

	private boolean emptyEnderChest(Player player) {
		boolean empty = true;
		ListIterator<ItemStack> ec = player.getEnderChest().iterator();

		while (ec.hasNext()) {
			if (ec.next() != null) {							
				empty = false;
				break;
			}
		}

		return empty;
	}

}