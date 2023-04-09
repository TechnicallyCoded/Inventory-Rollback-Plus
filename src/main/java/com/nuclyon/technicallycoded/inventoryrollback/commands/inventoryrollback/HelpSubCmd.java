package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpSubCmd extends IRPCommand {

    public HelpSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("inventoryrollbackplus.help")) {
            this.sendHelp(sender);
        } else {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
        }
        return;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(
                MessageData.getPluginPrefix() + ChatColor.GRAY + "InventoryRollbackPlus - by TechnicallyCoded\n" +
                        ChatColor.WHITE + "  Available Commands:\n" +
                        ChatColor.WHITE + "    /irp restore [player]" + ChatColor.GRAY + " - Open rollback GUI for optional [player]\n" +
                        ChatColor.WHITE + "    /irp forcebackup <all/player> [player]" + ChatColor.GRAY + " - Create a forced save of a player's inventory\n" +
                        ChatColor.WHITE + "    /irp enable" + ChatColor.GRAY + " - Enable the plugin\n" +
                        ChatColor.WHITE + "    /irp disable" + ChatColor.GRAY + " - Disable the plugin\n" +
                        ChatColor.WHITE + "    /irp reload" + ChatColor.GRAY + " - Reload the plugin\n" +
                        ChatColor.WHITE + "    /irp help" + ChatColor.GRAY + " - Get this message\n" +
                        ChatColor.WHITE + "    /irp version" + ChatColor.GRAY + " - Get plugin info & version\n");
    }

}
