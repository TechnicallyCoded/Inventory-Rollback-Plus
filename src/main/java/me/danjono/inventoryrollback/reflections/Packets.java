package me.danjono.inventoryrollback.reflections;

import org.bukkit.entity.Player;

import me.danjono.inventoryrollback.InventoryRollback;

public class Packets {
	
	public void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Class<?> getNMSClass(String name) {
		Class<?> c = null;
		
		try {
			c = Class.forName("net.minecraft.server." + InventoryRollback.packageVersion + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return c;
	}
	
	public Class<?> getCraftBukkitClass(String name) {
		Class<?> c = null;
		
		try {
			c = Class.forName("org.bukkit.craftbukkit." + InventoryRollback.packageVersion + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return c;
	}
}