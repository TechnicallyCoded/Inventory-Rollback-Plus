package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public class ItemStackSerialization {

    private static final String MODERN_SERIALIZATION_PREFIX = "IRP_VERSION:";

    public static String serialize(ItemStack[] items) {
        // Always use the newest serialization format
        String serialized = Version3Serialization.serialize(items);
        String data = MODERN_SERIALIZATION_PREFIX + Version3Serialization.ID + ":" + serialized;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static DeserializationResult deserializeData(String packageVersion, String data) {
        if (data == null) {
            return new DeserializationResult(null, "Data is null");
        }

        byte[] decodedBytes;
        String decodedString = null;

        try {
            decodedBytes = Base64.getDecoder().decode(data);
            decodedString = new String(decodedBytes);
        } catch (Exception ignored) {}

        // Default to version 1 if no prefix is found
        String version = "1";
        String unprefixedData = data; // version 1

        // Modern serialization format:
        // IRP_VERSION:2:data-here
        if (decodedString != null && decodedString.startsWith(MODERN_SERIALIZATION_PREFIX)) {
            int prefixLen = MODERN_SERIALIZATION_PREFIX.length();
            version = decodedString.substring(prefixLen, prefixLen + 1);
            unprefixedData = decodedString.substring(prefixLen + 2);
        }

        switch (version) {
            case "1":
                return new DeserializationResult(
                        Version1Serialization.stacksFromBase64(packageVersion, unprefixedData),
                        "");
            case "2":
                return Version2Serialization.deserialize(unprefixedData);
            case "3":
                return Version3Serialization.deserialize(unprefixedData);
            default:
                return new DeserializationResult(null, "Unsupported serialization version: " + version);
        }

    }

}
