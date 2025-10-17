package me.danjono.inventoryrollback.discord;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.ConfigData;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DiscordWebhook {

    public enum EventType {
        BACKUP_CREATED,
        INVENTORY_RESTORED,
        ENDER_CHEST_RESTORED,
        HEALTH_RESTORED,
        HUNGER_RESTORED,
        EXPERIENCE_RESTORED,
        PLAYER_DEATH,
        FORCE_BACKUP
    }

    /**
     * Sends a Discord webhook message for a backup creation event
     */
    public static void sendBackupCreated(String playerName, String backupType, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordBackupCreated()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleBackupCreated();
            description = MessageData.getDiscordDescBackupCreated()
                    .replace("%PLAYER%", playerName)
                    .replace("%TYPE%", backupType)
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgBackupCreated()
                    .replace("%PLAYER%", playerName)
                    .replace("%TYPE%", backupType)
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.BACKUP_CREATED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for an inventory restoration event
     */
    public static void sendInventoryRestored(String playerName, String adminName, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordInventoryRestored()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleInventoryRestored();
            description = MessageData.getDiscordDescInventoryRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgInventoryRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.INVENTORY_RESTORED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for an ender chest restoration event
     */
    public static void sendEnderChestRestored(String playerName, String adminName, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordEnderChestRestored()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleEnderChestRestored();
            description = MessageData.getDiscordDescEnderChestRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgEnderChestRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.ENDER_CHEST_RESTORED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for a health restoration event
     */
    public static void sendHealthRestored(String playerName, String adminName, double health, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordHealthRestored()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleHealthRestored();
            description = MessageData.getDiscordDescHealthRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%HEALTH%", String.format("%.1f", health))
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgHealthRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%HEALTH%", String.format("%.1f", health))
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.HEALTH_RESTORED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for a hunger restoration event
     */
    public static void sendHungerRestored(String playerName, String adminName, int hunger, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordHungerRestored()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleHungerRestored();
            description = MessageData.getDiscordDescHungerRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%HUNGER%", String.valueOf(hunger))
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgHungerRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%HUNGER%", String.valueOf(hunger))
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.HUNGER_RESTORED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for an experience restoration event
     */
    public static void sendExperienceRestored(String playerName, String adminName, int level, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordExperienceRestored()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleExperienceRestored();
            description = MessageData.getDiscordDescExperienceRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%LEVEL%", String.valueOf(level))
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgExperienceRestored()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%LEVEL%", String.valueOf(level))
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.EXPERIENCE_RESTORED, message, title, description);
    }

    /**
     * Sends a Discord webhook message for a player death event
     */
    public static void sendPlayerDeath(String playerName, Location location, String deathCause, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordPlayerDeath()) {
            return;
        }

        String worldName = location.getWorld() != null ? location.getWorld().getName() : "unknown";
        String x = String.valueOf((int) location.getX());
        String y = String.valueOf((int) location.getY());
        String z = String.valueOf((int) location.getZ());

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitlePlayerDeath();
            description = MessageData.getDiscordDescPlayerDeath()
                    .replace("%PLAYER%", playerName)
                    .replace("%WORLD%", worldName)
                    .replace("%X%", x)
                    .replace("%Y%", y)
                    .replace("%Z%", z)
                    .replace("%CAUSE%", deathCause)
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgPlayerDeath()
                    .replace("%PLAYER%", playerName)
                    .replace("%WORLD%", worldName)
                    .replace("%X%", x)
                    .replace("%Y%", y)
                    .replace("%Z%", z)
                    .replace("%CAUSE%", deathCause)
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.PLAYER_DEATH, message, title, description);
    }

    /**
     * Sends a Discord webhook message for a force backup event
     */
    public static void sendForceBackup(String playerName, String adminName, String timestamp) {
        if (!ConfigData.isDiscordEnabled() || !ConfigData.isDiscordForceBackup()) {
            return;
        }

        String message, title, description;
        if (ConfigData.isDiscordUseEmbeds()) {
            title = MessageData.getDiscordTitleForceBackup();
            description = MessageData.getDiscordDescForceBackup()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            message = null;
        } else {
            message = MessageData.getDiscordMsgForceBackup()
                    .replace("%PLAYER%", playerName)
                    .replace("%ADMIN%", adminName)
                    .replace("%TIME%", timestamp);
            title = null;
            description = null;
        }

        sendWebhookMessage(EventType.FORCE_BACKUP, message, title, description);
    }

    /**
     * Sends the actual webhook message to Discord
     */
    private static void sendWebhookMessage(EventType eventType, String message, String title, String description) {
        String webhookUrl = ConfigData.getDiscordWebhookUrl();

        if (webhookUrl == null || webhookUrl.isEmpty()) {
            if (ConfigData.isDebugEnabled()) {
                InventoryRollback.getInstance().getLogger().warning("Discord webhook URL is not configured");
            }
            return;
        }

        // Validate webhook URL
        if (!isValidWebhookUrl(webhookUrl)) {
            InventoryRollback.getInstance().getLogger().warning(MessageData.getDiscordErrorInvalidWebhook());
            return;
        }

        // Send webhook asynchronously to avoid blocking the main thread
        CompletableFuture.runAsync(() -> {
            try {
                String jsonPayload = buildJsonPayload(eventType, message, title, description);
                sendHttpRequest(webhookUrl, jsonPayload);

                if (ConfigData.isDebugEnabled()) {
                    InventoryRollback.getInstance().getLogger().info("Discord webhook sent successfully for event: " + eventType);
                }
            } catch (Exception e) {
                InventoryRollback.getInstance().getLogger().log(Level.WARNING, MessageData.getDiscordErrorWebhookFailed() + ": " + e.getMessage(), e);
            }
        });
    }

    /**
     * Builds the JSON payload for the Discord webhook
     */
    private static String buildJsonPayload(EventType eventType, String message, String title, String description) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        // Add server name if enabled
        if (ConfigData.isDiscordIncludeServerName()) {
            json.append("\"username\":\"").append(escapeJson(ConfigData.getDiscordServerName())).append("\",");
        }

        if (ConfigData.isDiscordUseEmbeds() && title != null && description != null) {
            // Use embeds - Discord embeds support newlines differently
            json.append("\"embeds\":[{");
            json.append("\"title\":\"").append(escapeJson(title)).append("\",");
            json.append("\"description\":\"").append(escapeJsonForEmbed(description)).append("\",");
            json.append("\"color\":").append(getColorForEventType(eventType)).append(",");
            json.append("\"timestamp\":\"").append(java.time.Instant.now().toString()).append("\"");
            json.append("}]");
        } else {
            // Use simple content message - Discord content supports newlines natively
            json.append("\"content\":\"").append(escapeJsonForContent(message != null ? message : "Unknown event")).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Gets the color code for the event type
     */
    private static int getColorForEventType(EventType eventType) {
        String colorHex;
        switch (eventType) {
            case BACKUP_CREATED:
            case FORCE_BACKUP:
                colorHex = ConfigData.getDiscordColorBackup();
                break;
            case INVENTORY_RESTORED:
            case ENDER_CHEST_RESTORED:
            case HEALTH_RESTORED:
            case HUNGER_RESTORED:
            case EXPERIENCE_RESTORED:
                colorHex = ConfigData.getDiscordColorRestore();
                break;
            case PLAYER_DEATH:
                colorHex = ConfigData.getDiscordColorDeath();
                break;
            default:
                colorHex = ConfigData.getDiscordColorWarning();
                break;
        }

        // Convert hex color to decimal
        try {
            if (colorHex.startsWith("#")) {
                colorHex = colorHex.substring(1);
            }
            return Integer.parseInt(colorHex, 16);
        } catch (NumberFormatException e) {
            return 0x0099ff; // Default blue color
        }
    }

    /**
     * Sends the HTTP request to Discord
     */
    private static void sendHttpRequest(String webhookUrl, String jsonPayload) throws IOException {
        // Log the JSON payload for debugging when debug is enabled
        if (ConfigData.isDebugEnabled()) {
            InventoryRollback.getInstance().getLogger().info("Sending Discord webhook payload: " + jsonPayload);
        }

        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("User-Agent", "InventoryRollbackPlus/1.7.6");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            // Try to read error response for debugging
            String errorResponse = "";
            try {
                if (connection.getErrorStream() != null) {
                    java.util.Scanner scanner = new java.util.Scanner(connection.getErrorStream()).useDelimiter("\\A");
                    errorResponse = scanner.hasNext() ? scanner.next() : "";
                }
            } catch (Exception e) {
                // Ignore error reading response
            }

            String errorMessage = "Discord webhook returned HTTP " + responseCode;
            if (!errorResponse.isEmpty() && ConfigData.isDebugEnabled()) {
                errorMessage += " - Response: " + errorResponse;
            }
            throw new IOException(errorMessage);
        }
    }

    /**
     * Validates if the webhook URL is a valid Discord webhook URL
     */
    private static boolean isValidWebhookUrl(String url) {
        try {
            new URL(url);
            return url.contains("discord.com/api/webhooks/") || url.contains("discordapp.com/api/webhooks/");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Escapes JSON special characters
     */
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\r", "")  // Remove carriage returns
                  .replace("\t", "    ");  // Replace tabs with spaces, keep newlines as-is
    }

    /**
     * Escapes JSON special characters for content messages
     * Content messages support newlines natively but need proper JSON escaping
     */
    private static String escapeJsonForContent(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")  // Properly escape newlines for JSON
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    /**
     * Escapes JSON special characters for embed descriptions
     * Embed descriptions support newlines but need proper JSON escaping
     */
    private static String escapeJsonForEmbed(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")  // Properly escape newlines for JSON
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
