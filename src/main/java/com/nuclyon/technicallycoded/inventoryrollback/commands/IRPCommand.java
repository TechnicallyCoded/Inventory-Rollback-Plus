package com.nuclyon.technicallycoded.inventoryrollback.commands;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public abstract class IRPCommand {

    public InventoryRollback main;

    public IRPCommand(InventoryRollback mainIn) {
        this.main = mainIn;
    }

    public abstract void onCommand(CommandSender sender, Command cmd, String label, String[] args);

}
