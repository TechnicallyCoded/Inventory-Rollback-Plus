package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Disable extends IRPCommand {

    public Disable(InventoryRollback mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollback.disable")) {
            main.getConfigFile().setEnabled(false);
            main.getConfigFile().saveConfig();

            sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
        } else {
            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
        }
    }

}
