package com.nuclyon.technicallycoded.inventoryrollback.data;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.config.ConfigFile;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final LogType logType;
    private final File folderLocation;

    private File playerFile;
    private FileConfiguration playerData;

    public PlayerData(Player player, LogType logType) {
        this.logType = logType;
        this.uuid = player.getUniqueId();
        this.folderLocation = new File(ConfigFile.folderLocation, "saves/");

        findPlayerFile();
        findPlayerData();
    }

    public PlayerData(OfflinePlayer player, LogType logType) {
        this.logType = logType;
        this.uuid = player.getUniqueId();
        this.folderLocation = new File(ConfigFile.folderLocation, "saves/");

        findPlayerFile();
        findPlayerData();
    }

    public PlayerData(UUID uuid, LogType logType) {
        this.logType = logType;
        this.uuid = uuid;
        this.folderLocation = new File(ConfigFile.folderLocation, "saves/");

        findPlayerFile();
        findPlayerData();
    }

    private boolean findPlayerFile() {
        if (logType == LogType.JOIN) {
            this.playerFile = new File(folderLocation, "joins/" + uuid + ".yml");
        } else if (logType == LogType.QUIT) {
            this.playerFile = new File(folderLocation, "quits/" + uuid + ".yml");
        } else if (logType == LogType.DEATH) {
            this.playerFile = new File(folderLocation, "deaths/" + uuid + ".yml");
        } else if (logType == LogType.WORLD_CHANGE) {
            this.playerFile = new File(folderLocation, "worldChanges/" + uuid + ".yml");
        } else if (logType == LogType.FORCE) {
            this.playerFile = new File(folderLocation, "force/" + uuid + ".yml");
        }

        return this.playerFile != null;
    }

    private boolean findPlayerData() {
        this.playerData = YamlConfiguration.loadConfiguration(playerFile);

        return this.playerData != null;
    }

    public File getFile() {
        return this.playerFile;
    }

    public FileConfiguration getData() {
        return this.playerData;
    }

    public void saveData() {
        InventoryRollback.getInstance().getServer().getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            try {
                playerData.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();

            }
        });
    }

    public int getMaxSaves() {
        if (logType == LogType.JOIN) {
            return ConfigFile.maxSavesJoin;
        } else if (logType == LogType.QUIT) {
            return ConfigFile.maxSavesQuit;
        } else if (logType == LogType.DEATH) {
            return ConfigFile.maxSavesDeath;
        } else if (logType == LogType.WORLD_CHANGE) {
            return ConfigFile.maxSavesWorldChange;
        } else if (logType == LogType.FORCE) {
            return ConfigFile.maxSavesForce;
        } else {
            return 0;
        }
    }

}
