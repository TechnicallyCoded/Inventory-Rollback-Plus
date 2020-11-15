package com.nuclyon.technicallycoded.inventoryrollback.gui;

import com.nuclyon.technicallycoded.inventoryrollback.config.ConfigFile;
import com.nuclyon.technicallycoded.inventoryrollback.config.MessageData;
import com.nuclyon.technicallycoded.inventoryrollback.data.LogType;
import com.nuclyon.technicallycoded.inventoryrollback.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.UUID;

public class MainMenu {

    private final Player staff;
    private final OfflinePlayer player;

    public MainMenu(Player staff, OfflinePlayer player) {
        this.staff = staff;
        this.player = player;
    }

    public Inventory getMenu() {
        Inventory mainMenu = Bukkit.createInventory(staff, 9, InventoryName.MAIN_MENU.getName());
        Buttons buttons = new Buttons();

        UUID uuid = player.getUniqueId();

        File joinsFile = new PlayerData(player, LogType.JOIN).getFile();
        File quitsFile = new PlayerData(player, LogType.QUIT).getFile();
        File deathsFile = new PlayerData(player, LogType.DEATH).getFile();
        File worldChangeFile = new PlayerData(player, LogType.WORLD_CHANGE).getFile();
        File forceSaveFile = new PlayerData(player, LogType.FORCE).getFile();

        if (!joinsFile.exists()
                && !quitsFile.exists()
                && !deathsFile.exists()
                && !worldChangeFile.exists()
                && !forceSaveFile.exists()) {
            staff.sendMessage(MessageData.pluginName + new MessageData().noBackup(player.getName()));
            return null;
        }

        mainMenu.setItem(0, buttons.playerHead(player, null));

        int position = 2;

        if (deathsFile.exists()) {
            mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.deathIcon), uuid, MessageData.deathIconName, LogType.DEATH, null));
            position++;
        }

        if (joinsFile.exists()) {
            mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.joinIcon), uuid, MessageData.joinIconName, LogType.JOIN, null));
            position++;
        }

        if (quitsFile.exists()) {
            mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.quitIcon), uuid, MessageData.quitIconName, LogType.QUIT, null));
            position++;
        }

        if (worldChangeFile.exists()) {
            mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.worldChangeIcon), uuid, MessageData.worldChangeIconName, LogType.WORLD_CHANGE, null));
            position++;
        }

        if (forceSaveFile.exists()) {
            mainMenu.setItem(position, buttons.createLogTypeButton(new ItemStack(ConfigFile.forceSaveIcon), uuid, MessageData.forceSaveIconName, LogType.FORCE, null));
            //position++;
        }

        return mainMenu;
    }

}
