package me.danjono.inventoryrollback.listeners;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class EventLogs implements Listener {

    @EventHandler
    private void playerJoin(PlayerJoinEvent e) {
        if (!ConfigFile.enabled) return;

        Player player = e.getPlayer();
        if (player.hasPermission("inventoryrollback.joinsave")) {
            final UUID uuid = player.getUniqueId();
            final CompletableFuture<?> future = new SaveInventory(e.getPlayer(), LogType.JOIN, null, player.getInventory(), player.getEnderChest()).saveToDiskAsync();
            long start = System.currentTimeMillis();
            Bukkit.getScheduler().runTaskLater(InventoryRollback.getInstance(), () -> {
                long elapsed = System.currentTimeMillis() - start;
                if (elapsed > ConfigFile.maxMillisAsyncLoadTime && !future.isDone()) {
                    final Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.kickPlayer(MessageData.inventoryLoadFailKickMessage);
                        InventoryRollback.getInstance().getLogger().log(Level.WARNING, "Failed to load inventory for  " + p.getName() + "! Time elapsed: " + elapsed + "ms | Max: " + ConfigFile.maxMillisAsyncLoadTime + "ms");
                    }
                }
                // Check if the delay has passed ~ 5 ticks after it should have.
            }, (ConfigFile.maxMillisAsyncLoadTime / 50) + 5);
        }
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent e) {
        if (!ConfigFile.enabled) return;

        Player player = e.getPlayer();

        if (player.hasPermission("inventoryrollback.leavesave")) {
            new SaveInventory(e.getPlayer(), LogType.QUIT, null, player.getInventory(), player.getEnderChest()).saveToDiskAsync();
        }
    }

    @EventHandler
    private void playerDeath(EntityDamageEvent e) {
        if (!ConfigFile.enabled) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (player.getHealth() - e.getDamage() <= 0 && player.hasPermission("inventoryrollback.deathsave")) {
            new SaveInventory(player, LogType.DEATH, e.getCause(), player.getInventory(), player.getEnderChest()).saveToDiskAsync();
        }
    }

    @EventHandler
    private void playerChangeWorld(PlayerChangedWorldEvent e) {
        if (!ConfigFile.enabled) return;

        Player player = e.getPlayer();

        if (player.hasPermission("inventoryrollback.worldchangesave")) {
            new SaveInventory(e.getPlayer(), LogType.WORLD_CHANGE, null, player.getInventory(), player.getEnderChest()).saveToDiskAsync();
        }
    }

}
