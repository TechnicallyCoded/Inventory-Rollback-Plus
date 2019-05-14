package me.danjono.inventoryrollback.listeners;

import java.util.ListIterator;
import java.util.UUID;

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
import org.bukkit.inventory.ItemStack;

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
        } 

        else {
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

            staff.openInventory(new RollbackListMenu(staff, offlinePlayer, logType, 1).showBackups());
        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private void rollbackMenu(InventoryClickEvent e) {
        if (e.getRawSlot() >= 0 && e.getRawSlot() < 45) {
            NBT nbt = new NBT(icon);
            if (!nbt.hasUUID())
                return;

            //Player has selected a backup to open
            if (icon.getType().equals(Material.CHEST)) {
                UUID uuid = UUID.fromString(nbt.getString("uuid"));
                Long timestamp = nbt.getLong("timestamp");
                LogType logType = LogType.valueOf(nbt.getString("logType"));
                String location = nbt.getString("location");

                FileConfiguration playerData = new PlayerData(uuid, logType).getData();

                RestoreInventory restore = new RestoreInventory(playerData, timestamp);

                ItemStack[] inventory = restore.retrieveMainInventory();
                ItemStack[] armour = restore.retrieveArmour();

                boolean enderchest = false;				
                for (ItemStack item : restore.retrieveEnderChestInventory()) {
                    if (item != null)
                        enderchest = true;
                }

                Float xp = restore.getXP();
                Double health = restore.getHealth();
                int hunger = restore.getHunger();
                float saturation = restore.getSaturation();

                //If the backup file is invalid it will return null, we want to catch it here
                try {
                    staff.openInventory(new BackupMenu(staff, uuid, logType, timestamp, inventory, armour, location, enderchest, health, hunger, saturation, xp).showItems());
                } catch (NullPointerException e1) {}
            } 

            //Player has selected a page icon
            else if (icon.getType().equals(getPageSelectorIcon().getType())) {
                int page = nbt.getInt("page");

                if (page == 0) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

                    staff.openInventory(new MainMenu(staff, player).getMenu());
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
                    LogType logType = LogType.valueOf(nbt.getString("logType"));

                    staff.openInventory(new RollbackListMenu(staff, player, logType, page).showBackups());
                }
            }	
        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private void backupMenu(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(InventoryName.BACKUP.getName()))
            return;

        MessageData messages = new MessageData();

        if (e.getRawSlot() >= 45 && e.getRawSlot() < 54) {
            NBT nbt = new NBT(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            Long timestamp = nbt.getLong("timestamp");

            PlayerData data = new PlayerData(offlinePlayer, logType);		
            FileConfiguration playerData = data.getData();

            RestoreInventory restore = new RestoreInventory(playerData, timestamp);

            //Click on page selector button to go back to rollback menu
            if (icon.getType().equals(getPageSelectorIcon().getType())) {
                staff.openInventory(new RollbackListMenu(staff, offlinePlayer, logType, 1).showBackups());
            } 

            //Clicked icon to teleport player to backup coordinates
            else if (icon.getType().equals(getEnderPearlIcon().getType())) {
                String[] location = nbt.getString("location").split(",");			
                World world = Bukkit.getWorld(location[0]);

                if (world == null) {
                    //World is not available
                    staff.sendMessage(MessageData.pluginName + new MessageData().deathLocationInvalidWorld(location[0]));
                    return;
                }

                Location loc = new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])).add(0.5, 0, 0.5);				
                
                //Teleport player on a slight delay to block the teleport icon glitching out into the player inventory
                Bukkit.getScheduler().runTaskLater(InventoryRollback.getInstance(), () -> {
                    e.getWhoClicked().closeInventory();
                    staff.teleport(loc);
                    
                    if (SoundData.enderPearlEnabled)
                        staff.playSound(loc, SoundData.enderPearl, SoundData.enderPearlVolume, 1);

                    staff.sendMessage(MessageData.pluginName + messages.deathLocationTeleport(loc));
                }, 1L);
            } 

            //Clicked icon to restore backup players ender chest
            else if (icon.getType().equals(getEnderChestIcon().getType())) {
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	

                    ItemStack[] enderchest = restore.retrieveEnderChestInventory();

                    if (emptyEnderChest(player)) {
                        player.getEnderChest().setContents(enderchest);

                        if (SoundData.enderChestEnabled)
                            player.playSound(player.getLocation(), SoundData.enderChest, SoundData.enderChestVolume, 1);
                    } else {
                        staff.sendMessage(MessageData.pluginName + messages.enderChestNotEmpty(player.getName()));
                        e.setCancelled(true);
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
                    Double health = nbt.getDouble("health");

                    player.setHealth(health);

                    if (SoundData.foodEnabled)
                        player.playSound(player.getLocation(), SoundData.food, SoundData.foodVolume, 1);

                    staff.sendMessage(MessageData.pluginName + messages.healthRestored(player.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        player.sendMessage(MessageData.pluginName + messages.healthRestoredPlayer(staff.getName()));
                } else {
                    staff.sendMessage(MessageData.pluginName + messages.healthNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players hunger
            else if (icon.getType().equals(getHungerIcon().getType())) {

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	
                    int hunger = nbt.getInt("hunger");
                    Float saturation = nbt.getFloat("saturation");

                    player.setFoodLevel(hunger);
                    player.setSaturation(saturation);

                    if (SoundData.hungerEnabled)
                        player.playSound(player.getLocation(), SoundData.hunger, SoundData.hungerVolume, 1);

                    staff.sendMessage(MessageData.pluginName + messages.hungerRestored(player.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        player.sendMessage(MessageData.pluginName + messages.hungerRestoredPlayer(staff.getName()));
                } else {
                    staff.sendMessage(MessageData.pluginName + messages.hungerNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players experience
            else if (icon.getType().equals(getExperienceIcon().getType())) {
                if (offlinePlayer.isOnline()) {				
                    Player player = (Player) offlinePlayer;	
                    Float xp = nbt.getFloat("xp");

                    RestoreInventory.setTotalExperience(player, xp);

                    if (SoundData.experienceEnabled)
                        player.playSound(player.getLocation(), SoundData.experience, SoundData.experienceVolume, 1);

                    staff.sendMessage(MessageData.pluginName + messages.experienceRestored(player.getName(), (int) RestoreInventory.getLevel(xp)));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        player.sendMessage(MessageData.pluginName + messages.experienceRestoredPlayer(staff.getName(), xp.intValue()));
                } else {				    
                    staff.sendMessage(MessageData.pluginName + messages.experienceNotOnline(offlinePlayer.getName()));
                }
            }
        } else {             
            if (((e.getRawSlot() < (e.getInventory().getSize() - 9) || e.getRawSlot() >= e.getInventory().getSize()) && !e.isShiftClick())
                    || (e.getRawSlot() < (e.getInventory().getSize() - 9) && e.isShiftClick())) {
                e.setCancelled(false);
            }
        }
    }

    private boolean emptyEnderChest(Player player) {
        boolean empty = true;
        ListIterator<ItemStack> ec = player.getEnderChest().iterator();

        while (ec.hasNext()) {
            if (ec.next() != null) {							
                empty = false;
                break;
            }
        }

        return empty;
    }

}