package me.danjono.inventoryrollback.listeners;

import me.danjono.inventoryrollback.InventoryRollback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class EventLogs implements Listener {

	@EventHandler
	private void playerJoin(PlayerJoinEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();
		if (player.hasPermission("inventoryrollbackplus.joinsave") || player.hasPermission("inventoryrollback.joinsave")) {
			new SaveInventory(e.getPlayer(), LogType.JOIN, null, null, player.getInventory(), player.getEnderChest()).createSave();
		}
	}

	@EventHandler
	private void playerQuit(PlayerQuitEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollbackplus.leavesave") || player.hasPermission("inventoryrollback.leavesave")) {
			new SaveInventory(e.getPlayer(), LogType.QUIT, null, null, player.getInventory(), player.getEnderChest()).createSave();
		}
	}

	@EventHandler
	private void playerDeath(EntityDamageEvent e) {
		if (!ConfigData.isEnabled()) return;
		if (!(e.getEntity() instanceof Player)) return;
		if (isEntityCause(e.getCause())) return;

		Player player = (Player) e.getEntity();

		if (player.getHealth() - e.getFinalDamage() <= 0 && (player.hasPermission("inventoryrollbackplus.deathsave") || player.hasPermission("inventoryrollback.deathsave"))) {
			new SaveInventory(player, LogType.DEATH, e.getCause(), null, player.getInventory(), player.getEnderChest()).createSave();
		}
	}

	@EventHandler
	public void playerDeathByEntity(EntityDamageByEntityEvent e) {
		if (!ConfigData.isEnabled()) return;
		if (!(e.getEntity() instanceof Player)) return;
		if (!isEntityCause(e.getCause())) return;

		Player player = (Player) e.getEntity();

		if (player.getHealth() - e.getFinalDamage() <= 0 && (player.hasPermission("inventoryrollbackplus.deathsave") || player.hasPermission("inventoryrollback.deathsave"))) {
			String reason = e.getCause().name() + " (" + e.getDamager().getName() + ")";
			new SaveInventory(player, LogType.DEATH, e.getCause(), reason, player.getInventory(), player.getEnderChest()).createSave();
		}

	}

	@EventHandler
	private void playerChangeWorld(PlayerChangedWorldEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollbackplus.worldchangesave") || player.hasPermission("inventoryrollback.worldchangesave")) {
			new SaveInventory(e.getPlayer(), LogType.WORLD_CHANGE, null, null, player.getInventory(), player.getEnderChest()).createSave();
		}
	}

	public boolean isEntityCause(EntityDamageEvent.DamageCause cause) {
		if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ||
				cause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) return true;
		if (!InventoryRollback.getInstance().getVersion().equals(InventoryRollback.VersionName.V1_8)) {
			if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return true;
		}
		return false;
	}

}
