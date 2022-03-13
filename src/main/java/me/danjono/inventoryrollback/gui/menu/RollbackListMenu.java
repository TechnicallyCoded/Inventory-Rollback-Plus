package me.danjono.inventoryrollback.gui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;

public class RollbackListMenu {

    private int pageNumber;

    private Player staff;
    private UUID playerUUID;
    private LogType logType;

    private Buttons buttons;
    private Inventory inventory;

    public RollbackListMenu(Player staff, OfflinePlayer player, LogType logType, int pageNumberIn) {
        this.staff = staff;
        this.playerUUID = player.getUniqueId();
        this.logType = logType;
        this.pageNumber = pageNumberIn;
        this.buttons = new Buttons(playerUUID);

        createInventory();
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(staff, InventoryName.ROLLBACK_LIST.getSize(), InventoryName.ROLLBACK_LIST.getName());

        List<String> lore = new ArrayList<>();
        if (pageNumber == 1) {
            ItemStack mainMenu = buttons.backButton(MessageData.getMainMenuButton(), logType, 0, null);
            inventory.setItem(InventoryName.ROLLBACK_LIST.getSize() - 8, mainMenu);
        }

        if (pageNumber > 1) {
            lore.add("Page " + (pageNumber - 1));
            ItemStack previousPage = buttons.backButton(MessageData.getPreviousPageButton(), logType, pageNumber - 1, lore);

            inventory.setItem(InventoryName.ROLLBACK_LIST.getSize() - 8, previousPage);
            lore.clear();
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void showBackups() {
        PlayerData playerData = new PlayerData(playerUUID, logType, null);

        //Check how many backups there are in total
        int backups = playerData.getAmountOfBackups();

        //How many rows are required
        int spaceRequired = InventoryName.ROLLBACK_LIST.getSize() - 9;

        //How many pages are required
        int pagesRequired = (int) Math.ceil(backups / (double) spaceRequired);

        //Check if pageNumber supplied is greater than pagesRequired, if true set to last page
        if (pageNumber > pagesRequired) {
            pageNumber = pagesRequired;
        } else if (pageNumber <= 0) {
            pageNumber = 1;
        }

        int backupsAlreadyPassed = spaceRequired * (pageNumber - 1);
        int backupsOnCurrentPage = Math.min(backups, Math.min(spaceRequired, backups - backupsAlreadyPassed));
        List<Long> timeStamps = playerData.getSelectedPageTimestamps(pageNumber);

        int position = 0;
        for (int i = 0; i < backupsOnCurrentPage; i++) {
            try {
                Long timestamp = timeStamps.get(i);
                playerData = new PlayerData(playerUUID, logType, timestamp);

                playerData.getRollbackMenuData();

                String displayName = MessageData.getDeathTime(PlayerData.getTime(timestamp));

                List<String> lore = new ArrayList<>();

                String deathReason = playerData.getDeathReason();
                if (deathReason != null)
                    lore.add(MessageData.getDeathReason(deathReason));

                String world = playerData.getWorld();
                double x = playerData.getX();
                double y = playerData.getY();
                double z = playerData.getZ();
                int ping = playerData.getPing();
                String location = world + "," + x + "," + y + "," + z;

                lore.add(MessageData.getDeathLocationWorld(world));
                lore.add(MessageData.getDeathLocationX(x));
                lore.add(MessageData.getDeathLocationY(y));
                lore.add(MessageData.getDeathLocationZ(z));
                lore.add(MessageData.getPing(ping).replace(" ", " ")); // I can't find the double space cause so here's a temporary fix

                ItemStack item = buttons.createInventoryButton(new ItemStack(Material.CHEST), logType, location, timestamp, displayName, lore);

                inventory.setItem(position, item);

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            position++;
        }

        List<String> lore = new ArrayList<>();
        if (pageNumber < pagesRequired) {
            lore.add("Page " + (pageNumber + 1));
            ItemStack nextPage = buttons.nextButton(MessageData.getNextPageButton(), logType, pageNumber + 1, lore);

            inventory.setItem(position + 7, nextPage);
            lore.clear();
        }
    }

}
