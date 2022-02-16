package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.SaveInventory;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceBackupSubCmd extends IRPCommand {

    public ForceBackupSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.forcebackup")) {
            if (args.length == 1 || args.length > 3) {
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
                return;
            }

            if (args[1].equalsIgnoreCase("all")) {
                forceBackupAll(sender);
            } else if (args[1].equalsIgnoreCase("player")) {
                forceBackupPlayer(sender, args);
            } else {
                sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
            }
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
    }

    private void forceBackupAll(CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            new SaveInventory(player, LogType.FORCE, null, null, player.getInventory(), player.getEnderChest()).createSave();
        }

        sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getForceBackupAll());
    }

    private void forceBackupPlayer(CommandSender sender, String[] args) {
        if (args.length == 2) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getError());
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getPlayer(args[2]);

        if (offlinePlayer == null) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNotOnlineError(args[2]));
            return;
        }

        if (!offlinePlayer.isOnline()) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNotOnlineError(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        new SaveInventory(player, LogType.FORCE, null, null, player.getInventory(), player.getEnderChest()).createSave();

        sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getForceBackupPlayer(offlinePlayer.getName()));
    }

}
