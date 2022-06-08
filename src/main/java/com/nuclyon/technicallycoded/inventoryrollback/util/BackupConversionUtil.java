package com.nuclyon.technicallycoded.inventoryrollback.util;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.data.YAML;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class BackupConversionUtil {

    public static Map<String, LogType> oldLogTypesMap = new HashMap<>();

    static {
        oldLogTypesMap.put("WORLDCHANGE", LogType.WORLD_CHANGE);
    }

    public static void convertOldBackupData() {
        Logger logger = InventoryRollbackPlus.getPluginLogger();

        List<File> oldBackupTypeFolders = new ArrayList<>();

        File oldBackupsRoot = new File(ConfigData.getFolderLocation().getParentFile(), "InventoryRollback/saves");

        oldBackupTypeFolders.add(new File(oldBackupsRoot, "deaths"));
        oldBackupTypeFolders.add(new File(oldBackupsRoot, "joins"));
        oldBackupTypeFolders.add(new File(oldBackupsRoot, "quits"));
        oldBackupTypeFolders.add(new File(oldBackupsRoot, "worldChanges"));
        oldBackupTypeFolders.add(new File(oldBackupsRoot, "force"));

        int logTypeNumber = -1;
        List<LogType> logTypes = new ArrayList<>();
        logTypes.add(LogType.DEATH);
        logTypes.add(LogType.JOIN);
        logTypes.add(LogType.QUIT);
        logTypes.add(LogType.WORLD_CHANGE);
        logTypes.add(LogType.FORCE);

        for (File oldBackupFolder : oldBackupTypeFolders) {

            logTypeNumber++;

            if (!oldBackupFolder.exists()) {
                logger.warning(MessageData.getPluginPrefix() + "Backup folder does not exist at " + oldBackupFolder.getAbsolutePath() + "! Skipping...");
                continue;
            }

            // Add all YAML files to list
            File[] availableFiles = oldBackupFolder.listFiles();
            if (availableFiles == null) continue;

            List<File> backupFilesToConvert = getFilesToConvert(availableFiles);

            LogType currLogTypeProcessing = logTypes.get(logTypeNumber);
            logger.info(MessageData.getPluginPrefix() + "Converting the backup location " + currLogTypeProcessing.name());

            for (File backupFile : backupFilesToConvert) {
                convertBackupFile(logger, backupFile, currLogTypeProcessing);
            }

        }

        InventoryRollbackPlus.getPluginLogger().info(MessageData.getPluginPrefix() + "Conversion completed!");
    }

    private static ArrayList<File> getFilesToConvert(File[] availableFiles) {
        ArrayList<File> backupFiles = new ArrayList<>();

        // Sanity check files in the folder & add them to a list to process later
        for (File file : availableFiles) {
            String originalFileName = file.getName();
            String[] fileParts = originalFileName.split("\\.");
            String fileUUIDStr = fileParts[0];
            String fileExtension = fileParts[1];

            UUID playerUuid;
            try {
                // Check if it's a valid UUID
                playerUuid = UUID.fromString(fileUUIDStr);
            } catch (IllegalArgumentException ex) {
                InventoryRollbackPlus.getInstance().getLogger().severe(
                        "An error occurred when trying to retrieve a old backup player UUID! " +
                                "Please seek help in the issues section of the InventryRollbackPlus github page.");
                ex.printStackTrace();
                continue;
            }

            if (file.isFile() && fileExtension.equals("yml")) {
                backupFiles.add(file);
            }
        }

        return backupFiles;
    }

    public static void convertBackupFile(Logger logger, File backupFile, LogType logTypeProcessing) {
        YamlConfiguration oldBackupDataConfig;
        oldBackupDataConfig = loadConfiguration(backupFile);

        if (oldBackupDataConfig == null) {
            logger.warning(MessageData.getPluginPrefix() + "Error converting backup file at " +
                    backupFile.getAbsolutePath() + " - Invalid YAML format possibly from corruption.");
            return;
        }

        ConfigurationSection configSectionData = oldBackupDataConfig.getConfigurationSection("data");
        if (configSectionData == null) {
            return;
        }
        Set<String> timestamps = configSectionData.getKeys(false);

        for (String timestampStr : timestamps) {
            try {
                Long timestamp = Long.parseLong(timestampStr);
                String fileName = backupFile.getName();
                String fileUUIDStr = fileName.substring(0, fileName.indexOf('.'));

                UUID uuid;
                try {
                    // Check if it's a valid UUID
                    uuid = UUID.fromString(fileUUIDStr);
                } catch (IllegalArgumentException ex) {
                    InventoryRollbackPlus.getInstance().getLogger().severe(
                            "An error occurred when trying to retrieve the player UUID from " + backupFile.getAbsolutePath() + "#" + timestampStr + "! " +
                                    "Please ask for help in the issues section of the InventoryRollbackPlus github page.");
                    ex.printStackTrace();
                    continue;
                }

                // ---- Load all data from the old config ----

                String packageVersion = oldBackupDataConfig.getString("data." + timestamp + ".version");
                ItemStack[] mainInvItems = RestoreInventory.getInventoryItems(packageVersion,
                        oldBackupDataConfig.getString("data." + timestamp + ".inventory"));
                ItemStack[] armorItems = RestoreInventory.getInventoryItems(packageVersion,
                        oldBackupDataConfig.getString("data." + timestamp + ".armour"));
                ItemStack[] enderChestItems = RestoreInventory.getInventoryItems(packageVersion,
                        oldBackupDataConfig.getString("data." + timestamp + ".enderchest"));
                float xp = Float.parseFloat(
                        oldBackupDataConfig.getString("data." + timestamp + ".xp"));
                double health = oldBackupDataConfig.getDouble("data." + timestamp + ".health");
                int foodLevel = oldBackupDataConfig.getInt("data." + timestamp + ".hunger");
                float saturation = Float.parseFloat(
                        oldBackupDataConfig.getString("data." + timestamp + ".saturation"));
                String worldName = oldBackupDataConfig.getString("data." + timestamp + ".location.world");
                double posX = oldBackupDataConfig.getDouble("data." + timestamp + ".location.x");
                double posY = oldBackupDataConfig.getDouble("data." + timestamp + ".location.y");
                double posZ = oldBackupDataConfig.getDouble("data." + timestamp + ".location.z");
                String logTypeStoredString = oldBackupDataConfig.getString("data." + timestamp + ".logType");
                String deathReason = oldBackupDataConfig.getString("data." + timestamp + ".deathReason");

                // ---- Process all loaded data ----

                PlayerData importedData = new PlayerData(uuid, logTypeProcessing, timestamp);

                importedData.setMainInventory(mainInvItems);
                importedData.setArmour(armorItems);
                importedData.setEnderChest(enderChestItems);
                importedData.setXP(xp);
                importedData.setHealth(health);
                importedData.setFoodLevel(foodLevel);
                importedData.setSaturation(saturation);
                importedData.setWorld(worldName);
                importedData.setX(posX);
                importedData.setY(posY);
                importedData.setZ(posZ);

                // Sanity check log type
                if (logTypeStoredString == null) {
                    InventoryRollbackPlus.getInstance().getLogger().severe(
                            "An error occurred when trying to retrieve the backup type of " + backupFile.getAbsolutePath() + "#" + timestampStr + "! " +
                                    "Please ask for help in the issues section of the InventoryRollbackPlus github page. (typeStr is null)");
                    continue;
                }

                // Convert old log type format to new enum value
                LogType logTypeStored = oldLogTypesMap.get(logTypeStoredString);

                // If this log type isn't an old type, attempt retrieval from newer LogType enum
                if (logTypeStored == null) {
                    try {
                        logTypeStored = LogType.valueOf(logTypeStoredString);
                    } catch (IllegalArgumentException ex) {
                        InventoryRollbackPlus.getInstance().getLogger().severe(
                                "An error occurred when trying to retrieve the backup type of " + backupFile.getAbsolutePath() + "#" + timestampStr + "! " +
                                        "Please ask for help in the issues section of the InventoryRollbackPlus github page. (typeStr: " + logTypeStoredString + ")");
                        continue;
                    }
                }

                // Apply last data after sanity checks & conversions
                importedData.setLogType(logTypeStored);
                importedData.setVersion(packageVersion);
                if (deathReason != null) importedData.setDeathReason(deathReason);

                // Save the data to the new folder location
                importedData.saveData(true);
            } catch (Exception e) {
                InventoryRollbackPlus.getPluginLogger().warning(
                        MessageData.getPluginPrefix() + "Error converting backup file at " +
                                backupFile.getAbsolutePath() + " on timestamp " + timestampStr);
            }
        }
    }

    public static YamlConfiguration loadConfiguration(@NotNull File file) {
        Validate.notNull(file, "File cannot be null");
        YamlConfiguration config;

        try {
            config = new YamlConfiguration();
            config.load(file);
        } catch (FileNotFoundException ignored) {
            return null;
        } catch (IOException | InvalidConfigurationException var4) {
            Bukkit.getLogger().severe("Cannot load " + file);
            return null;
        }

        return config;
    }

}
