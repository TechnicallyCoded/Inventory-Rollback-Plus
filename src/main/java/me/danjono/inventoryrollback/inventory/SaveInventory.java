package me.danjono.inventoryrollback.inventory;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.InventoryRollback.VersionName;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SaveInventory {

    private final Player player;
    private final LogType logType;
    private final DamageCause deathCause;

    private final PlayerInventory mainInventory;
    private final Inventory enderChestInventory;

    public SaveInventory(Player player, LogType logType, DamageCause deathCause, PlayerInventory mainInventory, Inventory enderChestInventory) {
        this.player = player;
        this.logType = logType;
        this.deathCause = deathCause;

        this.mainInventory = mainInventory;
        this.enderChestInventory = enderChestInventory;
    }

    //Conversion to Base64 code courtesy of github.com/JustRayz
    private static String toBase64(Inventory inventory) {
        return toBase64(inventory.getContents());
    }

    private static String toBase64(ItemStack[] contents) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(contents.length);

            for (ItemStack stack : contents) {
                dataOutput.writeObject(stack);
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public CompletableFuture<?> saveToDiskAsync() {

        final Location location = player.getLocation();
        final double health = player.getHealth();
        final int foodLevel = player.getFoodLevel();
        final float saturation = player.getSaturation();
        final String world = player.getWorld().getName();

        final String serializedArmour;
        final String serializedEnderchest = toBase64(enderChestInventory);
        final String serializedMainInventory = toBase64(mainInventory);

        if (InventoryRollback.getVersion().equals(VersionName.v1_8)) {
            serializedArmour = toBase64(mainInventory.getArmorContents());
        } else {
            serializedArmour = null;
        }
        final CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        final Runnable runnable = () -> {

            // Loading player data is fine async, Player UUID's are final anyway.
            PlayerData data = new PlayerData(player, logType, true);
            FileConfiguration inventoryData = data.getData();

            int maxSaves = data.getMaxSaves();

            float xp = getTotalExperience(player);
            long time = System.currentTimeMillis();
            int saves = inventoryData.getInt("saves");

            ConfigurationSection dataSection = inventoryData.getConfigurationSection("data");
            if (dataSection == null) {
                dataSection = inventoryData.createSection("data");
            }

            if (data.getFile().exists() && maxSaves > 0) {
                if (saves >= maxSaves) {
                    List<Double> timeSaved = new ArrayList<>();

                    for (String times : dataSection.getKeys(false)) {
                        timeSaved.add(Double.parseDouble(times));
                    }

                    int deleteAmount = saves - maxSaves + 1;

                    for (int i = 0; i < deleteAmount; i++) {
                        Double deleteData = Collections.min(timeSaved);
                        DecimalFormat df = new DecimalFormat("#.##############");

                        inventoryData.set("data." + df.format(deleteData), null);
                        timeSaved.remove(deleteData);
                        saves--;
                    }
                }
            }
            ConfigurationSection timeSection = dataSection.getConfigurationSection(String.valueOf(time));
            if (timeSection == null) {
                timeSection = dataSection.createSection(String.valueOf(time));
            }

            // Save data to the FileConfiguration
            timeSection.set("inventory", serializedMainInventory);
            timeSection.set("armor", serializedArmour);
            timeSection.set("enderchest", serializedEnderchest);
            timeSection.set("xp", xp);
            timeSection.set("health", health);
            timeSection.set("hunger", foodLevel);
            timeSection.set("saturation", saturation);
            final ConfigurationSection locSection = timeSection.createSection("location");
            locSection.set("world", world);
            locSection.set("x", location.getBlockX());
            locSection.set("y", location.getBlockY());
            locSection.set("z", location.getBlockZ());
            timeSection.set("logType", logType.name());
            timeSection.set("version", InventoryRollback.getPackageVersion());
            if (InventoryRollback.getVersion().equals(VersionName.v1_8) && serializedArmour != null)
               timeSection.set("armor", serializedArmour);

            if (deathCause != null) {
                timeSection.set("deathReason", deathCause.name());
            }
            inventoryData.set("saves", saves + 1);

            // Save data to disk, can be "sync" because we're already on an async thread
            data.saveDataSync();
            completableFuture.complete(true);
        };

        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), runnable);
        return completableFuture;
    }

    //Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
    //https://gist.github.com/RichardB122/8958201b54d90afbc6f0
    private float getTotalExperience(Player player) {
        int level = player.getLevel();
        float currentExp = player.getExp();
        int experience;
        int requiredExperience;

        if (level >= 0 && level <= 15) {
            experience = (int) Math.ceil(level * level + (6 * level));
            requiredExperience = 2 * level + 7;
        } else if (level > 15 && level <= 30) {
            experience = (int) Math.ceil(2.5 * level * level - (40.5 * level) + 360);
            requiredExperience = 5 * level - 38;
        } else {
            experience = (int) Math.ceil((4.5 * level * level - (162.5 * level) + 2220));
            requiredExperience = 9 * level - 158;
        }

        experience += Math.ceil(currentExp * requiredExperience);

        return experience;
    }

}
