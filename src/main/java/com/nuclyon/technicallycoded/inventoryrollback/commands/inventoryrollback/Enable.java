package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Enable extends IRPCommand {

    public Enable(InventoryRollback mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollback.enable")) {
            main.getConfigFile().setEnabled(true);
            main.getConfigFile().saveConfig();

            sender.sendMessage(MessageData.pluginName + MessageData.enabledMessage);
        } else {
            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
        }
        return;
    }

}
