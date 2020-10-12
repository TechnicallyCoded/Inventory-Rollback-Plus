package me.danjono.inventoryrollback.data;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerData {

    private final UUID uuid;
    private final LogType logType;
    private final File folderLocation;

    private File playerFile;
    private FileConfiguration playerData = new YamlConfiguration();

    public PlayerData(OfflinePlayer player, LogType logType) {
       this(player.getUniqueId(), logType, true);
    }

    public PlayerData(OfflinePlayer player, LogType logType, boolean load) {
        this(player.getUniqueId(), logType, load);
    }

    public PlayerData(UUID uuid, LogType logType) {
       this(uuid, logType, true);
    }

    public PlayerData(UUID uuid, LogType logType, boolean load) {
        this.logType = logType;
        this.uuid = uuid;
        this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
        this.playerFile = determinePlayerFile();
        if (load) {
            loadData();
        }
    }

    private File determinePlayerFile() {
        final String prefix;
        switch (logType) {
            case JOIN:
                prefix = "joins";
                break;
            case QUIT:
                prefix = "quits";
                break;
            case DEATH:
                prefix = "deaths";
                break;
            case WORLD_CHANGE:
                prefix = "worldChanges";
                break;
            case FORCE:
                prefix = "force";
                break;
            default:
                return null;
        }
        return new File(folderLocation, prefix + File.separator + uuid + ".yml");
    }

    public boolean loadData() {
        this.playerData = null;
        final File file = determinePlayerFile();
        if (file != null) {
            this.playerData = YamlConfiguration.loadConfiguration(file);
            return true;
        }
        return false;
    }

    public CompletableFuture<Boolean> loadDataAsync() {
        this.playerData = null;
        final File file = determinePlayerFile();
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        if (file == null) {
            return CompletableFuture.completedFuture(false);
        }
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            Bukkit.getScheduler().callSyncMethod(InventoryRollback.getInstance(), () -> {
                this.playerData = configuration;
                completableFuture.complete(true);
                return null;
            });
        });
        return completableFuture;
    }

    public File getFile() {
        return this.playerFile;
    }

    public FileConfiguration getData() {
        return this.playerData;
    }

    public CompletableFuture<Boolean> saveData() {
        final CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        final YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.loadFromString(playerData.saveToString());
        } catch (InvalidConfigurationException ex) {
            ex.printStackTrace();
            return CompletableFuture.completedFuture(false);
        }
        InventoryRollback.getInstance().getServer().getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
            try {
                configuration.save(playerFile);
                completableFuture.complete(true);
            } catch (IOException e) {
                e.printStackTrace();
                completableFuture.complete(false);
            }
        });
        return completableFuture;
    }

    public boolean saveDataSync() {
        try {
            playerData.save(playerFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getMaxSaves() {
        switch (logType) {
            case JOIN:
                return ConfigFile.maxSavesJoin;
            case FORCE:
                return ConfigFile.maxSavesForce;
            case WORLD_CHANGE:
                return ConfigFile.maxSavesWorldChange;
            case DEATH:
                return ConfigFile.maxSavesDeath;
            case QUIT:
                return ConfigFile.maxSavesQuit;
            default:
                return 0;
        }
    }

}
