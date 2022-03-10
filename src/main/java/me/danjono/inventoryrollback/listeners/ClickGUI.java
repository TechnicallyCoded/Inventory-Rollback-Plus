package me.danjono.inventoryrollback.listeners;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import io.papermc.lib.PaperLib;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.config.SoundData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import me.danjono.inventoryrollback.gui.Buttons;
import me.danjono.inventoryrollback.gui.InventoryName;
import me.danjono.inventoryrollback.gui.menu.*;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBTWrapper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ClickGUI implements Listener {

    private final InventoryRollbackPlus main;

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

        //Check if inventory is a virtual one and not one that has the same name on a player chest
        if (this.main.getVersion().isAtLeast(EnumNmsVersion.v1_9_R1) && isLocationAvailable(e.getInventory().getLocation())) {
            return;
        }

        e.setCancelled(true);

        Player staff = (Player) e.getWhoClicked();
        ItemStack icon = e.getCurrentItem();

        //Listener for player menu
        if (title.equals(InventoryName.MAIN_MENU.getName())) {
            mainMenu(e,staff, icon);
        }

        //Listener for player menu
        else if (title.equals(InventoryName.PLAYER_MENU.getName())) {
            playerMenu(e,staff, icon);
        }

        //Listener for rollback list menu
        else if (title.equals(InventoryName.ROLLBACK_LIST.getName())) {
            rollbackMenu(e,staff, icon);
        }

        //Listener for main inventory backup menu
        else if (title.equals(InventoryName.MAIN_BACKUP.getName())) {
            mainBackupMenu(e,staff, icon);
        }

        //Listener for enderchest backup menu
        else if (title.equals(InventoryName.ENDER_CHEST_BACKUP.getName())) {
            enderChestBackupMenu(e,staff, icon);
        }

        else {
            e.setCancelled(true);
        }
    }

    private void mainMenu(InventoryClickEvent e, Player staff, ItemStack icon) {
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

    private void playerMenu(InventoryClickEvent e, Player staff, ItemStack icon) {
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

    private void rollbackMenu(InventoryClickEvent e, Player staff, ItemStack icon) {
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

                // Run all data retrieval operations async to avoid tick lag
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Init from MySQL or, if YAML, init & load config file
                        PlayerData data = new PlayerData(uuid, logType, timestamp);

                        // Get from MySQL
                        if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                            try {
                                data.getAllBackupData().get();
                            } catch (ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }

                        // Create inventory
                        MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);

                        // Display inventory to player
                        Future<InventoryView> inventoryViewFuture =
                                main.getServer().getScheduler().callSyncMethod(main,
                                        () -> staff.openInventory(menu.getInventory()));
                        //If the backup file is invalid it will return null, we want to catch it here
                        try {
                            inventoryViewFuture.get();
                            // Start placing items in the inventory async
                            menu.showBackupItems();
                        } catch (NullPointerException | ExecutionException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(main);
            } 

            //Player has selected a page icon
            else if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
                int page = nbt.getInt("page");

                //Selected to go back to main menu
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
                if (page == 0) {
                    PlayerMenu menu = new PlayerMenu(staff, player);

                    staff.openInventory(menu.getInventory());
                    Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getPlayerMenu);
                } else {
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

    private void mainBackupMenu(InventoryClickEvent e, Player staff, ItemStack icon) {
        if (!e.getView().getTitle().equals(InventoryName.MAIN_BACKUP.getName()))
            return;

        if (e.getRawSlot() >= (InventoryName.MAIN_BACKUP.getSize() - 9) && e.getRawSlot() < InventoryName.MAIN_BACKUP.getSize()) {
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));            
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            Long timestamp = nbt.getLong("timestamp");

            //Click on page selector button to go back to rollback menu
            if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
                RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1);

                staff.openInventory(menu.getInventory());
                Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackups);
            }

            //Clicked icon to overwrite player inventory with backup data
            else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Init from MySQL or, if YAML, init & load config file
                            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);

                            // Get data if using MySQL
                            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                                try {
                                    data.getAllBackupData().get();
                                } catch (ExecutionException | InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            ItemStack[] inventory = data.getMainInventory();
                            ItemStack[] armour = data.getArmour();

                            // Place inventory items sync (compressed code)
                            Future<Void> futureSetInv = main.getServer().getScheduler().callSyncMethod(main,
                                    () -> { player.getInventory().setContents(inventory); return null; });
                            try { futureSetInv.get(); }
                            catch (ExecutionException | InterruptedException ex) { ex.printStackTrace(); }

                            // If 1.8, place armor contents separately
                            if (main.getVersion().isNoHigherThan(EnumNmsVersion.v1_8_R3)) {
                                // Place items sync (compressed code)
                                Future<Void> futureSetArmor = main.getServer().getScheduler().callSyncMethod(main,
                                        () -> { player.getInventory().setArmorContents(armour); return null; });
                                try { futureSetArmor.get(); }
                                catch (ExecutionException | InterruptedException ex) { ex.printStackTrace(); }
                            }

                            // Play sound effect is enabled
                            if (SoundData.isInventoryRestoreEnabled()) {
                                // Play sound sync (compressed code)
                                Future<Void> futurePlaySound = main.getServer().getScheduler().callSyncMethod(main,
                                        () -> { player.playSound(player.getLocation(), SoundData.getInventoryRestored(), 1, 1); return null; });
                                try { futurePlaySound.get(); }
                                catch (ExecutionException | InterruptedException ex) { ex.printStackTrace(); }
                            }

                            // Send player & staff feedback
                            player.sendMessage(MessageData.getPluginPrefix() + MessageData.getMainInventoryRestoredPlayer(staff.getName()));
                            if (!staff.getUniqueId().equals(player.getUniqueId()))
                                staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getMainInventoryRestored(offlinePlayer.getName()));
                        }
                    }.runTaskAsynchronously(main);

                } else {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getMainInventoryNotOnline(offlinePlayer.getName()));
                }
            }

            // Clicked icon to teleport player to backup coordinates
            else if (icon.getType().equals(Buttons.getTeleportLocationIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore.teleport")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                String[] location = nbt.getString("location").split(",");			
                World world = Bukkit.getWorld(location[0]);

                if (world == null) {
                    //World is not available
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getDeathLocationInvalidWorldError(location[0]));
                    return;
                }

                Location loc = new Location(world, 
                        Math.floor(Double.parseDouble(location[1])), 
                        Math.floor(Double.parseDouble(location[2])), 
                        Math.floor(Double.parseDouble(location[3])))
                        .add(0.5, 0.5, 0.5);				

                // Teleport player on a slight delay to block the teleport icon glitching out into the player inventory
                Bukkit.getScheduler().runTaskLater(InventoryRollback.getInstance(), () -> {
                    e.getWhoClicked().closeInventory();
                    PaperLib.teleportAsync(staff,loc).thenAccept((result) -> {
                        if (SoundData.isTeleportEnabled())
                            staff.playSound(loc, SoundData.getTeleport(), 1, 1);

                        staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getDeathLocationTeleport(loc));
                    });
                }, 1L);
            } 

            // Clicked icon to restore backup players ender chest
            else if (icon.getType().equals(Buttons.getEnderChestIcon())) {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Init from MySQL or, if YAML, init & load config file
                        PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);

                        // Get data if using MySQL
                        if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                            try {
                                data.getAllBackupData().get();
                            } catch (ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }

                        // Create Inventory
                        EnderChestBackupMenu menu = new EnderChestBackupMenu(staff, data, 1);

                        // Open inventory sync (compressed code)
                        Future<Void> futureOpenInv = main.getServer().getScheduler().callSyncMethod(main,
                                () -> {
                                    staff.openInventory(menu.getInventory());
                                    return null;
                                });
                        try {
                            futureOpenInv.get();
                        } catch (ExecutionException | InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        // Place items async
                        menu.showEnderChestItems();
                    }
                }.runTaskAsynchronously(this.main);
            }

            // Clicked icon to restore backup players health
            else if (icon.getType().equals(Buttons.getHealthIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	
                    double health = nbt.getDouble("health");

                    player.setHealth(health);

                    if (SoundData.isFoodRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getFoodRestored(), 1, 1);

                    player.sendMessage(MessageData.getPluginPrefix() + MessageData.getHealthRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getHealthRestored(player.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getHealthNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players hunger
            else if (icon.getType().equals(Buttons.getHungerIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;	
                    int hunger = nbt.getInt("hunger");
                    Float saturation = nbt.getFloat("saturation");

                    player.setFoodLevel(hunger);
                    player.setSaturation(saturation);

                    if (SoundData.isHungerRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getHungerRestored(), 1, 1);

                    player.sendMessage(MessageData.getPluginPrefix() + MessageData.getHungerRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getHungerRestored(player.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getHungerNotOnline(offlinePlayer.getName()));
                }
            } 

            //Clicked icon to restore backup players experience
            else if (icon.getType().equals(Buttons.getExperienceIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                if (offlinePlayer.isOnline()) {				
                    Player player = (Player) offlinePlayer;	
                    Float xp = nbt.getFloat("xp");

                    RestoreInventory.setTotalExperience(player, xp);

                    if (SoundData.isExperienceRestoredEnabled())
                        player.playSound(player.getLocation(), SoundData.getExperienceSound(), 1, 1);

                    player.sendMessage(MessageData.getPluginPrefix() + MessageData.getExperienceRestoredPlayer(staff.getName(), xp.intValue()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getExperienceRestored(player.getName(), (int) RestoreInventory.getLevel(xp)));
                } else {				    
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getExperienceNotOnlinePlayer(offlinePlayer.getName()));
                }
            }
        } else {
            int slotIndex = e.getRawSlot();
            int topInvSize = e.getView().getTopInventory().getSize();
            boolean clickIsWithinPlayerInventory = slotIndex >= topInvSize;

            boolean clickIsWithinMainBackupInv = slotIndex < topInvSize - 18;
            boolean notInLastLine = slotIndex < topInvSize - 9;
            boolean notBeforeArmorSlots = slotIndex > topInvSize - 15;

            boolean clickIsWithinArmorOrOffHandSlots = notInLastLine && notBeforeArmorSlots;
            boolean isValidBackupMenuInteraction = clickIsWithinMainBackupInv || clickIsWithinArmorOrOffHandSlots;

            //Allow items to be grabbed in the top inventory except the bottom line AND NOT player inventory items to be shift clicked to top inventory
            if (clickIsWithinPlayerInventory && !e.isShiftClick()) {
                e.setCancelled(false);
            } else if (isValidBackupMenuInteraction) {
                if (staff.hasPermission("inventoryrollbackplus.restore")) {
                    e.setCancelled(false);
                } else {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                }
            }
        }
    }

    private void enderChestBackupMenu(InventoryClickEvent e, Player staff, ItemStack icon) {
        if (!e.getView().getTitle().equals(InventoryName.ENDER_CHEST_BACKUP.getName()))
            return;

        if (e.getRawSlot() >= (InventoryName.ENDER_CHEST_BACKUP.getSize() - 9) && e.getRawSlot() < InventoryName.ENDER_CHEST_BACKUP.getSize()) {
            NBTWrapper nbt = new NBTWrapper(icon);
            if (!nbt.hasUUID())
                return;

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            Long timestamp = nbt.getLong("timestamp");

            // Click on page selector button to go back to backup menu
            if (icon.getType().equals(Buttons.getPageSelectorIcon())) {

                //Player has selected a page icon
                int page = nbt.getInt("page");

                //Selected to go back to main menu
                if (page == 0) {

                    // Run all data retrieval operations async to avoid tick lag
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Init from MySQL or, if YAML, init & load config file
                            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);

                            // Get data if using MySQL
                            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                                try {
                                    data.getAllBackupData().get();
                                } catch (ExecutionException | InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            // Get location of where the backup was made from data
                            String location = data.getWorld() + "," + data.getX() + "," + data.getY() + "," + data.getZ();

                            // Create inventory
                            MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);

                            // Display inventory to player
                            Future<InventoryView> inventoryViewFuture = main.getServer().getScheduler().callSyncMethod(main,
                                    () -> staff.openInventory(menu.getInventory()));
                            //If the backup file is invalid it will return null, we want to catch it here
                            try {
                                inventoryViewFuture.get();
                                // Start placing items in the inventory async
                                menu.showBackupItems();
                            } catch (NullPointerException | ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(main);

                } else {

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Init from MySQL or, if YAML, init & load config file
                            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);

                            // Get data if using MySQL
                            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                                try {
                                    data.getAllBackupData().get();
                                } catch (ExecutionException | InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            // Create Inventory
                            EnderChestBackupMenu menu = new EnderChestBackupMenu(staff, data, page);

                            // Open inventory sync (compressed code)
                            Future<Void> futureOpenInv = main.getServer().getScheduler().callSyncMethod(main,
                                    () -> {
                                        staff.openInventory(menu.getInventory());
                                        return null;
                                    });
                            try {
                                futureOpenInv.get();
                            } catch (ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            }

                            // Place items async
                            menu.showEnderChestItems();
                        }
                    }.runTaskAsynchronously(this.main);
                }
            }

            //Clicked icon to overwrite player ender chest with backup data
            else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }

                if (offlinePlayer.isOnline()) {
                    Player player = (Player) offlinePlayer;

                    // Run all data retrieval operations async to avoid tick lag
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Init from MySQL or, if YAML, init & load config file
                            PlayerData data = new PlayerData(offlinePlayer, logType, timestamp);

                            // Get from MySQL
                            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                                try {
                                    data.getAllBackupData().get();
                                } catch (ExecutionException | InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            // Display inventory to player
                            Future<Void> inventoryReplaceFuture = main.getServer().getScheduler().callSyncMethod(main,
                                    () -> {
                                        player.getEnderChest().setContents(data.getEnderChest());
                                        return null;
                                    });

                            //If the backup file is invalid it will return null, we want to catch it here
                            try {
                                inventoryReplaceFuture.get();
                            } catch (NullPointerException | ExecutionException | InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.runTaskAsynchronously(main);

                    if (SoundData.isInventoryRestoreEnabled())
                        player.playSound(player.getLocation(), SoundData.getInventoryRestored(), 1, 1); 

                    player.sendMessage(MessageData.getPluginPrefix() + MessageData.getEnderChestRestoredPlayer(staff.getName()));
                    if (!staff.getUniqueId().equals(player.getUniqueId()))
                        staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getEnderChestRestored(offlinePlayer.getName()));
                } else {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getEnderChestNotOnline(offlinePlayer.getName()));
                }
            }
        } else {
            int slotIndex = e.getRawSlot();
            int topInvSize = e.getView().getTopInventory().getSize();
            boolean clickIsWithinPlayerInventory = slotIndex >= topInvSize;

            if (clickIsWithinPlayerInventory && !e.isShiftClick()) {
                e.setCancelled(false);
            } else if (slotIndex < topInvSize - 9) {
                // Perm check
                if (!staff.hasPermission("inventoryrollbackplus.restore")) {
                    staff.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
                    return;
                }
                e.setCancelled(false);
            }
        }
    }

}