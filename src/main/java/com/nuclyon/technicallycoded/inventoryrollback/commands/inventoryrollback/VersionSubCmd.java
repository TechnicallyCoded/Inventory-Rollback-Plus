package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class VersionSubCmd extends IRPCommand {

    public VersionSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        StringBuilder strb = new StringBuilder(MessageData.getPluginPrefix());
        boolean hasVersionPerm = sender.hasPermission("inventoryrollbackplus.version");

        strb.append("\n")
            .append(ChatColor.WHITE)
            .append("Plugin:").append("\n")
            .append(ChatColor.GRAY)
            .append("  Running InventoryRollbackPlus");
        // Can see version?
        if (hasVersionPerm) strb.append(" v").append(InventoryRollback.getPluginVersion());
        strb.append("\n");
        // Else show warning
        if (!hasVersionPerm)
            strb.append(ChatColor.GRAY)
                .append("  (Version not visible, lacking permission)")
                .append("\n");

        strb.append(ChatColor.WHITE)
            .append("Authors:").append("\n")
            .append(ChatColor.GRAY)
            .append("  - Maintained/updated by: TechnicallyCoded").append("\n")
            .append("  - Original author: danjono").append("\n")
            .append("\n")
            .append(ChatColor.WHITE).append("Update link:").append("\n")
            .append(ChatColor.BLUE).append(ChatColor.ITALIC).append("  https://www.spigotmc.org/resources/inventoryrollback-plus.85811/");


        // Send
        sender.sendMessage(strb.toString());
    }

}
