package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollback;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.gui.MainMenu;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Restore extends IRPCommand {

    public Restore(InventoryRollback mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("inventoryrollback.restore")) {
                if (!ConfigFile.enabled) {
                    sender.sendMessage(MessageData.pluginName + MessageData.disabledMessage);
                    return;
                }

                Player staff = (Player) sender;

                if (args.length == 1) {
                    try {
                        staff.openInventory(new MainMenu(staff, staff).getMenu());
                    } catch (NullPointerException ignored) {
                    }
                } else if (args.length == 2) {
                    @SuppressWarnings("deprecation")
                    OfflinePlayer rollbackPlayer = Bukkit.getOfflinePlayer(args[1]);

                    try {
                        staff.openInventory(new MainMenu(staff, rollbackPlayer).getMenu());
                    } catch (NullPointerException ignored) {
                    }
                } else {
                    sender.sendMessage(MessageData.pluginName + MessageData.error);
                }
            } else {
                sender.sendMessage(MessageData.pluginName + MessageData.noPermission);
            }
        } else {
            sender.sendMessage(MessageData.pluginName + MessageData.playerOnly);
        }
    }

}
