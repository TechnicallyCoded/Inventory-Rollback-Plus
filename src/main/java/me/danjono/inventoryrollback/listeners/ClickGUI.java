package me.danjono.inventoryrollback.listeners;

import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.Messages;
import me.danjono.inventoryrollback.config.PlayerData;
import me.danjono.inventoryrollback.config.Sounds;
import me.danjono.inventoryrollback.gui.BackupMenu;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.gui.RollbackListMenu;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBT;

public class ClickGUI implements Listener {
		
	@EventHandler
	private void onMainMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTopInventory().getName().equals(InventoryName.MAIN_MENU.getName()))
			return;
		
		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;
		
		if (e.getClickedInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();

		if ((e.getRawSlot() >= 0 && e.getRawSlot() < 9) && e.getClickedInventory().getName().equals(InventoryName.MAIN_MENU.getName())) {				
			//Clicked in menu area	
			if (currentItem.getType() == Material.AIR && cursorItem.getType() == Material.AIR) {
				return;
			}

			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			final String logType = nbt.getString("logType");
			final OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
			
			staff.openInventory(new RollbackListMenu(staff, player, logType, 1).showBackups());
		} else if (!e.isShiftClick()){
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void onRollbackListMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTopInventory().getName().equals(InventoryName.ROLLBACK_LIST.getName()))
			return;
		
		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;

		if (e.getClickedInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursorItem = e.getCursor();

		if ((e.getRawSlot() >= 0 && e.getRawSlot() < 45) && e.getClickedInventory().getName().equals(InventoryName.ROLLBACK_LIST.getName())) {
			//Clicked in menu area	
			if (currentItem.getType() == Material.AIR && cursorItem.getType() == Material.AIR)
				return;

			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			if (currentItem.getType().equals(Material.CHEST)) {
				UUID uuid = UUID.fromString(nbt.getString("uuid"));
				Long timestamp = nbt.getLong("timestamp");
				String logType = nbt.getString("logType");

				FileConfiguration playerData = new PlayerData(uuid, logType).getData();
				RestoreInventory restore = new RestoreInventory();
				ItemStack[] inventory = restore.retrieveMainInventory(playerData, timestamp);
				ItemStack[] armour = restore.retrieveArmour(playerData, timestamp);

				boolean enderchest = false;				
				for (ItemStack item : restore.retrieveEnderChestInventory(playerData, timestamp)) {
					if (item != null)
						enderchest = true;
				}

				Float xp = restore.getXP(playerData, timestamp);
				Double health = restore.getHealth(playerData, timestamp);
				int hunger = restore.getHunger(playerData, timestamp);
				float saturation = restore.getSaturation(playerData, timestamp);

				staff.openInventory(new BackupMenu(staff, uuid, logType, timestamp, inventory, armour, enderchest, health, hunger, saturation, xp).showItems());
			} else if (currentItem.getType().equals(Material.BANNER)) {
				int page = nbt.getInt("page");

				if (page == 0) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

					staff.openInventory(new MainMenu(staff, player).getMenu());
				} else {
					OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
					String logType = nbt.getString("logType");

					staff.openInventory(new RollbackListMenu(staff, player, logType, page).showBackups());
				}
			}	
		} else if (!e.isShiftClick()) {
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void onBackupMenuClick(InventoryClickEvent e) {
		if (!e.getView().getTopInventory().getName().equals(InventoryName.BACKUP.getName()))
			return;
		
		e.setCancelled(true);

		final Player staff = (Player) e.getWhoClicked();
		if (!staff.hasPermission("inventoryrollback.restore"))
			return;

		if (e.getClickedInventory() == null) {
			e.setCancelled(false);
			return;
		}

		ItemStack currentItem = e.getCurrentItem();
		Messages messages = new Messages();

		if ((e.getRawSlot() >= 45 && e.getRawSlot() < 54) && e.getClickedInventory().getName().equals(InventoryName.BACKUP.getName())) {
			NBT nbt = new NBT(currentItem);
			if (!nbt.hasUUID())
				return;

			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
			String logType = nbt.getString("logType");
			Long timestamp = nbt.getLong("timestamp");
			
			PlayerData data = new PlayerData(offlinePlayer, logType);		
			FileConfiguration playerData = data.getData();

			RestoreInventory restore = new RestoreInventory();

			if (currentItem.getType().equals(Material.BANNER)) {
				//Click on back button
				staff.openInventory(new RollbackListMenu(staff, offlinePlayer, logType, 1).showBackups());
			} else if (currentItem.getType().equals(Material.ENDER_CHEST)) {
				//Clicked Ender Chest

				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	

					ItemStack[] enderchest = restore.retrieveEnderChestInventory(playerData, timestamp);

					if (emptyEnderChest(player)) {
						player.getEnderChest().setContents(enderchest);

						if (Sounds.enderChestEnabled)
							player.playSound(player.getLocation(), Sounds.enderChest, Sounds.enderChestVolume, 1);
					} else {
						staff.sendMessage(Messages.pluginName + messages.enderChestNotEmpty(player.getName()));
						e.setCancelled(true);
						return;
					}

					staff.sendMessage(Messages.pluginName + messages.enderChestRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(Messages.pluginName + messages.enderChestRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(Messages.pluginName + messages.enderChestNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(Material.MELON)) {
				//Clicked health button

				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	
					Double health = nbt.getDouble("health");

					player.setHealth(health);

					if (Sounds.foodEnabled)
						player.playSound(player.getLocation(), Sounds.food, Sounds.foodVolume, 1);

					staff.sendMessage(Messages.pluginName + messages.healthRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(Messages.pluginName + messages.healthRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(Messages.pluginName + messages.healthNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(Material.ROTTEN_FLESH)) {
				//Clicked hunger button	
				
				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;	
					int hunger = nbt.getInt("hunger");
					Float saturation = nbt.getFloat("saturation");

					player.setFoodLevel(hunger);
					player.setSaturation(saturation);

					if (Sounds.hungerEnabled)
						player.playSound(player.getLocation(), Sounds.hunger, Sounds.hungerVolume, 1);

					staff.sendMessage(Messages.pluginName + messages.hungerRestored(player.getName()));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(Messages.pluginName + messages.hungerRestoredPlayer(staff.getName()));
				} else {
					staff.sendMessage(Messages.pluginName + messages.hungerNotOnline(offlinePlayer.getName()));
				}
			} else if (currentItem.getType().equals(Material.EXP_BOTTLE)) {
				//Clicked XP button
				
				if (offlinePlayer.isOnline()) {				
					Player player = (Player) offlinePlayer;	
					Float xp = nbt.getFloat("xp");

					restore.setTotalExperience(player, xp);

					if (Sounds.experienceEnabled)
						player.playSound(player.getLocation(), Sounds.experience, Sounds.experienceVolume, 1);

					staff.sendMessage(Messages.pluginName + messages.experienceRestored(player.getName(), (int) new RestoreInventory().getLevel(xp)));
					if (!staff.getUniqueId().equals(player.getUniqueId()))
						player.sendMessage(Messages.pluginName + messages.experienceRestoredPlayer(staff.getName(), xp.intValue()));
				} else {
					staff.sendMessage(Messages.pluginName + messages.experienceNotOnline(offlinePlayer.getName()));
				}
			}
		} else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	private void blockMenuDrags(InventoryDragEvent e) {
		if (!e.getInventory().getName().equals(InventoryName.MAIN_MENU.getName()) && !e.getInventory().getName().equals(InventoryName.ROLLBACK_LIST.getName()))
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