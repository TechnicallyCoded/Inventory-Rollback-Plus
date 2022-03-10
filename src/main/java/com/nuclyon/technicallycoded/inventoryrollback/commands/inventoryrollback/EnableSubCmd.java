package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class EnableSubCmd extends IRPCommand {

    public EnableSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.enable")) {
            main.getConfigData().setEnabled(true);
            main.getConfigData().saveConfig();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginEnabled());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
        return;
    }

}
