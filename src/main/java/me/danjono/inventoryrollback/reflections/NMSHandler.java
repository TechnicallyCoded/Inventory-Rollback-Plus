package me.danjono.inventoryrollback.reflections;

import me.danjono.inventoryrollback.InventoryRollback;

public class NMSHandler {

    public Class<?> getNMSClass(String name) {
        Class<?> c = null;

        try {
            c = Class.forName("net.minecraft.server." + InventoryRollback.getPackageVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return c;
    }

    public Class<?> getCraftBukkitClass(String name) {
        Class<?> c = null;

        try {
            c = Class.forName("org.bukkit.craftbukkit." + InventoryRollback.getPackageVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return c;
    }
}