package com.nuclyon.technicallycoded.inventoryrollback.util;

import me.danjono.inventoryrollback.data.LogType;

import java.util.HashMap;

public class UserLogRateLimiter {

    private static final long TICK_DURATION = 50; // ms
    private static final long BUFFER = Math.round(50 * 0.20d); // 10 ms - allow up to 20% faster than normal tick speeds
    private static final long MIN_INTERVAL_FROM_START_OF_LOG = (TICK_DURATION - BUFFER) * LogTimestamps.MAX_SIZE; // ms

    HashMap<LogType, LogTimestamps> saveLogs = new HashMap<>();

    public void log(LogType logType, Long timestamp) {
        LogTimestamps logTimestamps = saveLogs.get(logType);
        if (logTimestamps == null) {
            logTimestamps = new LogTimestamps();
            saveLogs.put(logType, logTimestamps);
        }
        logTimestamps.log(timestamp);
    }

    public boolean isRateLimitExceeded(LogType logType) {
        LogTimestamps logTimestamps = saveLogs.get(logType);
        if (logTimestamps == null) {
            return false;
        }

        if (!logTimestamps.isFull()) {
            return false;
        }

        Long first = logTimestamps.getFirst();
        if (first == null) {
            return false;
        }

        return System.currentTimeMillis() - first < MIN_INTERVAL_FROM_START_OF_LOG;
    }
}
