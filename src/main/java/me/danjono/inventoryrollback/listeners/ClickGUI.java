package me.danjono.inventoryrollback.listeners;

import java.util.UUID;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.config.SoundData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.gui.menu.EnderChestBackupMenu;
import me.danjono.inventoryrollback.gui.menu.MainInventoryBackupMenu;
import me.danjono.inventoryrollback.gui.menu.MainMenu;
import me.danjono.inventoryrollback.gui.menu.PlayerMenu;
import me.danjono.inventoryrollback.gui.menu.RollbackListMenu;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBTWrapper;

public class ClickGUI implements Listener {

    private final InventoryRollbackPlus main;

    private Player staff;
    private ItemStack icon;

    private static boolean isLocationAvailable(Location location) {
        return location != null;
    }

    public ClickGUI() {
        this.main = InventoryRollbackPlus.getInstance();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        //Cancel listener if the event is not for an EpicFishing GUI menu
        String title = e.getView().getTitle();
        if (!title.equals(InventoryName.MAIN_MENU.getName()) 
                && !title.equals(InventoryName.PLAYER_MENU.getName()) 
                && !title.equalsIgnoreCase(InventoryName.ROLLBACK_LIST.getName())
                && !title.equalsIgnoreCase(InventoryName.MAIN_BACKUP.getName())
                && !title.equalsIgnoreCase(InventoryName.ENDER_CHEST_BACKUP.getName()))
            return;

        e.setCancelled(true);

        //Check if inventory is a virtual one and not one that has the same name on a player chest
        if (this.main.getVersion().isAtLeast(EnumNmsVersion.v1_9_R1) && isLocationAvailable(e.getInventory().getLocation())) {
            e.setCancelled(false);
            return;
        }

        for (Integer slot : e.getRawSlots()) {            
            if (slot < e.getInventory().getSize()) {
                return;
            }
        }

        e.setCancelled(false);
    }



    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!title.equals(InventoryName.MAIN_MENU.getName()) 
                && !title.equals(InventoryName.PLAYER_MENU.getName()) 
                && !title.equalsIgnoreCase(InventoryName.ROLLBACK_LIST.getName())
                && !title.equalsIgnoreCase(InventoryName.MAIN_BACKUP.getName())
                && !title.equalsIgnoreCase(InventoryName.ENDER_CHEST_BACKUP.getName()))
            return;

        e.setCancelled(true);

        //Check if inventory is a virtual one and not one that has the same name on a player chest
        if (this.main.getVersion().isAtLeast(EnumNmsVersion.v1_9_R1) && isLocationAvailable(e.getInventory().getLocation())) {
            e.setCancelled(false);
            return;
        }

        staff = (Player) e.getWhoClicked();
        icon = e.getCurrentItem();

        //Listener for player menu
        if (title.equals(InventoryName.MAIN_MENU.getName())) {
            mainMenu(e);
        }

        //Listener for player menu
        else if (title.equals(InventoryName.PLAYER_MENU.getName())) {
            playerMenu(e);
        }

        //Listener for rollback list menu
        else if (title.equals(InventoryName.ROLLBACK_LIST.getName())) {
            rollbackMenu(e);
        }

        //Listener for main inventory backup menu
        else if (title.equals(InventoryName.MAIN_BACKUP.getName())) {
            mainBackupMenu(e);
        }

        //Listener for enderchest backup menu
        else if (title.equals(InventoryName.ENDER_CHEST_BACKUP.getName())) {
            enderChestBackupMenu(e);
        }

        else {
            e.setCancelled(true);
        }
    }

    private void mainMenu(InventoryClickEvent e) {        
        if ((e.getRawSlot() >= 0 && e.getRawSlot() < InventoryName.MAIN_MENU.getSize())) {                
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            //Clicked a page button
            if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
                int page = nbt.getInt("page");

                //Selected to go back to main menu
                MainMenu menu = new MainMenu(staff, page);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getMainMenu);
            } 
            //Clicked a player head
            else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
                PlayerMenu menu = new PlayerMenu(staff, offlinePlayer);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getPlayerMenu);
            }
        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private void playerMenu(InventoryClickEvent e) {		
        //Return if a blank slot is selected
        if (icon == null)
            return;

        if ((e.getRawSlot() >= 0 && e.getRawSlot() < InventoryName.PLAYER_MENU.getSize())) {				
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

            //Clicked player head
            if (e.getRawSlot() == 0) {
                MainMenu menu = new MainMenu(staff, 1);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getMainMenu);
            } else {
                LogType logType = LogType.valueOf(nbt.getString("logType"));
                RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackups);
            }

        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private void rollbackMenu(InventoryClickEvent e) {
        if (e.getRawSlot() >= 0 && e.getRawSlot() < InventoryName.ROLLBACK_LIST.getSize()) {
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            //Player has selected a backup to open
            if (icon.getType().equals(Material.CHEST)) {
                UUID uuid = UUID.fromString(nbt.getString("uuid"));
                Long timestamp = nbt.getLong("timestamp");
                LogType logType = LogType.valueOf(nbt.getString("logType"));
                String location = nbt.getString("location");

                PlayerData data = new PlayerData(uuid, logType, timestamp);
                data.getAllBackupData();

                //If the backup file is invalid it will return null, we want to catch it here
                try {
                    MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);

                    staff.openInventory(menu.getInventory());
                    Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackupItems);
                } catch (NullPointerException ignored) {}
            } 

            //Player has selected a page icon
            else if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
                int page = nbt.getInt("page");

                //Selected to go back to main menu
                if (page == 0) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
                    PlayerMenu menu = new PlayerMenu(staff, player);

                    staff.openInventory(menu.getInventory());
                    Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getPlayerMenu);
                } else {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
                    LogType logType = LogType.valueOf(nbt.getString("logType"));
                    RollbackListMenu menu = new RollbackListMenu(staff, player, logType, page);

                    staff.openInventory(menu.getInventory());
                    Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackups);
                }
            }	
        } else {
            if (e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick()) {
                e.setCancelled(false);
            }
        }
    }

    private void mainBackupMenu(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(InventoryName.MAIN_BACKUP.getName()))
            return;

        if (e.getRawSlot() >= (InventoryName.MAIN_BACKUP.getSize() - 9) && e.getRawSlot() < InventoryName.MAIN_BACKUP.getSize()) {
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));            
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            Long timestamp = nbt.getLong("timestamp");

            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);		

            //Click on page selector button to go back to rollback menu
            if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
                RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackups);
            }

            //Clicked icon to overwrite player inventory with backup data
            else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;

                    data.getAllBackupData();

                    ItemStack[] inventory = data.getMainInventory();
                    ItemStack[] armour = data.getArmour();

                    player.getInventory().setContents(inventory);
                    if (this.main.getVersion().isNoHigherThan(EnumNmsVersion.v1_8_R3))
                        player.getInventory().setArmorContents(armour);

                    if (SoundData.isInventoryRestoreEnabled())
                        player.playSound(player.getLocation(), SoundData.getInventoryRestored(), 1, 1);

                    player.sendMessage(MessageData.getPluginName() + MessageData.getMainInventoryRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginName() + MessageData.getMainInventoryRestored(offlinePlayer.getName()));           
                } else {
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getMainInventoryNotOnline(offlinePlayer.getName()));
                }
            }

            //Clicked icon to teleport player to backup coordinates
            else if (icon.getType().equals(Buttons.getTeleportLocationIcon())) {
                String[] location = nbt.getString("location").split(",");			
                World world = Bukkit.getWorld(location[0]);

                if (world == null) {
                    //World is not available
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getDeathLocationInvalidWorldError(location[0]));
                    return;
                }

                Location loc = new Location(world, 
                        Math.floor(Double.parseDouble(location[1])), 
                        Math.floor(Double.parseDouble(location[2])), 
                        Math.floor(Double.parseDouble(location[3])))
                        .add(0.5, 0.5, 0.5);				

                //Teleport player on a slight delay to block the teleport icon glitching out into the player inventory
                Bukkit.getScheduler().runTaskLater(InventoryRollback.getInstance(), () -> {
                    e.getWhoClicked().closeInventory();
                    PaperLib.teleportAsync(staff,loc).thenAccept((result) -> {
                        if (SoundData.isTeleportEnabled())
                            staff.playSound(loc, SoundData.getTeleport(), 1, 1);

                        staff.sendMessage(MessageData.getPluginName() + MessageData.getDeathLocationTeleport(loc));
                    });
                }, 1L);
            } 

            //Clicked icon to restore backup players ender chest
            else if (icon.getType().equals(Buttons.getEnderChestIcon())) {
                data.getAllBackupData();

                EnderChestBackupMenu menu = new EnderChestBackupMenu(staff, data);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showEnderChestItems);
            } 

            //Clicked icon to restore backup players health
            else if (icon.getType().equals(Buttons.getHealthIcon())) {

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	
                    double health = nbt.getDouble("health");

                    player.setHealth(health);

                    if (SoundData.isFoodRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getFoodRestored(), 1, 1);

                    player.sendMessage(MessageData.getPluginName() + MessageData.getHealthRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginName() + MessageData.getHealthRestored(player.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getHealthNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players hunger
            else if (icon.getType().equals(Buttons.getHungerIcon())) {

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	
                    int hunger = nbt.getInt("hunger");
                    Float saturation = nbt.getFloat("saturation");

                    player.setFoodLevel(hunger);
                    player.setSaturation(saturation);

                    if (SoundData.isHungerRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getHungerRestored(), 1, 1);

                    player.sendMessage(MessageData.getPluginName() + MessageData.getHungerRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginName() + MessageData.getHungerRestored(player.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getHungerNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players experience
            else if (icon.getType().equals(Buttons.getExperienceIcon())) {
                if (offlinePlayer.isOnline()) {				
                    Player player = (Player) offlinePlayer;	
                    Float xp = nbt.getFloat("xp");

                    RestoreInventory.setTotalExperience(player, xp);

                    if (SoundData.isExperienceRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getExperienceSound(), 1, 1);

                    player.sendMessage(MessageData.getPluginName() + MessageData.getExperienceRestoredPlayer(staff.getName(), xp.intValue()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginName() + MessageData.getExperienceRestored(player.getName(), (int) RestoreInventory.getLevel(xp))); 
                } else {				    
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getExperienceNotOnlinePlayer(offlinePlayer.getName()));
                }
            }
        } else {             
            if (
                    //Allow items to be grabbed in the top inventory except the bottom line AND NOT player inventory items to be shift clicked to top inventory
                    ((e.getRawSlot() < (e.getInventory().getSize() - 18) || (e.getRawSlot() < (e.getInventory().getSize() - 9) && e.getRawSlot() > (e.getInventory().getSize() - 15)))
                            || e.getRawSlot() >= e.getInventory().getSize() && !e.isShiftClick())

                    //Allow items to be grabbed in the top inventory except the bottom line AND allow them to be shift clicked to player inventory
                    || ((e.getRawSlot() < (e.getInventory().getSize() - 18) || (e.getRawSlot() < (e.getInventory().getSize() - 9) && e.getRawSlot() > (e.getInventory().getSize() - 15)))
                            && e.isShiftClick())) {
                e.setCancelled(false);
            }
        }
    }

    private void enderChestBackupMenu(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(InventoryName.ENDER_CHEST_BACKUP.getName()))
            return;

        if (e.getRawSlot() >= (InventoryName.ENDER_CHEST_BACKUP.getSize() - 9) && e.getRawSlot() < InventoryName.ENDER_CHEST_BACKUP.getSize()) {
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            Long timestamp = nbt.getLong("timestamp");

            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp); 
            data.getAllBackupData();

            //Click on page selector button to go back to backup menu
            if (icon.getType().equals(Buttons.getPageSelectorIcon())) {           
                String location = data.getWorld() + "," + data.getX() + "," + data.getY() + "," + data.getZ();
                MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackupItems);
            }

            //Clicked icon to overwrite player ender chest with backup data
            else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;
                    player.getEnderChest().setContents(data.getEnderChest());

                    if (SoundData.isInventoryRestoreEnabled())
                        player.playSound(player.getLocation(), SoundData.getInventoryRestored(), 1, 1); 

                    player.sendMessage(MessageData.getPluginName() + MessageData.getEnderChestRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginName() + MessageData.getEnderChestRestored(offlinePlayer.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginName() + MessageData.getEnderChestNotOnline(offlinePlayer.getName()));
                }
            }
        } else {             
            if (((e.getRawSlot() < (e.getInventory().getSize() - 9) || e.getRawSlot() >= e.getInventory().getSize()) && !e.isShiftClick())
                    || (e.getRawSlot() < (e.getInventory().getSize() - 9) && e.isShiftClick())) {
                e.setCancelled(false);
            }
        }
    }

}