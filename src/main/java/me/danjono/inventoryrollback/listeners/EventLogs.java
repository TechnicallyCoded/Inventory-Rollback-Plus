package me.danjono.inventoryrollback.listeners;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.discord.DiscordWebhook;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventLogs implements Listener {

	private InventoryRollbackPlus main;
	private Map<UUID, SaveInventory.PlayerDataSnapshot> inventoryCache;

	public EventLogs() {
		this.main = InventoryRollbackPlus.getInstance();
		this.inventoryCache = new ConcurrentHashMap<>();
	}

	public static void patchLowestHandlers() {
		// Fix for LOWEST priority handlers.
		// We move the handlers to the end of the list such that it runs after our handler
		HandlerList deathEventHandlers = PlayerDeathEvent.getHandlerList();
		List<RegisteredListener> otherDeathHandlers = new ArrayList<>();

		for (RegisteredListener handler : deathEventHandlers.getRegisteredListeners()) {
			// Ignore and non-LOWEST priority handlers
			if (handler.getPriority() != EventPriority.LOWEST) continue;
			// Ignore our own listener
			if (handler.getListener().getClass() == EventLogs.class) continue;
			otherDeathHandlers.add(handler);
		}

		// Shift all the handlers to the end of the list, in order
		for (RegisteredListener handler : otherDeathHandlers) {
			deathEventHandlers.unregister(handler);
			deathEventHandlers.register(handler);
		}

		deathEventHandlers.bake();
	}

	@EventHandler
	private void playerJoin(PlayerJoinEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();
		if (player.hasPermission("inventoryrollbackplus.joinsave")) {
			new SaveInventory(e.getPlayer(), LogType.JOIN, null, null)
					.snapshotAndSave(player.getInventory(), player.getEnderChest(), true);

			// Send Discord webhook for backup creation
			try {
				String timestamp = ConfigData.getTimeFormat().format(System.currentTimeMillis());
				DiscordWebhook.sendBackupCreated(player.getName(), "JOIN", timestamp);
			} catch (Exception ex) {
				if (ConfigData.isDebugEnabled()) {
					main.getLogger().warning("Failed to send Discord webhook for join backup: " + ex.getMessage());
				}
			}
		}
		if (player.hasPermission("inventoryrollbackplus.adminalerts")) {
			// can send info to admins here
		}
	}

	@EventHandler
	private void playerQuit(PlayerQuitEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollbackplus.leavesave")) {
			new SaveInventory(e.getPlayer(), LogType.QUIT, null, null)
					.snapshotAndSave(player.getInventory(), player.getEnderChest(), true);

			// Send Discord webhook for backup creation
			try {
				String timestamp = ConfigData.getTimeFormat().format(System.currentTimeMillis());
				DiscordWebhook.sendBackupCreated(player.getName(), "QUIT", timestamp);
			} catch (Exception ex) {
				if (ConfigData.isDebugEnabled()) {
					main.getLogger().warning("Failed to send Discord webhook for quit backup: " + ex.getMessage());
				}
			}
		}

		UUID uuid = player.getUniqueId();

		// Run the cleanup 1 tick later in case the rate limiter should need to provide debug data.
		// If the cleanup would run and the event is being spammed, this cleanup would delete the rate limiter's data
		// before it has a chance to act.
		main.getServer().getScheduler().runTaskLater(main, () -> {
			// Double check that the player is offline
			if (main.getServer().getPlayer(uuid) != null) return;
			// Cleanup the player's data
			SaveInventory.cleanup(uuid);
		}, 1);
	}

	/**
	 * Save the player's inventory before death.
	 * @param event Bukkit damage event
	 */
    @EventHandler(priority = EventPriority.LOWEST)
	public void playerPreDeath(EntityDamageEvent event) {
		// Only run if other plugins are not allowed to edit the death inventory (early event listen)
		if (ConfigData.isAllowOtherPluginEditDeathInventory()) return;

		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();

		// Not death? Don't make a snapshot & remove any old ones to prevent false-positives
		if (!isDeathDamage(event)) {
			this.inventoryCache.remove(uuid);
			return;
		}

		SaveInventory saveInventory = new SaveInventory(player, LogType.DEATH, event.getCause(), null);
		SaveInventory.PlayerDataSnapshot snapshot = saveInventory.createSnapshot(player.getInventory(), player.getEnderChest());

		this.inventoryCache.put(uuid, snapshot);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void playerPreDeathCheck(EntityDamageEvent event) {
		// Only run if other plugins are not allowed to edit the death inventory (early event listen)
		if (ConfigData.isAllowOtherPluginEditDeathInventory()) return;

		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();

		// Other plugins may cancel or edit the damage on the event between LOWEST and now MONITOR.
		// Let's make sure we don't keep our snapshot if that's the case.
		if (event.isCancelled() || event.getFinalDamage() == 0) {
			// Remove our temporary snapshot. This will also prevent further checks below from succeeding.
			this.inventoryCache.remove(uuid);
			return;
		}

		SaveInventory.PlayerDataSnapshot firstSnapshot = this.inventoryCache.get(uuid);
		if (firstSnapshot == null) return;

		SaveInventory saveInventory = new SaveInventory(player, LogType.DEATH, event.getCause(), null);
		SaveInventory.PlayerDataSnapshot lastSnapshot = saveInventory.createSnapshot(player.getInventory(), player.getEnderChest());

		// Inventory was not edited during a damage event, we don't need this hacky snapshot
		if (firstSnapshot.equals(lastSnapshot)) {
			this.inventoryCache.remove(uuid);
			return;
		}

		// If the inventory was edited, warn
		InventoryRollbackPlus.getInstance().getLogger().warning(
				player.getName() + "'s inventory was edited during damage handling (instead of death, this is bad). " +
						"Please find which plugin is doing this by disabling one plugin at the time " +
						"(or use \"binary search\" if you know how) until this message disappears!"
		);
	}

	/**
	 * Handle saving the player's inventory on death. (Early event listen)
	 * @param event Bukkit damage event
	 */
    @EventHandler(priority = EventPriority.LOWEST)
	public void playerDeathEarly(PlayerDeathEvent event) {
		// Only run if other plugins are not allowed to edit the death inventory (early event listen)
		if (ConfigData.isAllowOtherPluginEditDeathInventory()) return;

		playerDeathHandle(event);
	}

	/**
	 * Handle saving the player's inventory on death. (Late event listen)
	 * @param event Bukkit damage event
	 */
    @EventHandler(priority = EventPriority.MONITOR)
	public void playerDeathLate(PlayerDeathEvent event) {
		// Only run if other plugins are allowed to edit the death inventory (late event listen)
		if (!ConfigData.isAllowOtherPluginEditDeathInventory()) return;

		playerDeathHandle(event);
	}

	public void playerDeathHandle(PlayerDeathEvent event) {
        // Sanity checks to prevent unwanted saves
        if (!ConfigData.isEnabled()) return;

        Player player = event.getEntity();

		// Check that the player has the permission for inventory saves
        if (player.hasPermission("inventoryrollbackplus.deathsave")) {

            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
			DetailedReason detailedReason = getDetailedReason(damageEvent);

			// After all checks, create the save with data provided above
			SaveInventory saveInventory = new SaveInventory(player, LogType.DEATH, detailedReason.damageCause, detailedReason.reason);

			UUID uuid = player.getUniqueId();
			SaveInventory.PlayerDataSnapshot preSnapshot = this.inventoryCache.get(uuid);

			if (preSnapshot == null) {
				saveInventory.snapshotAndSave(player.getInventory(), player.getEnderChest(), true);
			} else {
				// Save the snapshot inventory instead of the current one. We apparently had an edit
				// during the damage event.
				saveInventory.save(preSnapshot, true);
				// Remove the snapshot from the cache
				this.inventoryCache.remove(uuid);
			}

			// Send Discord webhook for player death
			try {
				String deathCause = detailedReason.reason != null ? detailedReason.reason : detailedReason.damageCause.name();
				String timestamp = ConfigData.getTimeFormat().format(System.currentTimeMillis());
				DiscordWebhook.sendPlayerDeath(
					player.getName(),
					player.getLocation(),
					deathCause,
					timestamp
				);
			} catch (Exception e) {
				if (ConfigData.isDebugEnabled()) {
					main.getLogger().warning("Failed to send Discord webhook for player death: " + e.getMessage());
				}
			}
        }
    }

	@EventHandler
	private void playerChangeWorld(PlayerChangedWorldEvent e) {
		if (!ConfigData.isEnabled()) return;

		Player player = e.getPlayer();

		if (player.hasPermission("inventoryrollbackplus.worldchangesave")) {
			new SaveInventory(e.getPlayer(), LogType.WORLD_CHANGE, null, null)
					.snapshotAndSave(player.getInventory(), player.getEnderChest(), true);

			// Send Discord webhook for backup creation
			try {
				String timestamp = ConfigData.getTimeFormat().format(System.currentTimeMillis());
				DiscordWebhook.sendBackupCreated(player.getName(), "WORLD_CHANGE", timestamp);
			} catch (Exception ex) {
				if (ConfigData.isDebugEnabled()) {
					main.getLogger().warning("Failed to send Discord webhook for world change backup: " + ex.getMessage());
				}
			}
		}
	}

	public boolean isEntityCause(EntityDamageEvent.DamageCause cause) {
		if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) ||
				cause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) return true;
		if (this.main.getVersion().greaterOrEqThan(BukkitVersion.v1_11_R1)) {
			if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return true;
		}
		return false;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isDeathDamage(EntityDamageEvent event) {
		// This only checks damage and doesn't take into account potential cancellation reasons such
		// as plugins or totems of undying. Useless SaveInventory objects will be created (not saved)
		// but this prevents other plugins from interfering with the death save.

		if (!(event.getEntity() instanceof LivingEntity)) return false;
		LivingEntity living = (LivingEntity) event.getEntity();

		return event.getFinalDamage() >= living.getHealth();
	}

	private @NotNull DetailedReason getDetailedReason(EntityDamageEvent damageEvent) {
		EntityDamageEvent.DamageCause damageCause;

		if (damageEvent == null) damageCause = EntityDamageEvent.DamageCause.CUSTOM;
		else damageCause = damageEvent.getCause();

		// Detailed reason for the death that can be applied given certain conditions
		String reason = null;

		// Handler the case where the death is caused by an entity
		if (isEntityCause(damageCause) && damageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
			Entity damager = damageByEntityEvent.getDamager();

			// Get the shooter's name if the killing entity is a projectile
			String shooterName = "";
			if (damager instanceof Projectile) {

				Projectile proj = (Projectile) damager;
				ProjectileSource shooter = proj.getShooter();

				// Show shooter name if it's a living entity
				if (shooter instanceof LivingEntity) {
					LivingEntity shooterEntity = (LivingEntity) shooter;
					shooterName = ", " + shooterEntity.getName();
				}
				// Show shooter block type if it's a block projectile source
				else if (shooter instanceof BlockProjectileSource) {
					BlockProjectileSource shooterBlock = (BlockProjectileSource) shooter;
					shooterName = ", " + shooterBlock.getBlock().getType().name();

				}
				// In all other cases, don't show projectile detailed shooter info
			}

			// Create a more specific reason given the data above
			reason = damageCause.name() + " (" + damageByEntityEvent.getDamager().getName() + shooterName + ")";
		}
		DetailedReason detailedReason = new DetailedReason(damageCause, reason);
		return detailedReason;
	}

	private static class DetailedReason {
		public final EntityDamageEvent.DamageCause damageCause;
		public final String reason;

		public DetailedReason(EntityDamageEvent.DamageCause damageCause, String reason) {
			this.damageCause = damageCause;
			this.reason = reason;
		}
	}

}
