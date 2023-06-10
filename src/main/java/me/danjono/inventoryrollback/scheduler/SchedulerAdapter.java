package me.danjono.inventoryrollback.scheduler;

import org.bukkit.entity.Entity;

import java.util.function.Consumer;

public interface SchedulerAdapter {
    void runAsync(Runnable runnable);

    void runAtGlobal(Runnable runnable);

    void runAtGlobalRate(Consumer<ScheduledTask> consumer, long delay, long period);

    void cancelTasks();

    void runAtEntity(Entity entity, Runnable runnable);

    void runAtEntityDelayed(Entity entity, Runnable runnable, long delay);

    void runAsyncDelayed(Runnable runnable, long delay);
}
