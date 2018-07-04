package me.danjono.inventoryrollback.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.Messages;
import me.danjono.inventoryrollback.config.PlayerData;

public class RollbackListMenu {
	
	private Player staff;
	private UUID playerUUID;
	private String logType;
	private int pageNumber;
	
	private FileConfiguration playerData;
	
	public RollbackListMenu(Player staff, OfflinePlayer player, String logType, int pageNumber) {
		this.staff = staff;
		this.playerUUID = player.getUniqueId();
		this.logType = logType;
		this.pageNumber = pageNumber;
		
		this.playerData = new PlayerData(this.playerUUID, this.logType).getData();
	}
		
	public Inventory showBackups() {						
		Inventory backupMenu = Bukkit.createInventory(staff, 45, InventoryName.ROLLBACK_LIST.getName());	
		
		Buttons buttons = new Buttons();
		Messages messages = new Messages();
					
		//Check how many backups there are in total
		int backups = 0;
		List<Long> timeStamps = new ArrayList<Long>();
		
		for (String time : playerData.getConfigurationSection("data").getKeys(false)) {
			backups++;
			timeStamps.add(Long.parseLong(time));
		}
		
		Collections.reverse(timeStamps);
		
		//How many rows are required
		int spaceRequired = 36;
		
		//How many pages are required
		int pagesRequired = (int) Math.ceil(backups / (double) spaceRequired);
		
		//Check if pageNumber supplied is greater then pagesRequired, if true set to last page
		if (pageNumber > pagesRequired) {
			pageNumber = pagesRequired;
		} else if (pageNumber <= 0) {
			pageNumber = 1;
		}
		
		int position = 0;
		for (int i = 0; i < spaceRequired; i++) {							
			try {
				Long time = timeStamps.get(((pageNumber - 1) * spaceRequired) + i);
				
				String deathReason = null;
				try {
					deathReason = messages.deathReason(playerData.getString("data." + time + ".deathReason"));
				} catch (NullPointerException e) {}
				
				String displayName = messages.deathTime(getTime(time));
								
				List<String> lore = new ArrayList<String>();
				if (deathReason != null)
					lore.add(deathReason);
				
				String world = playerData.getString("data." + time + ".location.world");
				String x = playerData.getString("data." + time + ".location.x");
				String y = playerData.getString("data." + time + ".location.y");
				String z = playerData.getString("data." + time + ".location.z");
				String location = world + "," + x + "," + y + "," + z;
				
				lore.add(messages.deathLocationWorld(world));
				lore.add(messages.deathLocationX(x));
				lore.add(messages.deathLocationY(y));
				lore.add(messages.deathLocationZ(z));
				
				ItemStack inventory = buttons.createInventoryButton(new ItemStack(Material.CHEST), playerUUID, logType, location, time, displayName, lore);
				
				backupMenu.setItem(position, inventory);
				
			} catch (IndexOutOfBoundsException e) {}
			
			position++;
		}
				
		List<String> lore = new ArrayList<String>();
		
		if (pageNumber == 1) {
			ItemStack mainMenu = buttons.backButton(Messages.mainMenuButton, playerUUID, logType, 0, null);						
			backupMenu.setItem(position + 1, mainMenu);
		}		
		
		if (pageNumber > 1) {
			lore.add("Page " + (pageNumber - 1));
			ItemStack previousPage = buttons.backButton(Messages.previousPageButton, playerUUID, logType, pageNumber - 1, lore);
						
			backupMenu.setItem(position + 1, previousPage);
			lore.clear();
		}
		
		if (pageNumber < pagesRequired) {
			lore.add("Page " + (pageNumber + 1));
			ItemStack nextPage = buttons.nextButton(Messages.nextPageButton, playerUUID, logType, pageNumber + 1, lore);
						
			backupMenu.setItem(position + 7, nextPage);
			lore.clear();
		}
		
		return backupMenu;
	}
	
	private static String getTime(Long time) {
	    SimpleDateFormat sdf = new SimpleDateFormat(ConfigFile.timeFormat);
	    sdf.setTimeZone(TimeZone.getTimeZone(ConfigFile.timeZone));
	    return sdf.format(new Date(time));
	}
	
}
