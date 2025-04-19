package com.nuclyon.technicallycoded.inventoryrollback.util.test;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelfTest {

    private final String name;
    private final Runnable test;
    private final List<String> logs;

    public SelfTest(String name, Consumer<List<String>> test) {
        this.name = name;
        this.logs = new ArrayList<>();
        this.test = () -> test.accept(logs);
    }

    public SelfTest(String name, Runnable test) {
        this.name = name;
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

}
