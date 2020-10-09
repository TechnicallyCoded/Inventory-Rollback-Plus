package me.danjono.inventoryrollback.listeners;

import io.papermc.lib.PaperLib;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.InventoryRollback.VersionName;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.config.SoundData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.BackupMenu;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.gui.MainMenu;
import me.danjono.inventoryrollback.gui.RollbackListMenu;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClickGUI extends Buttons implements Listener {

    private Player staff;
    private ItemStack icon;

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        //Cancel listener if the event is not for an EpicFishing GUI menu
        String title = e.getView().getTitle();
        if (!title.equals(InventoryName.MAIN_MENU.getName())
                && !title.equalsIgnoreCase(InventoryName.ROLLBACK_LIST.getName())
                && !title.equalsIgnoreCase(InventoryName.BACKUP.getName()))
            return;

        e.setCancelled(true);

        //Check if inventory is a virtual one and not one that has the same name on a player chest
        if (InventoryRollback.getVersion() != VersionName.v1_8) {
            if (e.getInventory().getLocation() != null) {
                e.setCancelled(false);
                return;
            }
        }

        for (Integer slot : e.getRawSlots()) {
            if (slot >= e.getInventory().getSize()) {
                e.setCancelled(false);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!title.equals(InventoryName.MAIN_MENU.getName())
                && !title.equalsIgnoreCase(InventoryName.ROLLBACK_LIST.getName())
                && !title.equalsIgnoreCase(InventoryName.BACKUP.getName())) {
            return;
        }

        e.setCancelled(true);

        //Check if inventory is a virtual one and not one that has the same name on a player chest
        if (InventoryRollback.getVersion() != VersionName.v1_8) {
            if (e.getInventory().getLocation() != null) {
                e.setCancelled(false);
                return;
            }
        }

        staff = (Player) e.getWhoClicked();
        icon = e.getCurrentItem();

        //Listener for main menu
        if (title.equals(InventoryName.MAIN_MENU.getName())) {
            mainMenu(e);
        }

        //Listener for rollback list menu
        else if (title.equals(InventoryName.ROLLBACK_LIST.getName())) {
            rollbackMenu(e);
        }

        //Listener for backup menu
        else if (title.equals(InventoryName.BACKUP.getName())) {
            backupMenu(e);
        } else {
            e.setCancelled(true);
        }
    }

    private void mainMenu(InventoryClickEvent e) {
        //Return if a blank slot is selected
        if (icon == null)
            return;

        if ((e.getRawSlot() >= 0 && e.getRawSlot() < 9)) {
            NBT nbt = new NBT(icon);
            if (!nbt.hasUUID())
                return;

            LogType logType = LogType.valueOf(nbt.getString("logType"));
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

            final RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1, false);
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
                // Load data async
                menu.loadData();
                // Sync back to main
                Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> staff.openInventory(menu.showBackups()));
            });
        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private static boolean emptyEnderChest(Player player) {
        boolean empty = true;

        for (ItemStack itemStack : player.getEnderChest()) {
            if (itemStack != null) {
                empty = false;
                break;
            }
        }

        return empty;
    }

    private void rollbackMenu(InventoryClickEvent event) {

        int slot = event.getRawSlot();

        if (slot < 0 || slot > 45 || (slot > event.getInventory().getSize() && !event.isShiftClick())) {
            event.setCancelled(false);
            return;
        }

        NBT nbt = new NBT(icon);
        if (!nbt.hasUUID()) {
            return;
        }

        //Player has selected a backup to open
        if (icon.getType() == Material.CHEST) {


            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
                UUID uuid = UUID.fromString(nbt.getString("uuid"));
                long timestamp = nbt.getLong("timestamp");
                LogType logType = LogType.valueOf(nbt.getString("logType"));
                String location = nbt.getString("location");

                // Load the data on init since we are async from main
                FileConfiguration playerData = new PlayerData(uuid, logType, true).getData();

                // Does not access bukkit api
                RestoreInventory restore = new RestoreInventory(playerData, timestamp);

                // Deserialize contents
                ItemStack[] inventory = restore.retrieveMainInventory();
                ItemStack[] armour = restore.retrieveArmour();
                ItemStack[] enderchest = restore.retrieveEnderChestInventory();

                boolean hasEnderChest = false;

                if (enderchest == null || enderchest.length > 0) {
                    hasEnderChest = true;
                } else {
                    for (ItemStack is : enderchest) {
                        if (is != null) {
                            hasEnderChest = true;
                            break;
                        }
                    }
                }

                // Deserialize stats
                float xp = restore.getXP();
                double health = restore.getHealth();
                int hunger = restore.getHunger();
                float saturation = restore.getSaturation();

                // This fine because nothing is accessing bukkit api here
                final Inventory gui =
                    new BackupMenu(staff, uuid, logType, timestamp, inventory, armour, location, hasEnderChest, health, hunger, saturation, xp)
                        .showItems();
                if (gui != null) {
                    // Open the ui from main
                    Bukkit.getScheduler()
                        .runTask(InventoryRollback.getInstance(), () -> staff.openInventory(gui));
                }
            });
        }

        //Player has selected a page icon
        else if (icon.getType() == getPageSelectorIcon().getType()) {
            int page = nbt.getInt("page");

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
            if (page == 0) {
                staff.openInventory(new MainMenu(staff, player).getMenu());
            } else {
                LogType logType = LogType.valueOf(nbt.getString("logType"));

                final RollbackListMenu menu = new RollbackListMenu(staff, player, logType, page, false);
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> {
                    // Load data async
                    menu.loadData();
                    // Sync back to main
                    Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                        staff.openInventory(menu.showBackups());
                    });
                });
            }
        }

    }

    private void backupMenu(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(InventoryName.BACKUP.getName()))
            return;

        MessageData messages = new MessageData();

        final int slot = event.getRawSlot();
        final int size = event.getInventory().getSize();
        if (((slot < (size - 9) || slot >= size) && !event.isShiftClick()) || (slot < (size - 9) && event
            .isShiftClick())) {
            event.setCancelled(false);
            return;
        }

        if (slot <= 45 || slot > 54) {
            return;
        }
        NBT nbt = new NBT(icon);
        if (!nbt.hasUUID()) {
            return;
        }
        final Material iconType = icon.getType();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
        LogType logType = LogType.valueOf(nbt.getString("logType"));
        long timestamp = nbt.getLong("timestamp");

        final CompletableFuture<PlayerData> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), () -> future
            .complete(new PlayerData(offlinePlayer, logType, true)));

        final boolean emptyEnderchest =
            offlinePlayer.hasPlayedBefore() && emptyEnderChest(offlinePlayer.getPlayer());
        event.setCancelled(!emptyEnderchest);

        future.thenAccept(data -> {

            FileConfiguration playerData = data.getData();

            RestoreInventory restore = new RestoreInventory(playerData, timestamp);

            //Click on page selector button to go back to rollback menu
            if (icon.getType() == getPageSelectorIcon().getType()) {
                // Already async from main, load data this thread
                final RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1);
                // Sync back to main to open the inventory
                Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> staff.openInventory(menu.showBackups()));
            }

            //Clicked icon to teleport player to backup coordinates
            else if (iconType == getEnderPearlIcon().getType()) {
                String[] location = nbt.getString("location").split(",");
                World world = Bukkit.getWorld(location[0]);

                if (world == null) {
                    //World is not available
                    staff.sendMessage(
                        MessageData.pluginName + new MessageData().deathLocationInvalidWorld(location[0]));
                    return;
                }

                Location loc = new Location(world,
                    Double.parseDouble(location[1]) + 0.5, Double.parseDouble(location[2]),
                    Double.parseDouble(location[3]) + 0.5);

                //Teleport player on a slight delay to block the teleport icon glitching out into the player inventory
                Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                    event.getWhoClicked().closeInventory();
                    PaperLib.teleportAsync(staff, loc).thenAccept(result -> {
                        if (SoundData.enderPearlEnabled) {
                            staff.playSound(loc, SoundData.enderPearl, SoundData.enderPearlVolume, 1);
                        }

                        staff.sendMessage(MessageData.pluginName + messages.deathLocationTeleport(loc));
                    });
                }); // We're async so #runTask will run on the next tick anyway
            }

            //Clicked icon to restore backup players ender chest
            else if (icon.getType() == getEnderChestIcon().getType()) {
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;

                    ItemStack[] enderchest = restore.retrieveEnderChestInventory();

                    if (emptyEnderchest) {
                        Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                            player.getEnderChest().setContents(enderchest);

                            if (SoundData.enderChestEnabled) {
                                player.playSound(player
                                    .getLocation(), SoundData.enderChest, SoundData.enderChestVolume, 1);
                            }
                        });
                    } else {
                        staff.sendMessage(
                            MessageData.pluginName + messages.enderChestNotEmpty(player.getName()));
                        return;
                    }

                    staff.sendMessage(MessageData.pluginName + messages.enderChestRestored(player.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        player.sendMessage(MessageData.pluginName + messages.enderChestRestoredPlayer(staff.getName()));
                } else {
                    staff.sendMessage(MessageData.pluginName + messages.enderChestNotOnline(offlinePlayer.getName()));
                }
            }

            //Clicked icon to restore backup players health
            else if (icon.getType().equals(getHealthIcon().getType())) {

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    double health = nbt.getDouble("health");

                    Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                        player.setHealth(health);

                        if (SoundData.foodEnabled) {
                            player.playSound(player.getLocation(), SoundData.food, SoundData.foodVolume, 1);
                        }

                        staff.sendMessage(MessageData.pluginName + messages.healthRestored(player.getName()));
                        if (!staff.getUniqueId().equals(player.getUniqueId())) {
                            player.sendMessage(
                                MessageData.pluginName + messages.healthRestoredPlayer(staff.getName()));
                        }
                    });
                } else {
                    staff.sendMessage(MessageData.pluginName + messages.healthNotOnline(offlinePlayer.getName()));
                }
            }

            //Clicked icon to restore backup players hunger
            else if (icon.getType().equals(getHungerIcon().getType())) {

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    int hunger = nbt.getInt("hunger");
                    float saturation = nbt.getFloat("saturation");
                    Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                        player.setFoodLevel(hunger);
                        player.setSaturation(saturation);

                        if (SoundData.hungerEnabled) {
                            player
                                .playSound(player.getLocation(), SoundData.hunger, SoundData.hungerVolume, 1);
                        }

                        staff.sendMessage(MessageData.pluginName + messages.hungerRestored(player.getName()));
                        if (!staff.getUniqueId().equals(player.getUniqueId())) {
                            player.sendMessage(
                                MessageData.pluginName + messages.hungerRestoredPlayer(staff.getName()));
                        }
                    });
                } else {
                    staff.sendMessage(
                        MessageData.pluginName + messages.hungerNotOnline(offlinePlayer.getName()));
                }
            }

            //Clicked icon to restore backup players experience
            else if (icon.getType() == getExperienceIcon().getType()) {
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    float xp = nbt.getFloat("xp");

                    Bukkit.getScheduler().runTask(InventoryRollback.getInstance(), () -> {
                        RestoreInventory.setTotalExperience(player, xp);

                        if (SoundData.experienceEnabled) {
                            player.playSound(player
                                .getLocation(), SoundData.experience, SoundData.experienceVolume, 1);
                        }

                        staff.sendMessage(MessageData.pluginName + messages
                            .experienceRestored(player.getName(), RestoreInventory.getLevel(xp)));
                        if (!staff.getUniqueId().equals(player.getUniqueId()))
                            player.sendMessage(MessageData.pluginName + messages
                                .experienceRestoredPlayer(staff.getName(), (int) xp));
                    });
                } else {
                    staff.sendMessage(
                        MessageData.pluginName + messages.experienceNotOnline(offlinePlayer.getName()));
                }
            }
        });
    }

}
