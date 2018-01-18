package me.danjono.inventoryrollback.gui;

public enum InventoryName {
	
	MAIN_MENU("Inventory Rollback"),
	ROLLBACK_LIST("Rollbacks"),
	BACKUP("Backup");
	
	private final String menuName;
	
	private InventoryName(String name) {
		this.menuName = name;
	}
	
	public String getName() {
		return menuName;
	}

}
