package me.danjono.inventoryrollback.data;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQL {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" +
                ConfigData.getMySQLHost() + ":" +
                ConfigData.getMySQLPort() + "/" +
                ConfigData.getMySQLDatabase() +
                "?useSSL=" + ConfigData.isMySQLUseSSL() +
                "&verifyServerCertificate=" + ConfigData.isMySQLVerifyCertificate() +
                "&allowPublicKeyRetrieval=" + ConfigData.isMySQLPubKeyRetrievalAllowed() +
                "&characterEncoding=UTF-8");
        config.setUsername(ConfigData.getMySQLUsername());
        config.setPassword(ConfigData.getMySQLPassword());
        config.setMaximumPoolSize(ConfigData.getMySQLPoolMaximumPoolSize());
        config.setMinimumIdle(ConfigData.getMySQLPoolMinimumIdle());
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(ConfigData.getMySQLPoolConnectionTimeout());
        config.setLeakDetectionThreshold(60000);
        dataSource = new HikariDataSource(config);
    }

    private final UUID uuid;
    private final Long timestamp;
    private final BackupTable backupTable;

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

    public enum BackupTable {
        DEATH(ConfigData.getMySQLTablePrefix() + "deaths"),
        JOIN(ConfigData.getMySQLTablePrefix() + "joins"),
        QUIT(ConfigData.getMySQLTablePrefix() + "quits"),
        WORLD_CHANGE(ConfigData.getMySQLTablePrefix() + "world_changes"),
        FORCE(ConfigData.getMySQLTablePrefix() + "force_backups");

        private final String tableName;

        BackupTable(String tableName) {
            this.tableName = tableName;
        }

        public String getTableName() {
            return this.tableName;
        }
    }

    public MySQL(UUID uuid, LogType logType, Long timestamp) {
        this.uuid = uuid;
        this.logType = logType;
        this.timestamp = timestamp;
        this.backupTable = getBackupTable();
    }

    public static void convertYAMLToMySQL() {
        List<File> backupLocations = new ArrayList<>();
        File backupArea = new File(ConfigData.getFolderLocation().getAbsoluteFile(), YAML.getBackupFolderName());

        backupLocations.add(new File(backupArea, "deaths"));
        backupLocations.add(new File(backupArea, "joins"));
        backupLocations.add(new File(backupArea, "quits"));
        backupLocations.add(new File(backupArea, "worldChanges"));
        backupLocations.add(new File(backupArea, "force"));

        List<LogType> logTypeFiles = new ArrayList<>();
        logTypeFiles.add(LogType.DEATH);
        logTypeFiles.add(LogType.JOIN);
        logTypeFiles.add(LogType.QUIT);
        logTypeFiles.add(LogType.WORLD_CHANGE);
        logTypeFiles.add(LogType.FORCE);

        for (int i = 0; i < backupLocations.size(); i++) {
            File backupType = backupLocations.get(i);
            LogType logType = logTypeFiles.get(i);

            InventoryRollbackPlus.getInstance().getConsoleSender()
                    .sendMessage("[InventoryRollbackPlus] Converting the backup location " + logType.name());

            if (backupType == null || !backupType.exists())
                continue;

            File[] uuidBackups = backupType.listFiles();
            if (uuidBackups == null)
                continue;

            for (File UUIDBackup : uuidBackups) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(UUIDBackup.getName());
                } catch (IllegalArgumentException e) {
                    continue;
                }

                File[] backups = UUIDBackup.listFiles();
                if (backups == null)
                    continue;

                for (File backup : backups) {
                    String fileName = backup.getName();
                    int pos = fileName.lastIndexOf(".");
                    if (pos == -1)
                        continue;

                    fileName = fileName.substring(0, pos);
                    if (!org.apache.commons.lang3.StringUtils.isNumeric(fileName))
                        continue;

                    Long timestamp = Long.parseLong(fileName);

                    YAML yaml = new YAML(uuid, logType, timestamp);
                    MySQL mysql = new MySQL(uuid, logType, timestamp);

                    mysql.setMainInventory(yaml.getMainInventory());
                    mysql.setArmour(yaml.getArmour());
                    mysql.setEnderChest(yaml.getEnderChest());
                    mysql.setXP(yaml.getXP());
                    mysql.setHealth(yaml.getHealth());
                    mysql.setFoodLevel(yaml.getFoodLevel());
                    mysql.setSaturation(yaml.getSaturation());
                    mysql.setWorld(yaml.getWorld());
                    mysql.setX(yaml.getX());
                    mysql.setY(yaml.getY());
                    mysql.setZ(yaml.getZ());
                    mysql.setVersion(yaml.getVersion());
                    mysql.setDeathReason(yaml.getDeathReason());

                    try {
                        mysql.saveData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        InventoryRollbackPlus.getInstance().getConsoleSender()
                .sendMessage("[InventoryRollbackPlus] Conversion completed!");
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
        } else {
            throw new IllegalArgumentException("Unknown log type: " + logType);
        }
    }

    public static void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            for (BackupTable table : BackupTable.values()) {
                String tableQuery = "CREATE TABLE IF NOT EXISTS " + table.getTableName() + " (" +
                        "`id` INT NOT NULL AUTO_INCREMENT," +
                        "`uuid` VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                        "`timestamp` DOUBLE NOT NULL," +
                        "`xp` FLOAT NOT NULL," +
                        "`health` DOUBLE NOT NULL," +
                        "`hunger` INT NOT NULL," +
                        "`saturation` FLOAT NOT NULL," +
                        "`location_world` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                        "`location_x` DOUBLE NOT NULL," +
                        "`location_y` DOUBLE NOT NULL," +
                        "`location_z` DOUBLE NOT NULL," +
                        "`version` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
                        "`death_reason` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci," +
                        "`main_inventory` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci," +
                        "`armour` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci," +
                        "`ender_chest` LONGTEXT CHARACTER SET utf8 COLLATE utf8_general_ci," +
                        "PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                try (PreparedStatement stmt = conn.prepareStatement(tableQuery)) {
                    stmt.executeUpdate();
                }

                createIndices(conn, table.getTableName());
            }
        }
    }

    private static void createIndices(Connection conn, String tableName) {
        String indexName = "idx_" + tableName + "_uuid_timestamp";
        try {
            String indexQuery = "CREATE INDEX " + indexName + " ON " + tableName + " (uuid, timestamp)";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(indexQuery);
                InventoryRollbackPlus.getInstance().getConsoleSender().sendMessage("[InventoryRollbackPlus] Created index: " + indexName);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1061) {
                InventoryRollbackPlus.getInstance().getConsoleSender()
                        .sendMessage("[InventoryRollbackPlus] Index verified (exists): " + indexName);
            } else {
                InventoryRollbackPlus.getInstance().getLogger()
                        .warning("Could not create index for table " + tableName + ": " + e.getMessage());
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean doesBackupTypeExist() throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "SELECT EXISTS(SELECT 1 FROM " + backupTable.getTableName() + " WHERE uuid = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    return rs.getBoolean(1);
                }
            }
        }
    }

    public int getAmountOfBackups() throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(id) FROM " + backupTable.getTableName() + " WHERE uuid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        }
    }

    public List<Long> getSelectedPageTimestamps(int pageNumber) throws SQLException {
        try (Connection conn = getConnection()) {
            List<Long> timestamps = new ArrayList<>();
            int backups = InventoryName.ROLLBACK_LIST.getSize() - 9;
            int offset = Math.max((pageNumber - 1) * backups, 0);
            String query = "SELECT timestamp FROM " + backupTable.getTableName() +
                    " WHERE uuid = ? ORDER BY timestamp DESC LIMIT ?, ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setInt(2, offset);
                stmt.setInt(3, backups);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next())
                        timestamps.add(rs.getLong(1));
                }
            }
            return timestamps;
        }
    }

    public void purgeExcessSaves(int deleteAmount) throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String delete = "DELETE FROM " + backupTable.getTableName()
                    + " WHERE uuid = ? ORDER BY timestamp ASC LIMIT ?";
            try (PreparedStatement stmt = conn.prepareStatement(delete)) {
                stmt.setString(1, uuid.toString());
                stmt.setInt(2, deleteAmount);
                stmt.executeUpdate();
            }
            conn.commit();
        }
    }

    public void saveData() throws SQLException {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String insert = "INSERT INTO " + backupTable.getTableName() + " " +
                    "(uuid, timestamp, xp, health, hunger, saturation, location_world, location_x, location_y, location_z, version, death_reason, main_inventory, armour, ender_chest) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, timestamp);
                stmt.setFloat(3, xp);
                stmt.setDouble(4, health);
                stmt.setInt(5, hunger);
                stmt.setFloat(6, saturation);
                stmt.setString(7, world);
                stmt.setDouble(8, x);
                stmt.setDouble(9, y);
                stmt.setDouble(10, z);
                stmt.setString(11, packageVersion);
                stmt.setString(12, deathReason);
                stmt.setString(13, mainInventory);
                stmt.setString(14, armour);
                stmt.setString(15, enderChest);
                stmt.executeUpdate();
            }
            conn.commit();
        }
    }

    public void getRollbackMenuData() throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "SELECT timestamp,death_reason,location_world,location_x,location_y,location_z FROM " +
                    backupTable.getTableName() + " WHERE uuid = ? AND timestamp = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, timestamp);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        world = rs.getString("location_world");
                        x = rs.getDouble("location_x");
                        y = rs.getDouble("location_y");
                        z = rs.getDouble("location_z");
                        deathReason = rs.getString("death_reason");
                    }
                }
            }
        }
    }

    public void getAllBackupData() throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM " + backupTable.getTableName() + " WHERE uuid = ? AND timestamp = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, timestamp);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        mainInventory = rs.getString("main_inventory");
                        armour = rs.getString("armour");
                        enderChest = rs.getString("ender_chest");
                        xp = rs.getFloat("xp");
                        health = rs.getDouble("health");
                        hunger = rs.getInt("hunger");
                        saturation = rs.getFloat("saturation");
                        world = rs.getString("location_world");
                        x = rs.getDouble("location_x");
                        y = rs.getDouble("location_y");
                        z = rs.getDouble("location_z");
                        packageVersion = rs.getString("version");
                        deathReason = rs.getString("death_reason");
                    }
                }
            }
        }
    }

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
}
