package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback.PurgeSubCmd;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PurgeSubCmd duration parsing and formatting.
 */
public class PurgeSubCmdTest {

    // ===== parseDuration tests =====

    @Test
    public void testParseDurationDaysOnly() {
        long result = PurgeSubCmd.parseDuration("30d");
        assertEquals(30L * 24 * 60 * 60 * 1000, result, "30 days should be correct milliseconds");
    }

    @Test
    public void testParseDurationHoursOnly() {
        long result = PurgeSubCmd.parseDuration("12h");
        assertEquals(12L * 60 * 60 * 1000, result, "12 hours should be correct milliseconds");
    }

    @Test
    public void testParseDurationMinutesOnly() {
        long result = PurgeSubCmd.parseDuration("45m");
        assertEquals(45L * 60 * 1000, result, "45 minutes should be correct milliseconds");
    }

    @Test
    public void testParseDurationDaysAndHours() {
        long result = PurgeSubCmd.parseDuration("7d12h");
        long expected = (7L * 24 * 60 * 60 * 1000) + (12L * 60 * 60 * 1000);
        assertEquals(expected, result, "7 days 12 hours should be correct");
    }

    @Test
    public void testParseDurationAllUnits() {
        long result = PurgeSubCmd.parseDuration("1d6h30m");
        long expected = (1L * 24 * 60 * 60 * 1000) + (6L * 60 * 60 * 1000) + (30L * 60 * 1000);
        assertEquals(expected, result, "1 day 6 hours 30 minutes should be correct");
    }

    @Test
    public void testParseDurationInvalidFormat() {
        assertEquals(-1, PurgeSubCmd.parseDuration("invalid"), "Invalid string should return -1");
        assertEquals(-1, PurgeSubCmd.parseDuration("30x"), "Unknown unit should return -1");
        assertEquals(-1, PurgeSubCmd.parseDuration(""), "Empty string should return -1");
    }

    @Test
    public void testParseDurationZeroValues() {
        // Empty string matches pattern but has no groups, resulting in totalMs = 0
        assertEquals(-1, PurgeSubCmd.parseDuration("0d"), "0 days results should be sensible or -1");
    }

    // ===== formatDuration tests =====

    @Test
    public void testFormatDurationDaysOnly() {
        long durationMs = 30L * 24 * 60 * 60 * 1000;
        String result = PurgeSubCmd.formatDuration(durationMs);
        assertEquals("30 day(s)", result);
    }

    @Test
    public void testFormatDurationHoursOnly() {
        long durationMs = 12L * 60 * 60 * 1000;
        String result = PurgeSubCmd.formatDuration(durationMs);
        assertEquals("12 hour(s)", result);
    }

    @Test
    public void testFormatDurationMinutesOnly() {
        long durationMs = 45L * 60 * 1000;
        String result = PurgeSubCmd.formatDuration(durationMs);
        assertEquals("45 minute(s)", result);
    }

    @Test
    public void testFormatDurationMixed() {
        long durationMs = (7L * 24 * 60 * 60 * 1000) + (12L * 60 * 60 * 1000) + (30L * 60 * 1000);
        String result = PurgeSubCmd.formatDuration(durationMs);
        assertEquals("7 day(s) 12 hour(s) 30 minute(s)", result);
    }

    @Test
    public void testFormatDurationZero() {
        String result = PurgeSubCmd.formatDuration(0);
        assertEquals("", result, "Zero duration should return empty string");
    }
}
