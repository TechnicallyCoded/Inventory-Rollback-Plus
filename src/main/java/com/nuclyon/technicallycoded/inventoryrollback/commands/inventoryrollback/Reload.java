package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Reload extends IRPCommand {

    public Reload(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.reload") || sender.hasPermission("inventoryrollback.reload")) {
            main.startupTasks();

            sender.sendMessage(MessageData.getPluginName() + MessageData.getPluginReload());
        } else {
            sender.sendMessage(MessageData.getPluginName() + MessageData.getNoPermission());
        }
    }

}
