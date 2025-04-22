package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class handles the serialization and deserialization of ItemStacks
 * using a GZIP compressed format wrapped around Version 2.
 */
public class Version3Serialization {

    public static final int ID = 3;

    public static DeserializationResult deserialize(String data) {
        try {
            byte[] b64decoded = Base64.getDecoder().decode(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(b64decoded);
            GZIPInputStream gis = new GZIPInputStream(bais);

            return Version2Serialization.deserialize(gis);
        } catch (Exception e) {
            e.printStackTrace();
            return new DeserializationResult(null, "Failed to deserialize item stack: " + e.getMessage());
        }
    }

    public static String serialize(ItemStack[] items) {
        try {
            byte[] serializedBytes = Version2Serialization.serializeBytes(items);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);

            assert serializedBytes != null;
            gos.write(serializedBytes);

            gos.close();

            byte[] compressedBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(compressedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
