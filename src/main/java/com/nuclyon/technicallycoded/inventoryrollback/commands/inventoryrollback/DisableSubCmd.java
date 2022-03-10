package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DisableSubCmd extends IRPCommand {

    public DisableSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.disable")) {
            ConfigData.setEnabled(false);
            main.getConfigData().saveConfig();

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getPluginDisabled());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

}
