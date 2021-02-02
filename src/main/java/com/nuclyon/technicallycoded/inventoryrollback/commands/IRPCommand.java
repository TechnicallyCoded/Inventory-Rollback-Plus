package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class IRPCommand {

    public InventoryRollbackPlus main;

    public IRPCommand(InventoryRollbackPlus mainIn) {
        this.main = mainIn;
    }

    public abstract void onCommand(CommandSender sender, Command cmd, String label, String[] args);

}
