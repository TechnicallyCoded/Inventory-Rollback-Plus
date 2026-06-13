package com.nuclyon.technicallycoded.inventoryrollback.folia;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus.usingFolia;

/**
 * Utility class for scheduling tasks in a Paper/Folia server.
 */
public abstract class SchedulerUtils {

    /**
     * Schedules a task to run later on the main server thread.
     * @param loc The location where the task should run, or null for the main thread.
     * @param task   The task to run.
     * @param delay  The delay in ticks before the task runs.
     */
    public static void runTaskLater(@Nullable Location loc, @NotNull Runnable task, long delay) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    regionScheduler.runDelayed(
                            plugin,
                            loc,
                            (ScheduledTask scheduledTask) -> task.run(),
                            delay
                    );
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    globalScheduler.runDelayed(
                            plugin,
                            (ScheduledTask scheduledTask) -> task.run(),
                            delay
                    );
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * Schedules a task to run later asynchronously on the main server thread.
     * @param loc The location where the task should run, or null for the main thread.
     * @param task   The task to run.
     * @param delay  The delay in ticks before the task runs.
     */
    public static void runTaskLaterAsynchronously(@Nullable Location loc, @NotNull Runnable task, long delay) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                // Use the async scheduler not a region/global tick thread!
                // the async scheduler is time based so convert ticks to milliseconds.
                Method getAsyncScheduler = plugin.getServer().getClass().getMethod("getAsyncScheduler");
                AsyncScheduler asyncScheduler = (AsyncScheduler) getAsyncScheduler.invoke(plugin.getServer());
                if (delay <= 0) {
                    asyncScheduler.runNow(plugin, (ScheduledTask scheduledTask) -> task.run());
                } else {
                    asyncScheduler.runDelayed(
                            plugin,
                            (ScheduledTask scheduledTask) -> task.run(),
                            delay * 50L,
                            TimeUnit.MILLISECONDS
                    );
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, task, delay);
    }

    /**
     * Schedules a task to run repeatedly on the main server thread.
     * @param loc The location where the task should run, or null for the main thread.
     * @param runnable   The BukkitRunnable to run.
     * @param delay  The delay in ticks before the task runs.
     * @param period The period in ticks between subsequent runs of the task.
     */
    public static void runTaskTimer(@Nullable Location loc, @NotNull FoliaRunnable runnable, long delay, long period) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                ScheduledTask task;
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    task = regionScheduler.runAtFixedRate(
                            plugin,
                            loc,
                            (ScheduledTask t) -> runnable.run(),
                            delay,
                            period
                    );
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    task = globalScheduler.runAtFixedRate(
                            plugin,
                            (ScheduledTask t) -> runnable.run(),
                            delay,
                            period
                    );
                }
                runnable.setScheduledTask(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        runnable.runTaskTimer(plugin, delay, period);
    }

    /**
     * Schedules a task to run repeatedly on the main server thread asynchronously.
     * @param runnable The FoliaRunnable to run.
     * @param delay  The delay in ticks before the task runs.
     * @param period The period in ticks between subsequent runs of the task.
     */
    public static void runTaskTimerAsynchronously(@NotNull FoliaRunnable runnable, long delay, long period) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                // True asyncronous repeating task via the async scheduler (time based)
                // requires strictly positive initial delay/period, so convert ticks to ms and clamp to a minimum of one tick
                Method getAsyncScheduler = plugin.getServer().getClass().getMethod("getAsyncScheduler");
                AsyncScheduler asyncScheduler = (AsyncScheduler) getAsyncScheduler.invoke(plugin.getServer());
                long initialDelayMs = Math.max(1L, delay) * 50L;
                long periodMs = Math.max(1L, period) * 50L;
                ScheduledTask task = asyncScheduler.runAtFixedRate(
                        plugin,
                        (ScheduledTask t) -> runnable.run(),
                        initialDelayMs,
                        periodMs,
                        TimeUnit.MILLISECONDS
                );
                runnable.setScheduledTask(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        runnable.runTaskTimerAsynchronously(plugin, delay, period);
    }

    /**
     * Runs a task asynchronously on the main server thread.
     * @param task The task to run.
     */
    public static void runTaskAsynchronously(@NotNull Runnable task) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                // Must use the async scheduler NOT the global region scheduler
                // running here lets callers safely block (eg CompletableFuture.get()) without
                // freezing the global region. Using the global scheduler caused a self deadlock
                // whenever a task here blocked on a future completed by another such "async" task
                Method getAsyncScheduler = plugin.getServer().getClass().getMethod("getAsyncScheduler");
                AsyncScheduler asyncScheduler = (AsyncScheduler) getAsyncScheduler.invoke(plugin.getServer());
                asyncScheduler.runNow(plugin, (ScheduledTask t) -> task.run());
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a task on the main server thread at a specific location or globally.
     * @param loc The location where the task should run, or null for the main thread.
     * @param task The task to run.
     */
    public static void runTask(@Nullable Location loc, @NotNull Runnable task) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            try {
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    regionScheduler.execute(plugin, loc, task);
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    globalScheduler.execute(plugin, task);
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    public static <T> CompletableFuture<T> callSyncMethod(@Nullable Location loc, @NotNull Callable<T> task) {
        JavaPlugin plugin = InventoryRollbackPlus.getInstance();
        if (usingFolia) {
            CompletableFuture<T> future = new CompletableFuture<>();
            try {
                if (loc != null) {
                    Method getRegionScheduler = plugin.getServer().getClass().getMethod("getRegionScheduler");
                    RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(plugin.getServer());
                    regionScheduler.execute(plugin, loc, () -> {
                        try {
                            future.complete(task.call());
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    });
                } else {
                    Method getGlobalScheduler = plugin.getServer().getClass().getMethod("getGlobalRegionScheduler");
                    GlobalRegionScheduler globalScheduler = (GlobalRegionScheduler) getGlobalScheduler.invoke(plugin.getServer());
                    globalScheduler.execute(plugin, () -> {
                        try {
                            future.complete(task.call());
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    });
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
            return future;
        }
        CompletableFuture<T> cf = new CompletableFuture<>();
        try {
            Future<T> bukkitFuture = plugin.getServer().getScheduler().callSyncMethod(plugin, task);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    T result = bukkitFuture.get();
                    cf.complete(result);
                } catch (Throwable ex) {
                    cf.completeExceptionally(ex);
                }
            });
        } catch (Throwable e) {
            cf.completeExceptionally(e);
        }
        return cf;
    }
}
