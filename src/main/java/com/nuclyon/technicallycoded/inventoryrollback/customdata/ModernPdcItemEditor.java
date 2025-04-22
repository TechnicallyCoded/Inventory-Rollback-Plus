package com.nuclyon.technicallycoded.inventoryrollback.customdata;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ModernPdcItemEditor implements CustomDataItemEditor {

    private final ItemStack item;

    public ModernPdcItemEditor(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean hasUUID() {
        String uuid = getString("uuid");
        return (uuid != null && !uuid.isEmpty());
    }

    @Override
    public ItemStack setString(String key, String data) {
        setData(key, PersistentDataType.STRING, data);
        return item;
    }

    @Override
    public ItemStack setInt(String key, Integer data) {
        setData(key, PersistentDataType.INTEGER, data);
        return item;
    }

    @Override
    public ItemStack setLong(String key, Long data) {
        setData(key, PersistentDataType.LONG, data);
        return item;
    }

    @Override
    public ItemStack setDouble(String key, Double data) {
        setData(key, PersistentDataType.DOUBLE, data);
        return item;
    }

    @Override
    public ItemStack setFloat(String key, Float data) {
        setData(key, PersistentDataType.FLOAT, data);
        return item;
    }

    @Override
    public String getString(String key) {
        return getData(key, PersistentDataType.STRING);
    }

    @Override
    public int getInt(String key) {
        return getData(key, PersistentDataType.INTEGER);
    }

    @Override
    public Long getLong(String key) {
        return getData(key, PersistentDataType.LONG);
    }

    @Override
    public double getDouble(String key) {
        return getData(key, PersistentDataType.DOUBLE);
    }

    @Override
    public Float getFloat(String key) {
        return getData(key, PersistentDataType.FLOAT);
    }

    @Override
    public ItemStack setItemData() {
        return item;
    }

    private <T> T getData(String key, PersistentDataType<T, T> type) {
        if (item == null) return null;

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();

        NamespacedKey namedKey = getNamespacedKey(key);

        if (!pdc.has(namedKey, type)) return null;
        return pdc.get(namedKey, type);
    }

    private <T> void setData(String key, PersistentDataType<T, T> type, T data) {
        if (item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(getNamespacedKey(key), type, data);

        item.setItemMeta(itemMeta);
    }

    private static @NotNull NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(InventoryRollbackPlus.getInstance(), key);
    }

}
