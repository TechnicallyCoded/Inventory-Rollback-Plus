package me.danjono.inventoryrollback.data;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.inventory.SaveInventory;

public class SQLite {

    private final UUID uuid;
    private final Long timestamp;
    private final BackupTable backupTable;

    public enum BackupTable {
        DEATH("deaths"),
        JOIN("joins"),
        QUIT("quits"),
        WORLD_CHANGE("world_changes"),
        FORCE("force_backups");

        private final String tableName;

        BackupTable(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return this.tableName;
        }
    }

    private Connection connection;

    private String mainInventory;
    private String armour;
    private String enderChest;
    private float xp;
    private double health;
    private int hunger;
    private float saturation;
    private String world;
    private double x;
    private double y;
    private double z;
    private final LogType logType;
    private String packageVersion;
    private String deathReason;

    public SQLite(UUID uuid, LogType logType, Long timestamp) {
        this.uuid = uuid;
        this.logType = logType;
        this.timestamp = timestamp;
        this.backupTable = getBackupTable();


    }

    public void openConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            File dbFile = new File(ConfigData.getFolderLocation(), "database.db");

            // Create parent directories if they don't exist
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            // Create the database file if it doesn't exist
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile();
                } catch (IOException e) {
                    throw new SQLException("Could not create database file", e);
                }
            }

            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createTables() throws SQLException {
        openConnection();

        try {
            for (BackupTable table : BackupTable.values()) {
                String tableQuery = "CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "uuid TEXT NOT NULL," +
                        "timestamp INTEGER NOT NULL," +
                        "xp REAL NOT NULL," +
                        "health REAL NOT NULL," +
                        "hunger INTEGER NOT NULL," +
                        "saturation REAL NOT NULL," +
                        "location_world TEXT NOT NULL," +
                        "location_x REAL NOT NULL," +
                        "location_y REAL NOT NULL," +
                        "location_z REAL NOT NULL," +
                        "version TEXT NOT NULL," +
                        "death_reason TEXT," +
                        "main_inventory BLOB," +
                        "armour BLOB," +
                        "ender_chest BLOB" +
                        ");";

                try (PreparedStatement statement = connection.prepareStatement(tableQuery)) {
                    statement.executeUpdate();
                }
            }
        } finally {
            closeConnection();
        }
    }

    private BackupTable getBackupTable() {
        if (logType == LogType.JOIN) {
            return BackupTable.JOIN;
        } else if (logType == LogType.QUIT) {
            return BackupTable.QUIT;
        } else if (logType == LogType.DEATH) {
            return BackupTable.DEATH;
        } else if (logType == LogType.WORLD_CHANGE) {
            return BackupTable.WORLD_CHANGE;
        } else if (logType == LogType.FORCE) {
            return BackupTable.FORCE;
        }

        return null;
    }

    public boolean doesBackupTypeExist() throws SQLException {
        openConnection();

        try {
            String query = "SELECT 1 FROM " + backupTable.getTableName() + " WHERE uuid = ? LIMIT 1";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());

                try (ResultSet results = statement.executeQuery()) {
                    // return results.next();
                    return  true;
                }
            }
        } finally {
            closeConnection();
        }
    }

    public int getAmountOfBackups() throws SQLException {
        openConnection();

        try {
            String query = "SELECT COUNT(id) FROM " + backupTable.getTableName() + " WHERE uuid = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());

                try (ResultSet results = statement.executeQuery()) {
                    results.next();
                    return results.getInt(1);
                }
            }
        } finally {
            closeConnection();
        }
    }

    public List<Long> getSelectedPageTimestamps(int pageNumber) throws SQLException {
        openConnection();

        List<Long> timeStamps = new ArrayList<>();
        int backups = InventoryName.ROLLBACK_LIST.getSize() - 9;

        try {
            int queryRange = ((pageNumber - 1) * backups);
            if (queryRange < 0)
                queryRange = 0;

            String query = "SELECT timestamp FROM " + backupTable.getTableName() +
                    " WHERE uuid = ? ORDER BY timestamp DESC LIMIT ? OFFSET ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, backups);
                statement.setInt(3, queryRange);

                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        timeStamps.add(results.getLong(1));
                    }
                }
            }

            return timeStamps;
        } finally {
            closeConnection();
        }
    }

    public void purgeExcessSaves(int deleteAmount) throws SQLException {
        openConnection();

        try {
            String delete = "DELETE FROM " + backupTable.getTableName() +
                    " WHERE id IN (SELECT id FROM " + backupTable.getTableName() +
                    " WHERE uuid = ? ORDER BY timestamp ASC LIMIT ?)";

            try (PreparedStatement statement = connection.prepareStatement(delete)) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, deleteAmount);
                statement.executeUpdate();
            }
        } finally {
            closeConnection();
        }
    }

    // Setters remain the same
    public void setMainInventory(ItemStack[] items) {
        this.mainInventory = SaveInventory.toBase64(items);
    }

    public void setArmour(ItemStack[] items) {
        this.armour = SaveInventory.toBase64(items);
    }

    public void setEnderChest(ItemStack[] items) {
        this.enderChest = SaveInventory.toBase64(items);
    }

    public void setXP(float xp) {
        this.xp = xp;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setFoodLevel(int foodLevel) {
        this.hunger = foodLevel;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public void setDeathReason(String deathReason) {
        this.deathReason = deathReason;
    }

    public void getRollbackMenuData() throws SQLException {
        openConnection();

        try {
            String query = "SELECT timestamp, death_reason, location_world, location_x, location_y, location_z " +
                    "FROM " + backupTable.getTableName() + " WHERE uuid = ? AND timestamp = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                statement.setLong(2, timestamp);

                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        world = results.getString("location_world");
                        x = results.getDouble("location_x");
                        y = results.getDouble("location_y");
                        z = results.getDouble("location_z");
                        deathReason = results.getString("death_reason");
                    }
                }
            }
        } finally {
            closeConnection();
        }
    }

    public void getAllBackupData() throws SQLException {
        openConnection();

        try {
            String query = "SELECT * FROM " + backupTable.getTableName() +
                    " WHERE uuid = ? AND timestamp = ?";


            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, uuid.toString());
                statement.setLong(2, timestamp);

                try (ResultSet results = statement.executeQuery()) {
                    if (results.next()) {
                        mainInventory = results.getString("main_inventory");
                        armour = results.getString("armour");
                        enderChest = results.getString("ender_chest");
                        xp = results.getFloat("xp");
                        health = results.getDouble("health");
                        hunger = results.getInt("hunger");
                        saturation = results.getFloat("saturation");
                        world = results.getString("location_world");
                        x = results.getDouble("location_x");
                        y = results.getDouble("location_y");
                        z = results.getDouble("location_z");
                        packageVersion = results.getString("version");
                        deathReason = results.getString("death_reason");
                    }
                }
            }
        } finally {
            closeConnection();
        }
    }

    // Getters remain the same
    public ItemStack[] getMainInventory() {
        return RestoreInventory.getInventoryItems(packageVersion, mainInventory);
    }

    public ItemStack[] getArmour() {
        return RestoreInventory.getInventoryItems(packageVersion, armour);
    }

    public ItemStack[] getEnderChest() {
        return RestoreInventory.getInventoryItems(packageVersion, enderChest);
    }

    public float getXP() {
        return this.xp;
    }

    public double getHealth() {
        return this.health;
    }

    public int getFoodLevel() {
        return this.hunger;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public String getWorld() {
        return this.world;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public String getVersion() {
        return this.packageVersion;
    }

    public String getDeathReason() {
        return this.deathReason;
    }

    public void saveData() throws SQLException {
        openConnection();

        try {
            String update = "INSERT INTO " + backupTable.getTableName() +
                    "(uuid, timestamp, xp, health, hunger, saturation, location_world, " +
                    "location_x, location_y, location_z, version, death_reason, " +
                    "main_inventory, armour, ender_chest) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(update)) {
                statement.setString(1, uuid.toString());
                statement.setLong(2, timestamp);
                statement.setFloat(3, xp);
                statement.setDouble(4, health);
                statement.setInt(5, hunger);
                statement.setFloat(6, saturation);
                statement.setString(7, world);
                statement.setDouble(8, x);
                statement.setDouble(9, y);
                statement.setDouble(10, z);
                statement.setString(11, packageVersion);
                statement.setString(12, deathReason);
                statement.setString(13, mainInventory);
                statement.setString(14, armour);
                statement.setString(15, enderChest);
                statement.executeUpdate();
            }
        } finally {
            closeConnection();
        }
    }
}