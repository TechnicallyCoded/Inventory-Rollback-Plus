package com.nuclyon.technicallycoded.inventoryrollback.util.test;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.tcoded.lightlibs.bukkitversion.MCVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelfTest {

    private final String name;
    private final MCVersion minVersion;
    private final MCVersion maxVersion;
    private final Runnable test;
    private final List<String> logs;

    public SelfTest(String name, Consumer<List<String>> test) {
        this(name, MCVersion.v1_8_8, MCVersion.getLatest(), test);
    }

    public SelfTest(String name, MCVersion minVersion, Consumer<List<String>> test) {
        this(name, minVersion, MCVersion.getLatest(), test);
    }

    public SelfTest(String name, MCVersion minVersion, MCVersion maxVersion, Consumer<List<String>> test) {
        this.name = name;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.logs = new ArrayList<>();
        this.test = () -> test.accept(logs);
    }

    public SelfTest(String name, Runnable test) {
        this(name, MCVersion.v1_8_8, MCVersion.getLatest(), test);
    }

    public SelfTest(String name, MCVersion minVersion, MCVersion maxVersion, Runnable test) {
        this.name = name;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.test = test;
        this.logs = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void run() {
        try {
            test.run();
        } catch (Throwable t) {
            Logger logger = InventoryRollbackPlus.getInstance().getLogger();
            logger.log(Level.SEVERE, "Test failed with exception: " + test, t);

            logger.severe("Logs:");
            for (String log : this.getLogs()) {
                logger.severe(" - " + log);
            }
        }
    }

    public List<String> getLogs() {
        return logs;
    }

    public MCVersion getMinVersion() {
        return minVersion;
    }

    public MCVersion getMaxVersion() {
        return maxVersion;
    }

}
