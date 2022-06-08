package me.danjono.inventoryrollback.inventory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.MessageData;

public class RestoreInventory {
    
    private RestoreInventory() {
        throw new IllegalStateException("Restore inventory class");
    }

    public static ItemStack[] getInventoryItems(String packageVersion, String base64) {
        ItemStack[] inv = null;

        try {
            inv = stacksFromBase64(packageVersion, base64);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return inv;
    }

    private static ItemStack[] stacksFromBase64(String packageVersion, String data) {
        if (data == null) 
            return new ItemStack[]{};

        ByteArrayInputStream inputStream = null; 
                
        try {
            inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        } catch (IllegalArgumentException e) {
            return new ItemStack[]{};
        }
        
        BukkitObjectInputStream dataInput = null;
        ItemStack[] stacks = null;

        try {
            dataInput = new BukkitObjectInputStream(inputStream);
            stacks = new ItemStack[dataInput.readInt()];
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        if (stacks == null)
            return new ItemStack[]{};

        for (int i = 0; i < stacks.length; i++) {
            try {
                stacks[i] = (ItemStack) dataInput.readObject();
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                //Backup generated before InventoryRollback v1.3
                if (packageVersion == null) {
                    InventoryRollbackPlus.getPluginLogger().severe(ChatColor.stripColor(MessageData.getPluginPrefix()) + "There was an error deserializing the material data. This is likely caused by a now incompatible material ID if the backup was originally generated on a different Minecraft server version.");
                } 
                //Backup was not generated on the same server version
                else if (!packageVersion.equalsIgnoreCase(InventoryRollbackPlus.getPackageVersion())) {
                    InventoryRollbackPlus.getPluginLogger().severe(ChatColor.stripColor(MessageData.getPluginPrefix()) + "There was an error deserializing the material data. The backup was generated on a " + packageVersion + " version server whereas you are now running a " + InventoryRollback.getPackageVersion() + " version server. It is likely a material ID inside the backup is no longer valid on this Minecraft server version and cannot be convereted.");
                } 
                //Unknown error
                else if (packageVersion.equalsIgnoreCase(InventoryRollbackPlus.getPackageVersion())) {
                    InventoryRollbackPlus.getPluginLogger().severe(ChatColor.stripColor(MessageData.getPluginPrefix()) + "There was an error deserializing the material data. The data file is likely corrupted since this was saved on the same version the server is currently running on so it should have worked.");
                }

                try { dataInput.close(); } catch (IOException e1) {}
                return null;
            }
        }

        try { dataInput.close(); } catch (IOException e1) {}

        return stacks;
    }

    //Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
    //https://gist.github.com/RichardB122/8958201b54d90afbc6f0
    public static void setTotalExperience(Player player, float xpFloat) {
        int xp = (int) xpFloat;

        //Levels 0 through 15
        if (xp >= 0 && xp <= 351) {
            //Calculate Everything
            int a = 1; 
            int b = 6; 
            int c = -xp;
            int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
            int remainder = xp - xpForLevel;
            int experienceNeeded = (2 * level) + 7;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
            //Levels 16 through 30
        } else if (xp >= 352 && xp < 1507) {
            //Calculate Everything
            double a = 2.5;
            double b = -40.5;
            int c = -xp + 360;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (5 * level) - 38;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
            //Level 31 and greater
        } else {
            //Calculate Everything
            double a = 4.5;
            double b = -162.5;
            int c = -xp + 2220;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (9 * level) - 158;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            //Set Everything
            player.setLevel(level);
            player.setExp(experience);
        }
    }

    public static float getLevel(float floatXP) {
        int xp = (int) floatXP;

        //Levels 0 through 15
        if (xp >= 0 && xp < 351) {
            //Calculate Everything
            int a = 1;
            int b = 6;
            int c = -xp;
            int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
            int remainder = xp - xpForLevel;
            int experienceNeeded = (2 * level) + 7;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            return level;
            //Levels 16 through 30
        } else if (xp >= 352 && xp < 1507) {
            //Calculate Everything
            double a = 2.5; double b = -40.5; int c = -xp + 360;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (2.5 * Math.pow(level, 2) - (40.5 * level) + 360);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (5 * level) - 38;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            //Set Everything
            return level;
            //Level 31 and greater
        } else {
            //Calculate Everything
            double a = 4.5;
            double b = -162.5;
            int c = -xp + 2220;
            double dLevel = (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int level = (int) Math.floor(dLevel);
            int xpForLevel = (int) (4.5 * Math.pow(level, 2) - (162.5 * level) + 2220);
            int remainder = xp - xpForLevel;
            int experienceNeeded = (9 * level) - 158;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            //Set Everything
            return (float) level;
        }
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = BigDecimal.valueOf((double) d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd.floatValue();
    }

}
