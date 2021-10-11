package me.danjono.inventoryrollback.gui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderChestBackupMenu {

    private int pageNumber;

    private Player staff;
    private UUID playerUUID;
    private LogType logType;
    private Long timestamp;
    private ItemStack[] enderchest;

    private Buttons buttons;
    private Inventory inventory;

    public EnderChestBackupMenu(Player staff, PlayerData data, int pageNumberIn) {
        this.staff = staff;
        this.playerUUID = data.getOfflinePlayer().getUniqueId();
        this.logType = data.getLogType();
        this.timestamp = data.getTimestamp();
        this.enderchest = data.getEnderChest();
        this.pageNumber = pageNumberIn;
        this.buttons = new Buttons(playerUUID);
        
        createInventory();
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(staff, InventoryName.ENDER_CHEST_BACKUP.getSize(), InventoryName.ENDER_CHEST_BACKUP.getName());

        List<String> lore = new ArrayList<>();
        if (pageNumber == 1) {
            ItemStack mainInventoryMenu = buttons.inventoryMenuBackButton(MessageData.getBackButton(), logType, timestamp);
            inventory.setItem(InventoryName.ENDER_CHEST_BACKUP.getSize() - 8, mainInventoryMenu);
        }

        if (pageNumber > 1) {
            lore.add("Page " + (pageNumber - 1));
            ItemStack previousPage = buttons.enderChestBackButton(MessageData.getPreviousPageButton(), logType, pageNumber - 1, timestamp, lore);

            inventory.setItem(InventoryName.ENDER_CHEST_BACKUP.getSize() - 8, previousPage);
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void showEnderChestItems() {
        //Check how many items there are in total
        int itemsToDisplay = enderchest.length;

        // How many rows are available
        int spaceAvailable = InventoryName.ROLLBACK_LIST.getSize() - 9;

        // How many pages are required
        int pagesRequired = Math.max(1, (int) Math.ceil(itemsToDisplay / (double) spaceAvailable));

        //Check if pageNumber supplied is greater then pagesRequired, if true set to last page
        if (pageNumber > pagesRequired) {
            pageNumber = pagesRequired;
        } else if (pageNumber <= 0) {
            pageNumber = 1;
        }

        //If the backup file is invalid it will return null, we want to catch it here
        try {

            // Add items, 5 per tick
            new BukkitRunnable() {

                int invPosition = 0;
                int itemPos = (pageNumber - 1) * 27;
                final int max = Math.max(0, itemPos + Math.min(enderchest.length - itemPos, 27)); // excluded but starts from 0

                @Override
                public void run() {
                    for (int i = 0; i < 6; i++) {
                        // If hit max item position, stop
                        if (itemPos >= max) {
                            this.cancel();
                            return;
                        }


                        ItemStack itemStack = enderchest[itemPos];
                        if (itemStack != null) {
                            inventory.setItem(invPosition, itemStack);
                            // Don't change inv position if there was nothing to place
                            invPosition++;
                        }
                        // Move to next item stack
                        itemPos++;
                    }
                }
            }.runTaskTimer(InventoryRollbackPlus.getInstance(), 0, 1);
        } catch (NullPointerException e) {
            staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getErrorInventory());
            return;
        }

        // Add restore all player inventory button
        if (ConfigData.isRestoreToPlayerButton()) {
            inventory.setItem(
                    InventoryName.ENDER_CHEST_BACKUP.getSize() - 5,
                    buttons.restoreAllInventory(logType, timestamp));
        } else {
            inventory.setItem(
                    InventoryName.ENDER_CHEST_BACKUP.getSize() - 5,
                    buttons.restoreAllInventoryDisabled(logType, timestamp));
        }


        List<String> lore = new ArrayList<>();
        if (pageNumber < pagesRequired) {
            lore.add("Page " + (pageNumber + 1));
            ItemStack nextPage = buttons.enderChestNextButton(MessageData.getNextPageButton(), logType, pageNumber + 1, timestamp, lore);

            inventory.setItem(InventoryName.ENDER_CHEST_BACKUP.getSize() - 2, nextPage);
            lore.clear();
        }
    }

}
