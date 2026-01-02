package me.danjono.inventoryrollback.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for YAML purge functionality.
 * Uses temp directories to test file deletion logic without Bukkit dependencies.
 */
public class YAMLPurgeTest {

    @TempDir
    Path tempDir;

    private File playerBackupFolder;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a mock player backup folder structure
        playerBackupFolder = tempDir.resolve("deaths").resolve("00000000-0000-0000-0000-000000000001").toFile();
        playerBackupFolder.mkdirs();
    }

    @AfterEach
    public void tearDown() {
        // Cleanup is handled by @TempDir
    }

    // ===== Helper methods =====

    private File createBackupFile(long timestamp) throws IOException {
        File file = new File(playerBackupFolder, timestamp + ".yml");
        Files.write(file.toPath(), "test: data".getBytes());
        return file;
    }

    // ===== purgeOlderThan logic tests =====

    @Test
    public void testPurgeOlderThanDeletesOldFiles() throws IOException {
        long now = System.currentTimeMillis();
        long oldTimestamp = now - (2 * 24 * 60 * 60 * 1000); // 2 days ago
        long recentTimestamp = now - (1 * 60 * 60 * 1000); // 1 hour ago

        File oldFile = createBackupFile(oldTimestamp);
        File recentFile = createBackupFile(recentTimestamp);

        assertTrue(oldFile.exists(), "Old file should exist before purge");
        assertTrue(recentFile.exists(), "Recent file should exist before purge");

        // Simulate purge logic: delete files older than 1 day
        long cutoffTimestamp = now - (1 * 24 * 60 * 60 * 1000);
        int deletedCount = purgeOlderThanInFolder(playerBackupFolder, cutoffTimestamp);

        assertEquals(1, deletedCount, "Should delete 1 old file");
        assertFalse(oldFile.exists(), "Old file should be deleted");
        assertTrue(recentFile.exists(), "Recent file should still exist");
    }

    @Test
    public void testPurgeOlderThanEmptyFolder() {
        // Empty folder - nothing to delete
        long cutoffTimestamp = System.currentTimeMillis();
        int deletedCount = purgeOlderThanInFolder(playerBackupFolder, cutoffTimestamp);

        assertEquals(0, deletedCount, "Should delete 0 files in empty folder");
    }

    @Test
    public void testPurgeOlderThanSkipsNonYmlFiles() throws IOException {
        long oldTimestamp = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000);
        
        // Create a non-yml file
        File txtFile = new File(playerBackupFolder, oldTimestamp + ".txt");
        Files.write(txtFile.toPath(), "test".getBytes());
        
        // Create a yml file
        File ymlFile = createBackupFile(oldTimestamp);

        long cutoffTimestamp = System.currentTimeMillis();
        int deletedCount = purgeOlderThanInFolder(playerBackupFolder, cutoffTimestamp);

        assertEquals(1, deletedCount, "Should only delete yml file");
        assertTrue(txtFile.exists(), "Non-yml file should not be deleted");
        assertFalse(ymlFile.exists(), "Yml file should be deleted");
    }

    // ===== purgeAllSaves logic tests =====

    @Test
    public void testPurgeAllSavesDeletesAllFiles() throws IOException {
        createBackupFile(1000000000000L);
        createBackupFile(2000000000000L);
        createBackupFile(3000000000000L);

        File[] filesBefore = playerBackupFolder.listFiles();
        assertNotNull(filesBefore);
        assertEquals(3, filesBefore.length, "Should have 3 files before purge");

        int deletedCount = purgeAllInFolder(playerBackupFolder);

        assertEquals(3, deletedCount, "Should delete all 3 files");
        
        File[] filesAfter = playerBackupFolder.listFiles();
        assertNotNull(filesAfter);
        assertEquals(0, filesAfter.length, "Should have 0 files after purge");
    }

    @Test
    public void testPurgeAllSavesOnNonExistentFolder() {
        File nonExistent = new File(tempDir.toFile(), "nonexistent");
        int deletedCount = purgeAllInFolder(nonExistent);
        assertEquals(0, deletedCount, "Should return 0 for non-existent folder");
    }

    // ===== Helper methods that mirror YAML.java logic =====

    /**
     * Mirrors the purgeOlderThan logic from YAML.java
     */
    private int purgeOlderThanInFolder(File folder, long olderThanTimestamp) {
        if (!folder.exists()) return 0;

        File[] files = folder.listFiles();
        if (files == null) return 0;

        int deletedCount = 0;
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".yml")) continue;

            int pos = file.getName().lastIndexOf('.');
            if (pos <= 0) continue;
            String fileName = file.getName().substring(0, pos);

            long saveTimeStamp;
            try {
                saveTimeStamp = Long.parseLong(fileName);
            } catch (NumberFormatException ex) {
                continue;
            }

            if (saveTimeStamp < olderThanTimestamp) {
                if (file.delete()) {
                    deletedCount++;
                }
            }
        }

        return deletedCount;
    }

    /**
     * Mirrors the purgeAllSaves logic from YAML.java
     */
    private int purgeAllInFolder(File folder) {
        if (!folder.exists()) return 0;

        File[] files = folder.listFiles();
        if (files == null) return 0;

        int deletedCount = 0;
        for (File file : files) {
            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".yml")) continue;

            if (file.delete()) {
                deletedCount++;
            }
        }

        return deletedCount;
    }
}
