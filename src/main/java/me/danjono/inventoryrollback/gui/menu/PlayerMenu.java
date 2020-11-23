package me.danjono.inventoryrollback.gui.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;

public class PlayerMenu {

    private Player staff;
    private OfflinePlayer offlinePlayer;

    private Buttons buttons;
    private Inventory inventory;

    public PlayerMenu(Player staff, OfflinePlayer player) {
        this.staff = staff;
        this.offlinePlayer = player;
        this.buttons = new Buttons(player.getUniqueId());

        createInventory();
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(staff, InventoryName.PLAYER_MENU.getSize(), InventoryName.PLAYER_MENU.getName());
        
        inventory.setItem(2, buttons.createDeathLogButton(LogType.DEATH, null));
        inventory.setItem(3, buttons.createJoinLogButton(LogType.JOIN, null));
        inventory.setItem(4, buttons.createQuitLogButton(LogType.QUIT, null));
        inventory.setItem(5, buttons.createWorldChangeLogButton(LogType.WORLD_CHANGE, null));
        inventory.setItem(6, buttons.createForceSaveLogButton(LogType.FORCE, null));
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void getPlayerMenu() {
        List<String> lore = new ArrayList<>();
        
        if (offlinePlayer.isOnline()) {
            lore.add(ChatColor.GREEN + "Online now");
        } else if (!offlinePlayer.hasPlayedBefore()) {
            lore.add(ChatColor.RED + "Never played on this server");
        } else {
            lore.add(ChatColor.RED + "Offline");
            
            String dateTime = "Unknown";
            if (offlinePlayer.getLastPlayed() != 0)
                dateTime = PlayerData.getTime(offlinePlayer.getLastPlayed());
            lore.add(ChatColor.RED + "Last online: " + dateTime);
        }
        
        inventory.setItem(0, buttons.playerHead(lore, true));
        UUID uuid = offlinePlayer.getUniqueId();

        PlayerData deathBackup = new PlayerData(uuid, LogType.DEATH, null);
        PlayerData joinBackup = new PlayerData(uuid, LogType.JOIN, null);
        PlayerData quitBackup = new PlayerData(uuid, LogType.QUIT, null);
        PlayerData worldChangeBackup = new PlayerData(uuid, LogType.WORLD_CHANGE, null);
        PlayerData forceSaveBackup = new PlayerData(uuid, LogType.FORCE, null);

        if (!joinBackup.doesBackupTypeExist()
                && !quitBackup.doesBackupTypeExist()
                && !deathBackup.doesBackupTypeExist()
                && !worldChangeBackup.doesBackupTypeExist()
                && !forceSaveBackup.doesBackupTypeExist()) {

            //No backups have been found for the player
            staff.sendMessage(MessageData.getPluginName() + MessageData.getNoBackupError(offlinePlayer.getName()));
        }
        
        String backupsAvailable = " backup(s) available";

        List<String> deaths = Arrays.asList(deathBackup.getAmountOfBackups() + backupsAvailable);
        inventory.setItem(2, buttons.createDeathLogButton(LogType.DEATH, deaths));
        
        List<String> joins = Arrays.asList(joinBackup.getAmountOfBackups() + backupsAvailable);
        inventory.setItem(3, buttons.createJoinLogButton(LogType.JOIN, joins));
        
        List<String> quits = Arrays.asList(quitBackup.getAmountOfBackups() + backupsAvailable);
        inventory.setItem(4, buttons.createQuitLogButton(LogType.QUIT, quits));
        
        List<String> worldChange = Arrays.asList(worldChangeBackup.getAmountOfBackups() + backupsAvailable);
        inventory.setItem(5, buttons.createWorldChangeLogButton(LogType.WORLD_CHANGE, worldChange));
        
        List<String> forceSaves = Arrays.asList(forceSaveBackup.getAmountOfBackups() + backupsAvailable);
        inventory.setItem(6, buttons.createForceSaveLogButton(LogType.FORCE, forceSaves));
    }

}
