package com.nuclyon.technicallycoded.inventoryrollback.commands.inventoryrollback;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.commands.IRPCommand;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PurgeSubCmd extends IRPCommand {

    private static final AtomicBoolean suggestConfirm = new AtomicBoolean(false);
    public static final Pattern DURATION_PATTERN = Pattern.compile("^(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?$");

    public PurgeSubCmd(InventoryRollbackPlus mainIn) {
        super(mainIn);
    }

    @Override
    public void onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("inventoryrollbackplus.purge")) {
            sender.sendMessage(MessageData.getPluginPrefix() + MessageData.getNoPermission());
            return;
        }

        if (args.length < 2) {
            sendUsage(sender, label);
            return;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "player":
                handlePurgePlayer(sender, label, args);
                break;
            case "type":
                handlePurgeType(sender, label, args);
                break;
            case "older":
                handlePurgeOlder(sender, label, args);
                break;
            case "all":
                handlePurgeAll(sender, label, args);
                break;
            default:
                sendUsage(sender, label);
                break;
        }
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.GRAY + "Purge Commands:");
        sender.sendMessage(ChatColor.WHITE + "  /" + label + " purge player <name|uuid> confirm" + ChatColor.GRAY + " - Delete all backups for a player");
        sender.sendMessage(ChatColor.WHITE + "  /" + label + " purge type <death|join|quit|world-change|force> confirm" + ChatColor.GRAY + " - Delete all backups of a type");
        sender.sendMessage(ChatColor.WHITE + "  /" + label + " purge older <duration> confirm" + ChatColor.GRAY + " - Delete backups older than duration (e.g., 30d, 12h, 7d12h)");
        sender.sendMessage(ChatColor.WHITE + "  /" + label + " purge all confirm" + ChatColor.GRAY + " - Delete ALL backups");
    }

    @SuppressWarnings("deprecation")
    private void handlePurgePlayer(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Usage: /" + label + " purge player <name|uuid> confirm");
            return;
        }

        String playerIdentifier = args[2];

        // Check for confirmation
        if (args.length < 4 || !args[3].equalsIgnoreCase("confirm")) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.YELLOW + "This will delete ALL backups for player: " + ChatColor.WHITE + playerIdentifier);
            sender.sendMessage(ChatColor.RED + "Add 'confirm' to execute: /" + label + " purge player " + playerIdentifier + " confirm");
            suggestConfirm.set(true);
            scheduleConfirmReset();
            return;
        }

        // Resolve player
        OfflinePlayer offlinePlayer;
        String uuidStr = playerIdentifier;

        if (uuidStr.length() == 36 || uuidStr.length() == 32) {
            // Handle UUID input
            if (uuidStr.length() == 32) {
                uuidStr = uuidStr.substring(0, 8) + "-" +
                          uuidStr.substring(8, 12) + "-" +
                          uuidStr.substring(12, 16) + "-" +
                          uuidStr.substring(16, 20) + "-" +
                          uuidStr.substring(20);
            }
            try {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Invalid UUID format.");
                return;
            }
        } else {
            offlinePlayer = Bukkit.getOfflinePlayer(playerIdentifier);
        }

        final UUID playerUUID = offlinePlayer.getUniqueId();
        final String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : playerIdentifier;

        // Execute purge asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            int deletedCount = PlayerData.purgeAllBackupsForPlayer(playerUUID);
            
            String message = ChatColor.GREEN + "Purged " + deletedCount + " backup(s) for player " + playerName;
            Bukkit.getScheduler().runTask(main, () -> sender.sendMessage(MessageData.getPluginPrefix() + message));
            
            // Log to console
            main.getLogger().info("Purged " + deletedCount + " backups for player " + playerName + " (UUID: " + playerUUID + ") - Requested by " + sender.getName());
        });

        suggestConfirm.set(false);
    }

    private void handlePurgeType(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Usage: /" + label + " purge type <death|join|quit|world-change|force> confirm");
            return;
        }

        String typeStr = args[2].toUpperCase().replace("-", "_");
        LogType logType;

        try {
            logType = LogType.valueOf(typeStr);
            if (logType == LogType.UNKNOWN) {
                throw new IllegalArgumentException("UNKNOWN is not valid");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Invalid log type. Use: death, join, quit, world-change, force");
            return;
        }

        // Check for confirmation
        if (args.length < 4 || !args[3].equalsIgnoreCase("confirm")) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.YELLOW + "This will delete ALL " + logType.name() + " backups for ALL players!");
            sender.sendMessage(ChatColor.RED + "Add 'confirm' to execute: /" + label + " purge type " + args[2] + " confirm");
            suggestConfirm.set(true);
            scheduleConfirmReset();
            return;
        }

        final LogType finalLogType = logType;

        // Execute purge asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            int deletedCount = PlayerData.purgeAllBackupsForLogType(finalLogType);
            
            String message = ChatColor.GREEN + "Purged " + deletedCount + " " + finalLogType.name() + " backup(s)";
            Bukkit.getScheduler().runTask(main, () -> sender.sendMessage(MessageData.getPluginPrefix() + message));
            
            // Log to console
            main.getLogger().info("Purged " + deletedCount + " " + finalLogType.name() + " backups - Requested by " + sender.getName());
        });

        suggestConfirm.set(false);
    }

    private void handlePurgeOlder(CommandSender sender, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Usage: /" + label + " purge older <duration> confirm");
            sender.sendMessage(ChatColor.GRAY + "Duration format: 30d (30 days), 12h (12 hours), 7d12h (7 days 12 hours)");
            return;
        }

        String durationStr = args[2].toLowerCase();
        long durationMs = parseDuration(durationStr);

        if (durationMs <= 0) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "Invalid duration format. Use: 30d, 12h, 7d12h, 1d6h30m");
            return;
        }

        // Check for confirmation
        if (args.length < 4 || !args[3].equalsIgnoreCase("confirm")) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.YELLOW + "This will delete ALL backups older than " + formatDuration(durationMs) + "!");
            sender.sendMessage(ChatColor.RED + "Add 'confirm' to execute: /" + label + " purge older " + durationStr + " confirm");
            suggestConfirm.set(true);
            scheduleConfirmReset();
            return;
        }

        long olderThanTimestamp = System.currentTimeMillis() - durationMs;
        final String formattedDuration = formatDuration(durationMs);

        // Execute purge asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            int deletedCount = PlayerData.purgeAllBackupsOlderThan(olderThanTimestamp);
            
            String message = ChatColor.GREEN + "Purged " + deletedCount + " backup(s) older than " + formattedDuration;
            Bukkit.getScheduler().runTask(main, () -> sender.sendMessage(MessageData.getPluginPrefix() + message));
            
            // Log to console
            main.getLogger().info("Purged " + deletedCount + " backups older than " + formattedDuration + " - Requested by " + sender.getName());
        });

        suggestConfirm.set(false);
    }

    private void handlePurgeAll(CommandSender sender, String label, String[] args) {
        // Check for confirmation
        if (args.length < 3 || !args[2].equalsIgnoreCase("confirm")) {
            sender.sendMessage(MessageData.getPluginPrefix() + ChatColor.RED + "" + ChatColor.BOLD + "WARNING: This will delete ALL backups for ALL players!");
            sender.sendMessage(ChatColor.RED + "Add 'confirm' to execute: /" + label + " purge all confirm");
            suggestConfirm.set(true);
            scheduleConfirmReset();
            return;
        }

        // Execute purge asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            int deletedCount = PlayerData.purgeAllBackups();
            
            String message = ChatColor.GREEN + "Purged ALL " + deletedCount + " backup(s)";
            Bukkit.getScheduler().runTask(main, () -> sender.sendMessage(MessageData.getPluginPrefix() + message));
            
            // Log to console
            main.getLogger().info("Purged ALL " + deletedCount + " backups - Requested by " + sender.getName());
        });

        suggestConfirm.set(false);
    }

    /**
     * Parse duration string to milliseconds.
     * Supports formats like: 30d, 12h, 30m, 7d12h, 1d6h30m
     * @param duration The duration string to parse
     * @return Duration in milliseconds, or -1 if invalid
     */
    public static long parseDuration(String duration) {
        Matcher matcher = DURATION_PATTERN.matcher(duration);
        if (!matcher.matches()) {
            return -1;
        }

        long totalMs = 0;

        String days = matcher.group(1);
        String hours = matcher.group(2);
        String minutes = matcher.group(3);

        if (days != null) {
            totalMs += Long.parseLong(days) * 24 * 60 * 60 * 1000;
        }
        if (hours != null) {
            totalMs += Long.parseLong(hours) * 60 * 60 * 1000;
        }
        if (minutes != null) {
            totalMs += Long.parseLong(minutes) * 60 * 1000;
        }

        return totalMs > 0 ? totalMs : -1;
    }

    /**
     * Format duration in milliseconds to human-readable string.
     * @param durationMs Duration in milliseconds
     * @return Human-readable duration string
     */
    public static String formatDuration(long durationMs) {
        long days = durationMs / (24 * 60 * 60 * 1000);
        long hours = (durationMs % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (durationMs % (60 * 60 * 1000)) / (60 * 1000);

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" day(s) ");
        if (hours > 0) sb.append(hours).append(" hour(s) ");
        if (minutes > 0) sb.append(minutes).append(" minute(s)");

        return sb.toString().trim();
    }

    private void scheduleConfirmReset() {
        main.getServer().getScheduler().runTaskLaterAsynchronously(main, () -> {
            suggestConfirm.set(false);
        }, 10 * 20); // 10 seconds
    }

    public static boolean shouldShowConfirmOption() {
        return suggestConfirm.get();
    }
}
