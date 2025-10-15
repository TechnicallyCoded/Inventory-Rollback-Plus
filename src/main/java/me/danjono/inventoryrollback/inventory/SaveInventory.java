package me.danjono.inventoryrollback.inventory;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.folia.SchedulerUtils;
import com.nuclyon.technicallycoded.inventoryrollback.util.UserLogRateLimiter;
import com.nuclyon.technicallycoded.inventoryrollback.util.serialization.ItemStackSerialization;
import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SaveInventory {

    private static final HashMap<UUID, UserLogRateLimiter> rateLimiters = new HashMap<>();

    private final InventoryRollbackPlus main;

    private final long timestamp;
    private final Player player;
    private final LogType logType;
    private final DamageCause deathCause;
    private final String causeAlias;

    public SaveInventory(Player player, LogType logType, DamageCause deathCause, String causeAliasIn) {
        this.main = InventoryRollbackPlus.getInstance();
        this.timestamp = System.currentTimeMillis();
        this.player = player;
        this.logType = logType;
        this.deathCause = deathCause;
        this.causeAlias = causeAliasIn;
    }

    public void snapshotAndSave(PlayerInventory mainInventory, Inventory enderChestInventory, boolean saveAsync) {
        PlayerDataSnapshot snapshot = createSnapshot(mainInventory, enderChestInventory);
        if (snapshot == null) return;
        
        save(snapshot, saveAsync);
    }

    public void save(PlayerDataSnapshot snapshot, boolean async) {
        if (snapshot == null) return;
        UUID uuid = player.getUniqueId();

        // Rate limiter
        UserLogRateLimiter userLogRateLimiter = rateLimiters.get(uuid);
        if (userLogRateLimiter == null) {
            userLogRateLimiter = new UserLogRateLimiter();
            rateLimiters.put(uuid, userLogRateLimiter);
        }
        userLogRateLimiter.log(logType, timestamp);
        if (userLogRateLimiter.isRateLimitExceeded(logType)) {
            main.getLogger().warning("Player " + player.getName() + " is being rate limited! This means that something is causing this log to be created FASTER than even once per tick! Log type: " + logType.name());
            new IllegalStateException("Rate limiting reached! This should never happen under normal operation!").printStackTrace();
            return;
        }

        boolean saveAsync = !InventoryRollbackPlus.getInstance().isShuttingDown() && async;
        Runnable saveTask = () -> {
            PlayerData data = new PlayerData(player, logType, timestamp);

            if (snapshot.finalMainInvContents != null) data.setMainInventory(snapshot.finalMainInvContents);
            if (snapshot.finalMainInvArmor != null) data.setArmour(snapshot.finalMainInvArmor);
            if (snapshot.finalEnderInvContents != null) data.setEnderChest(snapshot.finalEnderInvContents);

            data.setXP(snapshot.totalXp);
            data.setHealth(snapshot.health);
            data.setFoodLevel(snapshot.foodLevel);
            data.setSaturation(snapshot.saturation);
            data.setWorld(snapshot.worldName);

            data.setX(snapshot.locX);
            data.setY(snapshot.locY);
            data.setZ(snapshot.locZ);

            data.setLogType(logType);
            data.setVersion(InventoryRollback.getPackageVersion());

            if (causeAlias != null) data.setDeathReason(causeAlias);
            else if (deathCause != null) data.setDeathReason(deathCause.name());
            else if (logType == LogType.DEATH) data.setDeathReason("UNKNOWN");

            // Remove excess saves if limit is reached
            CompletableFuture<Void> purgeTask = data.purgeExcessSaves(saveAsync);

            // Save new data
            purgeTask.thenRun(() -> data.saveData(saveAsync));
        };

        if (saveAsync) SchedulerUtils.runTaskAsynchronously(saveTask);
        else saveTask.run();

    }

    public @Nullable PlayerDataSnapshot createSnapshot(PlayerInventory mainInventory, Inventory enderChestInventory) {
        ItemStack[] mainInvContents = null;
        ItemStack[] mainInvArmor = null;
        ItemStack[] enderInvContents = null;

        for (ItemStack item : mainInventory.getContents()) {
            // If any item is not null, we take a copy of the contents
            if (item != null) {
                mainInvContents = copyItemArray(mainInventory.getContents());
                break;
            }
        }

        if (main.getVersion().lessOrEqThan(BukkitVersion.v1_8_R3)) {
            for (ItemStack item : mainInventory.getArmorContents()) {
                // If any item is not null, we take a copy of the contents
                if (item != null) {
                    mainInvArmor = copyItemArray(mainInventory.getArmorContents());
                    break;
                }
            }
        }

        for (ItemStack item : enderChestInventory.getContents()) {
            // If any item is not null, we take a copy of the contents
            if (item != null) {
                enderInvContents = copyItemArray(enderChestInventory.getContents());
                break;
            }
        }

        float totalXp = getTotalExperience(player);
        double health = player.getHealth();
        int foodLevel = player.getFoodLevel();
        float saturation = player.getSaturation();
        String worldName = player.getWorld().getName();

        // Location data
        Location pLoc = player.getLocation();
        // Multiply by 10, truncate, divide by 10
        // This has the effect of only keeping 1 decimal of precision
        double locX = ((int)(pLoc.getX() * 10)) / 10d;
        double locY = ((int)(pLoc.getY() * 10)) / 10d;
        double locZ = ((int)(pLoc.getZ() * 10)) / 10d;

        // Final vars
        ItemStack[] finalMainInvContents = mainInvContents;
        ItemStack[] finalMainInvArmor = mainInvArmor;
        ItemStack[] finalEnderInvContents = enderInvContents;

        return new PlayerDataSnapshot(totalXp, health, foodLevel, saturation, worldName, locX, locY, locZ,
                finalMainInvContents, finalMainInvArmor, finalEnderInvContents);
    }

    private ItemStack[] copyItemArray(ItemStack[] contents) {
        ItemStack[] copy = new ItemStack[contents.length];
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                copy[i] = contents[i].clone();
            }
        }
        return copy;
    }

    public static class PlayerDataSnapshot {
        public final float totalXp;
        public final double health;
        public final int foodLevel;
        public final float saturation;
        public final String worldName;
        public final double locX;
        public final double locY;
        public final double locZ;
        public final ItemStack[] finalMainInvContents;
        public final ItemStack[] finalMainInvArmor;
        public final ItemStack[] finalEnderInvContents;

        public PlayerDataSnapshot(float totalXp, double health, int foodLevel, float saturation, String worldName, double locX, double locY, double locZ, ItemStack[] finalMainInvContents, ItemStack[] finalMainInvArmor, ItemStack[] finalEnderInvContents) {
            this.totalXp = totalXp;
            this.health = health;
            this.foodLevel = foodLevel;
            this.saturation = saturation;
            this.worldName = worldName;
            this.locX = locX;
            this.locY = locY;
            this.locZ = locZ;
            this.finalMainInvContents = finalMainInvContents;
            this.finalMainInvArmor = finalMainInvArmor;
            this.finalEnderInvContents = finalEnderInvContents;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            PlayerDataSnapshot that = (PlayerDataSnapshot) obj;

            if (Double.compare(that.health, health) != 0) return false;
            if (foodLevel != that.foodLevel) return false;
            if (Float.compare(that.saturation, saturation) != 0) return false;
            if (Double.compare(that.locX, locX) != 0) return false;
            if (Double.compare(that.locY, locY) != 0) return false;
            if (Double.compare(that.locZ, locZ) != 0) return false;
            if (Float.compare(that.totalXp, totalXp) != 0) return false;
            if (!worldName.equals(that.worldName)) return false;
            if (!Arrays.equals(finalMainInvContents, that.finalMainInvContents)) return false;
            if (!Arrays.equals(finalMainInvArmor, that.finalMainInvArmor)) return false;
            if (!Arrays.equals(finalEnderInvContents, that.finalEnderInvContents)) return false;

            return true;
        }

        @Override
        public String toString() {
            return "PlayerDataSnapshot{" +
                    "totalXp=" + totalXp +
                    ", health=" + health +
                    ", foodLevel=" + foodLevel +
                    ", saturation=" + saturation +
                    ", worldName='" + worldName + '\'' +
                    ", locX=" + locX +
                    ", locY=" + locY +
                    ", locZ=" + locZ +
                    ", finalMainInvContents=" + Arrays.toString(finalMainInvContents) +
                    ", finalMainInvArmor=" + Arrays.toString(finalMainInvArmor) +
                    ", finalEnderInvContents=" + Arrays.toString(finalEnderInvContents) +
                    '}';
        }
    }

    //Conversion to Base64 code courtesy of github.com/JustRayz
    public static String toBase64(ItemStack[] contents) {
        return ItemStackSerialization.serialize(contents);
    }

    //Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
    //https://gist.github.com/RichardB122/8958201b54d90afbc6f0
    private float getTotalExperience(Player player) {
        int level = player.getLevel();
        float currentExp = player.getExp();
        int experience;
        int requiredExperience;

        if(level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            requiredExperience = 2 * level + 7;
        } else if(level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            requiredExperience = 5 * level - 38;
        } else {
            experience = (int) Math.ceil((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220));
            requiredExperience = 9 * level - 158;
        }

        experience += Math.ceil(currentExp * requiredExperience);

        return experience;
    }

    public static void cleanup(UUID uuid) {
        rateLimiters.remove(uuid);
    }

}
