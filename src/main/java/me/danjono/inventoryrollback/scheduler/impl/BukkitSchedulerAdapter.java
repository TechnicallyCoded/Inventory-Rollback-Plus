package me.danjono.inventoryrollback.scheduler.impl;

import me.danjono.inventoryrollback.scheduler.ScheduledTask;
import me.danjono.inventoryrollback.scheduler.SchedulerAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class BukkitSchedulerAdapter implements SchedulerAdapter {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitSchedulerAdapter(final Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public void runAsync(final Runnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runAtGlobal(Runnable runnable) {
        scheduler.runTask(plugin, runnable);
    }

    @Override
    public void runAtGlobalRate(Consumer<ScheduledTask> task, long delay, long period) {
        scheduler.runTaskTimer(plugin, bukkitTask -> task.accept(new BukkitSchedulerTask(bukkitTask)), delay, period);
    }

    @Override
    public void cancelTasks() {
        scheduler.cancelTasks(plugin);
    }

    @Override
    public void runAtEntity(Entity entity, Runnable runnable) {
        scheduler.runTask(plugin, runnable);
    }

    @Override
    public void runAtEntityDelayed(Entity entity, Runnable runnable, long delay) {
        scheduler.scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    @Override
    public void runAsyncDelayed(Runnable runnable, long delay) {
        scheduler.runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    private static class BukkitSchedulerTask implements ScheduledTask {
        private final BukkitTask task;

        public BukkitSchedulerTask(BukkitTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
