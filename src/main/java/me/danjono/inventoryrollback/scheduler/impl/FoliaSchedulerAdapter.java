package me.danjono.inventoryrollback.scheduler.impl;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import me.danjono.inventoryrollback.scheduler.ScheduledTask;
import me.danjono.inventoryrollback.scheduler.SchedulerAdapter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaSchedulerAdapter implements SchedulerAdapter {
    private static final boolean SUPPORTED = checkSupport();
    private static final Runnable DO_NOTHING = () -> {
    };

    private final Plugin plugin;
    private final AsyncScheduler asyncScheduler;
    private final GlobalRegionScheduler globalScheduler;

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.asyncScheduler = plugin.getServer().getAsyncScheduler();
        this.globalScheduler = plugin.getServer().getGlobalRegionScheduler();
    }

    public static boolean isSupported() {
        return SUPPORTED;
    }

    private static boolean checkSupport() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void runAsync(final Runnable runnable) {
        asyncScheduler.runNow(plugin, task -> runnable.run());
    }

    @Override
    public void runAsyncDelayed(Runnable runnable, long delay) {
        asyncScheduler.runDelayed(plugin, task -> runnable.run(), delay * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAtGlobal(Runnable runnable) {
        globalScheduler.execute(plugin, runnable);
    }

    @Override
    public void runAtGlobalRate(Consumer<ScheduledTask> consumer, long delay, long period) {
        globalScheduler.runAtFixedRate(plugin, task -> consumer.accept(new FoliaScheduledTask(task)), delay, period);
    }

    @Override
    public void runAtEntity(Entity entity, Runnable runnable) {
        entity.getScheduler().execute(plugin, runnable, DO_NOTHING, 0);
    }

    @Override
    public void runAtEntityDelayed(Entity entity, Runnable runnable, long delay) {
        entity.getScheduler().execute(plugin, runnable, DO_NOTHING, delay);
    }

    @Override
    public void cancelTasks() {
        asyncScheduler.cancelTasks(plugin);
    }

    private static class FoliaScheduledTask implements ScheduledTask {
        private final io.papermc.paper.threadedregions.scheduler.ScheduledTask task;

        public FoliaScheduledTask(io.papermc.paper.threadedregions.scheduler.ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }
    }
}
