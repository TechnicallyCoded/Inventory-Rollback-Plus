package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.MessageData;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Version1Serialization {

    public static String toBase64(ItemStack[] contents) {
        boolean convert = false;

        for (ItemStack item : contents) {
            if (item != null) {
                convert = true;
                break;
            }
        }

        if (convert) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                dataOutput.writeInt(contents.length);

                for (ItemStack stack : contents) {
                    dataOutput.writeObject(stack);
                }
                dataOutput.close();
                byte[] byteArr = outputStream.toByteArray();
                return Base64Coder.encodeLines(byteArr);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to save item stacks.", e);
            }
        }

        return null;
    }

    public static ItemStack[] stacksFromBase64(String packageVersion, String data) {
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

                try {
                    dataInput.close();
                } catch (IOException e1) {
                    InventoryRollbackPlus.getPluginLogger().severe(ChatColor.stripColor(MessageData.getPluginPrefix()) + "There was an error while terminating read of backup data after an error already occurred.");
                }
                return null;
            }
        }

        try {
            dataInput.close();
        } catch (IOException e1) {
            InventoryRollbackPlus.getPluginLogger().severe(ChatColor.stripColor(MessageData.getPluginPrefix()) + "There was an error while terminating read of backup data after normal read.");
        }

        return stacks;
    }

}
