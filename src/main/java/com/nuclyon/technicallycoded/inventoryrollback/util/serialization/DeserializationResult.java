package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DeserializationResult {

    public static DeserializationResult failure(String errorMessage) {
        return new DeserializationResult(null, errorMessage);
    }

    private final ItemStack[] items;
    private final String errorMessage;

    public DeserializationResult(ItemStack[] items, String errorMessage) {
        this.items = items;
        this.errorMessage = errorMessage;
    }

    @Nullable
    public ItemStack[] getItems() {
        return items;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

}
