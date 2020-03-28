package me.danjono.inventoryrollback.gui;

import me.danjono.inventoryrollback.config.ConfigData;

public enum InventoryName {
	
    MAIN_MENU("Inventory Rollback", 36),
	PLAYER_MENU("Player Data", 9),
	ROLLBACK_LIST("Rollbacks", ConfigData.getBackupLinesVisible() * 9 + 9),
	MAIN_BACKUP("Main Inventory Backup", 54),
    ENDER_CHEST_BACKUP("Ender Chest Backup", 36);
	
	private final String menuName;
	private final int size;
	
	private InventoryName(String name, int size) {
		this.menuName = name;
		this.size = size;
	}
	
	public String getName() {
		return this.menuName;
	}
	
	public int getSize() {
	    return this.size;
	}

}
