package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Version extends IRPCommand {

    public Version(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        StringBuilder strb = new StringBuilder(MessageData.getPluginName());
        if (sender.hasPermission("inventoryrollbackplus.version") || sender.hasPermission("inventoryrollback.version")) {
            strb.append("Server is running InventoryRollbackPlus v")
                .append(InventoryRollback.getPluginVersion())
                .append(" - Maintained by TechnicallyCoded");
        } else {
            strb.append("Server is running InventoryRollbackPlus - Maintained by TechnicallyCoded")
                .append("\n")
                .append(ChatColor.GRAY)
                .append("(Version not visible, lacking permission)");
        }
        strb.append("\n")
            .append(ChatColor.WHITE)
            .append("Plugin download & updates link: ")
            .append(ChatColor.BLUE)
            .append(ChatColor.ITALIC)
            .append("https://www.spigotmc.org/resources/inventoryrollback-plus-1-8-1-16-x.85811/");
        sender.sendMessage(strb.toString());
    }

}
