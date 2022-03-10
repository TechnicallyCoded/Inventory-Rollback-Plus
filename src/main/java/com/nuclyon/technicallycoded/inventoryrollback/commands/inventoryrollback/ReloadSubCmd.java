package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends IRPCommand {

    public ReloadSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.reload")) {
            main.startupTasks();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginReload());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

}
