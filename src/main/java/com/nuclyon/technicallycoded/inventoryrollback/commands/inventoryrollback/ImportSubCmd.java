package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import com.nuclyon.technicallycoded.inventoryrollback.util.BackupConversionUtil;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicBoolean;

public class ImportSubCmd extends IRPCommand {

    private static final AtomicBoolean suggestConfirm = new AtomicBoolean(false);

    public ImportSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.import")) {

            // Check that player confirms this operation
            if (args.length < 2 || !args[1].equalsIgnoreCase("confirm")) {
                // Send player help
                sender.sendMessage(ChatColor.RED + "/" + label.toLowerCase() + " import " + ChatColor.BOLD + "confirm");

                // Handle suggestions
                suggestConfirm.set(true);

                // Reset suggestion availability after 10 seconds
                InventoryRollbackPlus.getScheduler().runTaskLaterAsynchronously(() -> {
                    suggestConfirm.set(false);
                }, 10 * 20);

                return;
            }

            // Execute import
            Bukkit.getScheduler().runTaskAsynchronously(main, BackupConversionUtil::convertOldBackupData);

            // Reset suggestion to not visible
            suggestConfirm.set(false);

            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getImportSuccess());
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
        return;
    }

    public static boolean shouldShowConfirmOption() {
        return suggestConfirm.get();
    }

}
