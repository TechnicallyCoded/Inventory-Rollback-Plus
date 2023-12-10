package com.nuclyon.technicallycoded.inventoryrollback.util;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LogTimestamps {

    public static final int MAX_SIZE = 5;

    private final ConcurrentLinkedQueue<Long> timestamps = new ConcurrentLinkedQueue<>();

    public void log(Long timestamp) {
        timestamps.add(timestamp);
        while (timestamps.size() > MAX_SIZE) {
            timestamps.poll();
        }
    }

    public Long getFirst() {
        return timestamps.peek();
    }

    public boolean isFull() {
        return timestamps.size() >= MAX_SIZE;
    }
}
