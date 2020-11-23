package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceBackup extends IRPCommand {

    public ForceBackup(InventoryRollback mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollback.forcebackup")) {
            if (args.length == 1 || args.length > 2) {
                sender.sendMessage(MessageData.pluginName + MessageData.error);
                return;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);

            if (!offlinePlayer.isOnline()) {
                sender.sendMessage(MessageData.pluginName + main.getConfigFile().getMsgData().notOnline(offlinePlayer.getName()));
                return;
            }

            Player player = (Player) offlinePlayer;
            new SaveInventory(player, LogType.FORCE, null, player.getInventory(), player.getEnderChest()).createSave();
            sender.sendMessage(MessageData.pluginName + main.getConfigFile().getMsgData().forceSaved(offlinePlayer.getName()));
        } else {
            sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
        }
    }

}
