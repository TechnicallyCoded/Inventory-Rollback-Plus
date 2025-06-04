package me.danjono.inventoryrollback.listeners;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.customdata.CustomDataItemEditor;
import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClickGUI implements Listener {

    private final InventoryRollbackPlus main;
    private static final String PLUGIN_PREFIX = MessageData.getPluginPrefix();

    public ClickGUI() {
        this.main = InventoryRollbackPlus.getInstance();
    }

    private boolean isPluginInventory(String title) {
        return title.equals(InventoryName.MAIN_MENU.getName()) 
                || title.equals(InventoryName.PLAYER_MENU.getName()) 
                || title.equalsIgnoreCase(InventoryName.ROLLBACK_LIST.getName())
                || title.equalsIgnoreCase(InventoryName.MAIN_BACKUP.getName())
                || title.equalsIgnoreCase(InventoryName.ENDER_CHEST_BACKUP.getName());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        String title = e.getView().getTitle();
        if (!isPluginInventory(title)) return;

        boolean inTopInventory = e.getRawSlots().stream()
                .anyMatch(slot -> slot < e.getInventory().getSize());
        
        e.setCancelled(inTopInventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!isPluginInventory(title)) return;

        if (main.getVersion().greaterOrEqThan(BukkitVersion.v1_9_R1) && 
            e.getInventory().getLocation() != null) {
            return;
        }

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player staff) || e.getCurrentItem() == null) return;

        if (title.equals(InventoryName.MAIN_MENU.getName())) {
            mainMenu(e, staff);
        } else if (title.equals(InventoryName.PLAYER_MENU.getName())) {
            playerMenu(e, staff);
        } else if (title.equals(InventoryName.ROLLBACK_LIST.getName())) {
            rollbackMenu(e, staff);
        } else if (title.equals(InventoryName.MAIN_BACKUP.getName())) {
            mainBackupMenu(e, staff);
        } else if (title.equals(InventoryName.ENDER_CHEST_BACKUP.getName())) {
            enderChestBackupMenu(e, staff);
        }
    }

    private void mainMenu(InventoryClickEvent e, Player staff) {
        if (!isValidClick(e)) return;
        
        ItemStack icon = e.getCurrentItem();
        CustomDataItemEditor nbt = CustomDataItemEditor.editItem(icon);
        if (!nbt.hasUUID()) return;

        if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
            int page = nbt.getInt("page");
            MainMenu menu = new MainMenu(staff, page);
            staff.openInventory(menu.getInventory());
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getMainMenu);
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
            PlayerMenu menu = new PlayerMenu(staff, offlinePlayer);
            staff.openInventory(menu.getInventory());
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::getPlayerMenu);
        }
    }

    private void playerMenu(InventoryClickEvent e, Player staff) {
        ItemStack icon = e.getCurrentItem();
        if (icon == null || !isValidClick(e)) return;
        
        CustomDataItemEditor nbt = CustomDataItemEditor.editItem(icon);
        if (!nbt.hasUUID()) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));

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
    }

    private void rollbackMenu(InventoryClickEvent e, Player staff) {
        ItemStack icon = e.getCurrentItem();
        if (icon == null || !isValidClick(e)) return;
        
        CustomDataItemEditor nbt = CustomDataItemEditor.editItem(icon);
        if (!nbt.hasUUID()) return;

        if (icon.getType().equals(Material.CHEST)) {
            UUID uuid = UUID.fromString(nbt.getString("uuid"));
            long timestamp = nbt.getLong("timestamp");
            LogType logType = LogType.valueOf(nbt.getString("logType"));
            String location = nbt.getString("location");

            loadAndOpenBackupMenu(staff, uuid, logType, timestamp, location);
        } else if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
            handlePagination(e, staff, nbt);
        }
    }

    private void loadAndOpenBackupMenu(Player staff, UUID uuid, LogType logType, long timestamp, String location) {
        CompletableFuture.supplyAsync(() -> {
            PlayerData data = new PlayerData(uuid, logType, timestamp);
            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                try {
                    data.getAllBackupData().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return data;
        }, main).thenAccept(data -> {
            MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);
            main.getServer().getScheduler().runTask(main, () -> {
                staff.openInventory(menu.getInventory());
                menu.showBackupItems();
            });
        });
    }

    private void handlePagination(InventoryClickEvent e, Player staff, CustomDataItemEditor nbt) {
        int page = nbt.getInt("page");
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

    private void mainBackupMenu(InventoryClickEvent e, Player staff) {
        if (!e.getView().getTitle().equals(InventoryName.MAIN_BACKUP.getName())) return;

        ItemStack icon = e.getCurrentItem();
        int slot = e.getRawSlot();
        int size = e.getInventory().getSize();
        boolean isBottomRow = slot >= (size - 9) && slot < size;

        if (isBottomRow && icon != null) {
            handleBottomRowActions(e, staff, icon);
        } else {
            handleInventoryInteractions(e, staff);
        }
    }

    private void handleBottomRowActions(InventoryClickEvent e, Player staff, ItemStack icon) {
        CustomDataItemEditor nbt = CustomDataItemEditor.editItem(icon);
        if (!nbt.hasUUID()) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
        LogType logType = LogType.valueOf(nbt.getString("logType"));
        long timestamp = nbt.getLong("timestamp");

        if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
            RollbackListMenu menu = new RollbackListMenu(staff, offlinePlayer, logType, 1);
            staff.openInventory(menu.getInventory());
            Bukkit.getScheduler().runTaskAsynchronously(InventoryRollback.getInstance(), menu::showBackups);
        } else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
            restoreInventory(staff, offlinePlayer, logType, timestamp);
        } else if (icon.getType().equals(Buttons.getTeleportLocationIcon())) {
            teleportToLocation(e, staff, nbt);
        } else if (icon.getType().equals(Buttons.getEnderChestIcon())) {
            openEnderChest(staff, offlinePlayer, logType, timestamp);
        } else if (icon.getType().equals(Buttons.getHealthIcon())) {
            restoreHealth(staff, offlinePlayer, nbt);
        } else if (icon.getType().equals(Buttons.getHungerIcon())) {
            restoreHunger(staff, offlinePlayer, nbt);
        } else if (icon.getType().equals(Buttons.getExperienceIcon())) {
            restoreExperience(staff, offlinePlayer, nbt);
        }
    }

    private void restoreInventory(Player staff, OfflinePlayer offlinePlayer, LogType logType, long timestamp) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore")) return;
        
        if (!offlinePlayer.isOnline()) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getMainInventoryNotOnline(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        loadPlayerDataAsync(offlinePlayer, logType, timestamp).thenAccept(data -> {
            ItemStack[] inventory = data.getMainInventory();
            ItemStack[] armour = data.getArmour();

            main.getServer().getScheduler().runTask(main, () -> {
                player.getInventory().setContents(inventory);
                if (main.getVersion().lessOrEqThan(BukkitVersion.v1_8_R3)) {
                    player.getInventory().setArmorContents(armour);
                }
            });

            playSound(player, SoundData.getInventoryRestored(), SoundData.isInventoryRestoreEnabled());
            sendRestoreMessages(staff, player, 
                MessageData.getMainInventoryRestoredPlayer(staff.getName()),
                MessageData.getMainInventoryRestored(offlinePlayer.getName()));
        });
    }

    private void teleportToLocation(InventoryClickEvent e, Player staff, CustomDataItemEditor nbt) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore.teleport")) return;

        String[] location = nbt.getString("location").split(",");
        World world = Bukkit.getWorld(location[0]);

        if (world == null) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getDeathLocationInvalidWorldError(location[0]));
            return;
        }

        Location loc = new Location(
            world,
            Math.floor(Double.parseDouble(location[1])),
            Math.floor(Double.parseDouble(location[2])),
            Math.floor(Double.parseDouble(location[3]))
        ).add(0.5, 0.5, 0.5);

        main.getServer().getScheduler().runTaskLater(main, () -> {
            e.getWhoClicked().closeInventory();
            PaperLib.teleportAsync(staff, loc).thenAccept(result -> {
                playSound(staff, SoundData.getTeleport(), SoundData.isTeleportEnabled());
                staff.sendMessage(PLUGIN_PREFIX + MessageData.getDeathLocationTeleport(loc));
            });
        }, 1L);
    }

    private void openEnderChest(Player staff, OfflinePlayer offlinePlayer, LogType logType, long timestamp) {
        loadPlayerDataAsync(offlinePlayer, logType, timestamp).thenAccept(data -> {
            EnderChestBackupMenu menu = new EnderChestBackupMenu(staff, data, 1);
            main.getServer().getScheduler().runTask(main, () -> 
                staff.openInventory(menu.getInventory())
            );
            menu.showEnderChestItems();
        });
    }

    private void restoreHealth(Player staff, OfflinePlayer offlinePlayer, CustomDataItemEditor nbt) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore")) return;
        
        if (!offlinePlayer.isOnline()) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getHealthNotOnline(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        double health = nbt.getDouble("health");
        
        main.getServer().getScheduler().runTask(main, () -> {
            player.setHealth(health);
            playSound(player, SoundData.getFoodRestored(), SoundData.isFoodRestoredEnabled());
        });
        
        sendRestoreMessages(staff, player,
            MessageData.getHealthRestoredPlayer(staff.getName()),
            MessageData.getHealthRestored(player.getName()));
    }

    private void restoreHunger(Player staff, OfflinePlayer offlinePlayer, CustomDataItemEditor nbt) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore")) return;
        
        if (!offlinePlayer.isOnline()) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getHungerNotOnline(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        int hunger = nbt.getInt("hunger");
        float saturation = nbt.getFloat("saturation");
        
        main.getServer().getScheduler().runTask(main, () -> {
            player.setFoodLevel(hunger);
            player.setSaturation(saturation);
            playSound(player, SoundData.getHungerRestored(), SoundData.isHungerRestoredEnabled());
        });
        
        sendRestoreMessages(staff, player,
            MessageData.getHungerRestoredPlayer(staff.getName()),
            MessageData.getHungerRestored(player.getName()));
    }

    private void restoreExperience(Player staff, OfflinePlayer offlinePlayer, CustomDataItemEditor nbt) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore")) return;
        
        if (!offlinePlayer.isOnline()) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getExperienceNotOnlinePlayer(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        float xp = nbt.getFloat("xp");
        
        main.getServer().getScheduler().runTask(main, () -> {
            RestoreInventory.setTotalExperience(player, xp);
            playSound(player, SoundData.getExperienceSound(), SoundData.isExperienceRestoredEnabled());
            
            int level = (int) RestoreInventory.getLevel(xp);
            sendRestoreMessages(staff, player,
                MessageData.getExperienceRestoredPlayer(staff.getName(), level),
                MessageData.getExperienceRestored(player.getName(), level));
        });
    }

    private void enderChestBackupMenu(InventoryClickEvent e, Player staff) {
        if (!e.getView().getTitle().equals(InventoryName.ENDER_CHEST_BACKUP.getName())) return;

        ItemStack icon = e.getCurrentItem();
        int slot = e.getRawSlot();
        int size = e.getInventory().getSize();
        boolean isBottomRow = slot >= (size - 9) && slot < size;

        if (isBottomRow && icon != null) {
            handleEnderChestActions(e, staff, icon);
        } else {
            handleEnderChestInteractions(e, staff);
        }
    }

    private void handleEnderChestActions(InventoryClickEvent e, Player staff, ItemStack icon) {
        CustomDataItemEditor nbt = CustomDataItemEditor.editItem(icon);
        if (!nbt.hasUUID()) return;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(nbt.getString("uuid")));
        LogType logType = LogType.valueOf(nbt.getString("logType"));
        long timestamp = nbt.getLong("timestamp");

        if (icon.getType().equals(Buttons.getPageSelectorIcon())) {
            int page = nbt.getInt("page");
            
            if (page == 0) {
                loadAndOpenBackupMenu(staff, offlinePlayer, logType, timestamp);
            } else {
                openEnderChestPage(staff, offlinePlayer, logType, timestamp, page);
            }
        } else if (icon.getType().equals(Buttons.getRestoreAllInventoryIcon())) {
            restoreEnderChest(staff, offlinePlayer, logType, timestamp);
        }
    }

    private void loadAndOpenBackupMenu(Player staff, OfflinePlayer offlinePlayer, LogType logType, long timestamp) {
        loadPlayerDataAsync(offlinePlayer, logType, timestamp).thenAccept(data -> {
            String location = data.getWorld() + "," + data.getX() + "," + data.getY() + "," + data.getZ();
            MainInventoryBackupMenu menu = new MainInventoryBackupMenu(staff, data, location);
            
            main.getServer().getScheduler().runTask(main, () -> 
                staff.openInventory(menu.getInventory())
            );
            menu.showBackupItems();
        });
    }

    private void openEnderChestPage(Player staff, OfflinePlayer offlinePlayer, LogType logType, long timestamp, int page) {
        loadPlayerDataAsync(offlinePlayer, logType, timestamp).thenAccept(data -> {
            EnderChestBackupMenu menu = new EnderChestBackupMenu(staff, data, page);
            main.getServer().getScheduler().runTask(main, () -> 
                staff.openInventory(menu.getInventory())
            );
            menu.showEnderChestItems();
        });
    }

    private void restoreEnderChest(Player staff, OfflinePlayer offlinePlayer, LogType logType, long timestamp) {
        if (!checkPermission(staff, "inventoryrollbackplus.restore")) return;
        
        if (!offlinePlayer.isOnline()) {
            staff.sendMessage(PLUGIN_PREFIX + MessageData.getEnderChestNotOnline(offlinePlayer.getName()));
            return;
        }

        Player player = (Player) offlinePlayer;
        
        loadPlayerDataAsync(offlinePlayer, logType, timestamp).thenAccept(data -> {
            ItemStack[] enderChest = data.getEnderChest() != null ? 
                data.getEnderChest() : new ItemStack[0];
            
            main.getServer().getScheduler().runTask(main, () -> 
                player.getEnderChest().setContents(enderChest)
            );
            
            playSound(player, SoundData.getInventoryRestored(), SoundData.isInventoryRestoreEnabled());
            sendRestoreMessages(staff, player,
                MessageData.getEnderChestRestoredPlayer(staff.getName()),
                MessageData.getEnderChestRestored(offlinePlayer.getName()));
        });
    }

    private void handleEnderChestInteractions(InventoryClickEvent e, Player staff) {
        int slotIndex = e.getRawSlot();
        int topInvSize = e.getView().getTopInventory().getSize();
        boolean clickInPlayerInventory = slotIndex >= topInvSize;
        boolean clickInEnderChest = slotIndex < topInvSize - 9;

        if (clickInPlayerInventory && !e.isShiftClick()) {
            e.setCancelled(false);
        } else if (clickInEnderChest) {
            if (checkPermission(staff, "inventoryrollbackplus.restore")) {
                e.setCancelled(false);
            }
        }
    }

    private void handleInventoryInteractions(InventoryClickEvent e, Player staff) {
        int slotIndex = e.getRawSlot();
        int topInvSize = e.getView().getTopInventory().getSize();
        boolean clickInPlayerInventory = slotIndex >= topInvSize;
        boolean clickInMainArea = slotIndex < topInvSize - 18;
        boolean clickInArmorArea = slotIndex > topInvSize - 15 && slotIndex < topInvSize - 9;

        if (clickInPlayerInventory && !e.isShiftClick()) {
            e.setCancelled(false);
        } else if (clickInMainArea || clickInArmorArea) {
            if (checkPermission(staff, "inventoryrollbackplus.restore")) {
                e.setCancelled(false);
            }
        }
    }

    private CompletableFuture<PlayerData> loadPlayerDataAsync(OfflinePlayer player, LogType logType, long timestamp) {
        return CompletableFuture.supplyAsync(() -> {
            PlayerData data = new PlayerData(player, logType, timestamp);
            if (ConfigData.getSaveType() == ConfigData.SaveType.MYSQL) {
                try {
                    data.getAllBackupData().get();
                } catch (InterruptedException | ExecutionException ex) {
                    Thread.currentThread().interrupt();
                }
            }
            return data;
        }, main);
    }

    private boolean isValidClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        return slot >= 0 && slot < e.getInventory().getSize();
    }

    private boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(PLUGIN_PREFIX + MessageData.getNoPermission());
            return false;
        }
        return true;
    }

    private void playSound(Player player, Sound sound, boolean condition) {
        if (condition) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }

    private void sendRestoreMessages(Player staff, Player target, String playerMsg, String staffMsg) {
        target.sendMessage(PLUGIN_PREFIX + playerMsg);
        if (!staff.getUniqueId().equals(target.getUniqueId())) {
            staff.sendMessage(PLUGIN_PREFIX + staffMsg);
        }
    }
}
