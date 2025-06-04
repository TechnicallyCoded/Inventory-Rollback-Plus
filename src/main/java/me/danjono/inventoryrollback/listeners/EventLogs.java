package me.danjono.inventoryrollback.listeners;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.entity.*;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventLogs implements Listener {

    private final InventoryRollbackPlus main;
    private final Map<UUID, SaveInventory.PlayerDataSnapshot> inventoryCache;
    private static final Set<EntityDamageEvent.DamageCause> ENTITY_CAUSES = createEntityCauses();

    public EventLogs() {
        this.main = InventoryRollbackPlus.getInstance();
        this.inventoryCache = new ConcurrentHashMap<>();
    }

    private static Set<EntityDamageEvent.DamageCause> createEntityCauses() {
        Set<EntityDamageEvent.DamageCause> causes = EnumSet.of(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE
        );
        if (BukkitVersion.v1_11_R1.isSupported()) {
            causes.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        }
        return Collections.unmodifiableSet(causes);
    }

    public static void patchLowestHandlers() {
        HandlerList deathEventHandlers = PlayerDeathEvent.getHandlerList();
        List<RegisteredListener> lowestPriorityHandlers = new ArrayList<>();

        for (RegisteredListener handler : deathEventHandlers.getRegisteredListeners()) {
            if (handler.getPriority() != EventPriority.LOWEST) continue;
            if (handler.getListener().getClass() == EventLogs.class) continue;
            lowestPriorityHandlers.add(handler);
        }

        for (RegisteredListener handler : lowestPriorityHandlers) {
            deathEventHandlers.unregister(handler);
            deathEventHandlers.register(handler);
        }

        deathEventHandlers.bake();
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        if (!ConfigData.isEnabled()) return;

        Player player = event.getPlayer();
        if (hasPermission(player, "inventoryrollbackplus.joinsave")) {
            createSave(player, LogType.JOIN);
        }
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        if (!ConfigData.isEnabled()) return;

        Player player = event.getPlayer();
        if (hasPermission(player, "inventoryrollbackplus.leavesave")) {
            createSave(player, LogType.QUIT);
        }

        scheduleCleanup(player);
    }

    private void createSave(Player player, LogType logType) {
        new SaveInventory(player, logType, null, null)
            .snapshotAndSave(player.getInventory(), player.getEnderChest(), true);
    }

    private void scheduleCleanup(Player player) {
        UUID uuid = player.getUniqueId();
        main.getServer().getScheduler().runTaskLater(main, () -> {
            if (main.getServer().getPlayer(uuid) != null) return;
            SaveInventory.cleanup(uuid);
        }, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPreDeath(EntityDamageEvent event) {
        if (ConfigData.isAllowOtherPluginEditDeathInventory()) return;
        if (!(event.getEntity() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        if (!isDeathDamage(event)) {
            inventoryCache.remove(uuid);
            return;
        }

        SaveInventory saveInventory = new SaveInventory(player, LogType.DEATH, event.getCause(), null);
        inventoryCache.put(uuid, saveInventory.createSnapshot(
            player.getInventory(), player.getEnderChest()
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerPreDeathCheck(EntityDamageEvent event) {
        if (ConfigData.isAllowOtherPluginEditDeathInventory()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCancelled() || event.getFinalDamage() == 0) {
            inventoryCache.remove(player.getUniqueId());
            return;
        }

        SaveInventory.PlayerDataSnapshot snapshot = inventoryCache.get(player.getUniqueId());
        if (snapshot == null) return;

        SaveInventory saveInventory = new SaveInventory(player, LogType.DEATH, event.getCause(), null);
        SaveInventory.PlayerDataSnapshot newSnapshot = saveInventory.createSnapshot(
            player.getInventory(), player.getEnderChest()
        );

        if (!snapshot.equals(newSnapshot)) {
            main.getLogger().warning(
                player.getName() + "'s inventory was edited during damage handling. " +
                "Please identify the conflicting plugin!"
            );
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerDeathEarly(PlayerDeathEvent event) {
        if (!ConfigData.isAllowOtherPluginEditDeathInventory()) {
            handlePlayerDeath(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDeathLate(PlayerDeathEvent event) {
        if (ConfigData.isAllowOtherPluginEditDeathInventory()) {
            handlePlayerDeath(event);
        }
    }

    private void handlePlayerDeath(PlayerDeathEvent event) {
        if (!ConfigData.isEnabled()) return;
        
        Player player = event.getEntity();
        if (!hasPermission(player, "inventoryrollbackplus.deathsave")) return;

        EntityDamageEvent damageEvent = player.getLastDamageCause();
        DetailedReason detailedReason = getDetailedReason(damageEvent);

        UUID uuid = player.getUniqueId();
        SaveInventory saveInventory = new SaveInventory(
            player, LogType.DEATH, detailedReason.damageCause, detailedReason.reason
        );

        SaveInventory.PlayerDataSnapshot snapshot = inventoryCache.get(uuid);
        if (snapshot != null) {
            saveInventory.save(snapshot, true);
            inventoryCache.remove(uuid);
        } else {
            saveInventory.snapshotAndSave(
                player.getInventory(), player.getEnderChest(), true
            );
        }
    }

    @EventHandler
    private void playerChangeWorld(PlayerChangedWorldEvent event) {
        if (!ConfigData.isEnabled()) return;
        
        Player player = event.getPlayer();
        if (hasPermission(player, "inventoryrollbackplus.worldchangesave")) {
            createSave(player, LogType.WORLD_CHANGE);
        }
    }

    private boolean isDeathDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return false;
        return event.getFinalDamage() >= entity.getHealth();
    }

    private DetailedReason getDetailedReason(EntityDamageEvent damageEvent) {
        EntityDamageEvent.DamageCause cause = (damageEvent != null) ? 
            damageEvent.getCause() : EntityDamageEvent.DamageCause.CUSTOM;

        String reason = null;
        if (ENTITY_CAUSES.contains(cause) && damageEvent instanceof EntityDamageByEntityEvent entityEvent) {
            reason = buildDetailedReason(entityEvent);
        }
        
        return new DetailedReason(cause, reason);
    }

    private String buildDetailedReason(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        StringBuilder reasonBuilder = new StringBuilder(event.getCause().name())
            .append(" (")
            .append(damager.getName());

        if (damager instanceof Projectile projectile) {
            appendShooterInfo(reasonBuilder, projectile);
        }

        return reasonBuilder.append(')').toString();
    }

    private void appendShooterInfo(StringBuilder builder, Projectile projectile) {
        ProjectileSource shooter = projectile.getShooter();
        if (shooter instanceof LivingEntity living) {
            builder.append(", ").append(living.getName());
        } else if (shooter instanceof BlockProjectileSource blockSource) {
            builder.append(", ").append(blockSource.getBlock().getType());
        }
    }

    private boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    private record DetailedReason(
        EntityDamageEvent.DamageCause damageCause, 
        String reason
    ) {}
}
