package me.danjono.inventoryrollback.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class EventLogs implements Listener {
	
	@EventHandler
	private void playerJoin(PlayerJoinEvent e) {
		if (!ConfigFile.enabled) return;
		
		Player player = e.getPlayer();
		
		if (player.hasPermission("inventoryrollback.joinsave")) {			
			new SaveInventory().createSave(e.getPlayer(), "JOIN", null);
		}
	}
	
	@EventHandler
	private void playerQuit(PlayerQuitEvent e) {
		if (!ConfigFile.enabled) return;
		
		Player player = e.getPlayer();
		
		if (player.hasPermission("inventoryrollback.leavesave")) {				
			new SaveInventory().createSave(e.getPlayer(), "QUIT", null);
		}
	}
	
	@EventHandler
	private void playerDeath(EntityDamageEvent e) {
		if (!ConfigFile.enabled) return;
		if (!(e.getEntity() instanceof Player)) return;

		Player player = (Player) e.getEntity();
		
		if (player.getHealth() - e.getDamage() <= 0 && player.hasPermission("inventoryrollback.deathsave")) {											
			new SaveInventory().createSave(player, "DEATH", e.getCause().name());
		}
	}
	
	@EventHandler
	private void playerDeath(PlayerChangedWorldEvent e) {
		if (!ConfigFile.enabled) return;

		Player player = e.getPlayer();
		
		if (player.hasPermission("inventoryrollback.worldchangesave")) {				
			new SaveInventory().createSave(e.getPlayer(), "WORLDCHANGE", null);
		}
	}
	
}
