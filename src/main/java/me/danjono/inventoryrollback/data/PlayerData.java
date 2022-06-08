package me.danjono.inventoryrollback.data;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.ConfigData.SaveType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerData {

    private final OfflinePlayer offlinePlayer;
    private final LogType logType;
    private final Long timestamp;

    private YAML yaml;
    private MySQL mysql;
    
    public PlayerData(OfflinePlayer offlinePlayer, LogType logType, Long timestamp) {
        this.offlinePlayer = offlinePlayer;
        this.logType = logType;
        this.timestamp = timestamp;

        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml = new YAML(offlinePlayer.getUniqueId(), logType, timestamp);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql = new MySQL(offlinePlayer.getUniqueId(), logType, timestamp);
        }
    }

    public PlayerData(UUID uuid, LogType logType, Long timestamp) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        this.logType = logType;
        this.timestamp = timestamp;

        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml = new YAML(uuid, logType, timestamp);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql = new MySQL(uuid, logType, timestamp);
        }
    }
        
    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
        
    }
    
    public LogType getLogType() {
        return this.logType;
    }
    
    public Long getTimestamp() {
        return this.timestamp;
    }

    public boolean doesBackupTypeExist() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.doesBackupTypeExist();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            try {
                return mysql.doesBackupTypeExist();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public int getAmountOfBackups() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getAmountOfBackups();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            try {
                return mysql.getAmountOfBackups();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public List<Long> getSelectedPageTimestamps(int pageNumber) {
        List<Long> timeStamps = new ArrayList<>();

        if (ConfigData.getSaveType() == SaveType.YAML) {
            timeStamps = yaml.getSelectedPageTimestamps(pageNumber);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            try {
                timeStamps = mysql.getSelectedPageTimestamps(pageNumber);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return timeStamps;
    }

    public CompletableFuture<Void> purgeExcessSaves(boolean shouldSaveAsync) {

        CompletableFuture<Void> future = new CompletableFuture<>();

        boolean saveAsync = !InventoryRollbackPlus.getInstance().isShuttingDown() && shouldSaveAsync;
        Runnable purgeTask = () -> {
            int maxSaves = getMaxSaves();
            int currentSaves = getAmountOfBackups();

            if((maxSaves >0) && (currentSaves >= maxSaves)) {
                int deleteAmount = currentSaves - maxSaves + 1;

                if (ConfigData.getSaveType() == SaveType.YAML) {
                    yaml.purgeExcessSaves(deleteAmount);
                } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
                    try {
                        mysql.purgeExcessSaves(deleteAmount);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            future.complete(null);
        };

        InventoryRollbackPlus instance = InventoryRollbackPlus.getInstance();
        if (saveAsync) instance.getServer().getScheduler().runTaskAsynchronously(instance, purgeTask);
        else purgeTask.run();

        return future;
    }

    public void setMainInventory(ItemStack[] items) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setMainInventory(items);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setMainInventory(items);
        }
    }

    public void setArmour(ItemStack[] items) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setArmour(items);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setArmour(items);
        }
    }

    public void setEnderChest(ItemStack[] items) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setEnderChest(items);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setEnderChest(items);
        }
    }

    public void setXP(float xp) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setXP(xp);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setXP(xp);
        }
    }

    public void setHealth(double health) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setHealth(health);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setHealth(health);
        }
    }

    public void setFoodLevel(int foodLevel) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setFoodLevel(foodLevel);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setFoodLevel(foodLevel);
        }
    }

    public void setSaturation(float saturation) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setSaturation(saturation);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setSaturation(saturation);
        }
    }

    public void setWorld(String world) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setWorld(world);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setWorld(world);
        }
    }

    public void setX(double x) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setX(x);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setX(x);
        }
    }

    public void setY(double y) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setY(y);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setY(y);
        }
    }

    public void setZ(double z) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setZ(z);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setZ(z);
        }
    }

    public void setLogType(LogType logType) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setLogType(logType);
        }
    }

    public void setVersion(String packageVersion) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setVersion(packageVersion);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setVersion(packageVersion);
        }
    }

    public void setDeathReason(String deathReason) {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            yaml.setDeathReason(deathReason);
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            mysql.setDeathReason(deathReason);
        }
    }

    public void getRollbackMenuData() {
        if (ConfigData.getSaveType() == SaveType.MYSQL) {
            try {
                mysql.getRollbackMenuData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public CompletableFuture<Void> getAllBackupData() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (ConfigData.getSaveType() == SaveType.MYSQL) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        mysql.getAllBackupData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    future.complete(null);
                }
            }.runTaskAsynchronously(InventoryRollbackPlus.getInstance());
        }
        return future;
    }

    public ItemStack[] getMainInventory() {
        ItemStack[] items = {};

        if (ConfigData.getSaveType() == SaveType.YAML) {
            items = yaml.getMainInventory();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            items = mysql.getMainInventory();
        }

        return items;
    }

    public ItemStack[] getArmour() {
        ItemStack[] items = {};

        if (ConfigData.getSaveType() == SaveType.YAML) {
            items = yaml.getArmour();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            items = mysql.getArmour();
        }

        return items;
    }

    public ItemStack[] getEnderChest() {
        ItemStack[] items = {};

        if (ConfigData.getSaveType() == SaveType.YAML) {
            items = yaml.getEnderChest();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            items = mysql.getEnderChest();
        }

        return items;
    }

    public float getXP() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getXP();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getXP();
        }

        return 0;
    }

    public double getHealth() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getHealth();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getHealth();
        }

        return 0;
    }

    public int getFoodLevel() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getFoodLevel();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getFoodLevel();
        }

        return 0;
    }

    public float getSaturation() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getSaturation();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getSaturation();
        }

        return 0;
    }

    public String getWorld() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getWorld();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getWorld();
        }

        return null;
    }

    public double getX() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getX();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getX();
        }

        return 0;
    }

    public double getY() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getY();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getY();
        }

        return 0;
    }

    public double getZ() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getZ();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getZ();
        }

        return 0;
    }

    public LogType getSaveType() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getSaveType();
        }

        return null;
    }

    public String getVersion() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getVersion();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getVersion();
        }

        return null;
    }

    public String getDeathReason() {
        if (ConfigData.getSaveType() == SaveType.YAML) {
            return yaml.getDeathReason();
        } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
            return mysql.getDeathReason();
        }

        return null;
    }

    public void saveData(boolean shouldSaveAsync) {
        boolean saveAsync = !InventoryRollbackPlus.getInstance().isShuttingDown() && shouldSaveAsync;

        Runnable saveDataTask = () -> {
            if (ConfigData.getSaveType() == SaveType.YAML) {
                yaml.saveData();
            } else if (ConfigData.getSaveType() == SaveType.MYSQL) {
                try {
                    mysql.saveData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        if (saveAsync) Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(),saveDataTask);
        else saveDataTask.run();
    }

    public int getMaxSaves() {
        if (logType == LogType.JOIN) {
            return ConfigData.getMaxSavesJoin();
        } else if (logType == LogType.QUIT) {
            return ConfigData.getMaxSavesQuit();
        } else if (logType == LogType.DEATH) {
            return ConfigData.getMaxSavesDeath();
        } else if (logType == LogType.WORLD_CHANGE) {
            return ConfigData.getMaxSavesWorldChange();
        } else if (logType == LogType.FORCE) {
            return ConfigData.getMaxSavesForce();
        }


        return 0;
    }

    public static String getTime(Long time) {
        SimpleDateFormat sdf = ConfigData.getTimeFormat();
        sdf.setTimeZone(ConfigData.getTimeZone());
        return sdf.format(new Date(time));
    }

}
