package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;

public class Version2Serialization {

    public static final int ID = 2;

    public static DeserializationResult deserialize(String data) {
        try {
            byte[] b64decoded = Base64.getDecoder().decode(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(b64decoded);
            return deserialize(bais);
        } catch (Exception e) {
            e.printStackTrace();
            return new DeserializationResult(null, "Failed to deserialize item stack: " + e.getMessage());
        }
    }

    public static DeserializationResult deserialize(InputStream bais) throws IOException {
        ItemStack[] items = new ItemStack[readInt(bais)];

        for (int i = 0; i < items.length; i++) {
            // Read the length of the serialized item
            int length = readInt(bais);

            if (length == 0) {
                items[i] = null;
                continue;
            }

            // Read the serialized item
            byte[] serializedItem = new byte[length];
            for (int j = 0; j < length; j++) {
                serializedItem[j] = (byte) bais.read();
            }

            try {
                items[i] = deserializeItem(serializedItem);
            } catch (Exception ex) {
                ex.printStackTrace();
                return new DeserializationResult(null, "Failed to deserialize item stack: " + ex.getMessage());
            }
        }

        return new DeserializationResult(items, null);
    }

    public static String serialize(ItemStack[] items) {
        try {
            byte[] serializedBytes = serializeBytes(items);
            return Base64.getEncoder().encodeToString(serializedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] serializeBytes(ItemStack[] items) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Write the number of items
        try {
            writeInt(baos, items.length);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        // Write each item
        for (ItemStack item : items) {
            // If the item is null, write a 0 int
            if (item == null) {
                writeInt(baos, 0);
                continue;
            }

            // Write the length of the serialized item followed by the serialized item
            try {
                byte[] serializedItem = serializeItem(item);
                writeInt(baos, serializedItem.length);
                baos.write(serializedItem);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return baos.toByteArray();
    }

    private static byte[] serializeItem(ItemStack item) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
        boos.writeObject(item);
        boos.close();
        return baos.toByteArray();
    }

    private static ItemStack deserializeItem(byte[] serialized) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
        BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);
        return (ItemStack) bois.readObject();
    }

    private static int readInt(InputStream is) throws IOException {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result |= (is.read() & 0xFF) << (i * 8);
        }
        return result;
    }

    private static void writeInt(OutputStream os, int value) throws IOException {
        for (int i = 0; i < 4; i++) {
            os.write((value >> (i * 8)) & 0xFF);
        }
    }

}
