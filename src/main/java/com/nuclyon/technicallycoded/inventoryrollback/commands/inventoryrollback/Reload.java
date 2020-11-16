package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Reload extends IRPCommand {

    public Reload(InventoryRollback mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollback.reload")) {
            main.startupTasks();

            sender.sendMessage(MessageData.pluginName + MessageData.reloadMessage);
        } else {
            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
        }
    }

}
