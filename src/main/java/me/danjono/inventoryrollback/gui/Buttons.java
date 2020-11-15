package me.danjono.inventoryrollback.gui;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.InventoryRollback.VersionName;
import me.danjono.inventoryrollback.config.MessageData;
import me.danjono.inventoryrollback.data.LogType;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBT;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Buttons {

    private static final Material pageSelector = Material.getMaterial(InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS) ? "WHITE_BANNER" : "BANNER");
    private static final Material enderPearl = Material.ENDER_PEARL;
    private static final Material inventory = Material.CHEST;
    private static final Material enderChest = Material.ENDER_CHEST;
    private static final Material health = Material.getMaterial(InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS) ? "MELON_SLICE" : "MELON");
    private static final Material hunger = Material.ROTTEN_FLESH;
    private static final Material experience = Material.getMaterial(InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS) ? "EXPERIENCE_BOTTLE" : "EXP_BOTTLE");

    public static ItemStack getPageSelectorIcon() {
        return new ItemStack(pageSelector);
    }

    public static ItemStack getEnderPearlIcon() {
        return new ItemStack(enderPearl);
    }

    public static ItemStack getInventorytIcon() {
        return new ItemStack(inventory);
    }

    public static ItemStack getEnderChestIcon() {
        return new ItemStack(enderChest);
    }

    public static ItemStack getHealthIcon() {
        return new ItemStack(health);
    }

    public static ItemStack getHungerIcon() {
        return new ItemStack(hunger);
    }

    public static ItemStack getExperienceIcon() {
        return new ItemStack(experience);
    }

    public ItemStack nextButton(String displayName, UUID uuid, LogType logType, int page, List<String> lore) {
        ItemStack button = getPageSelectorIcon();
        BannerMeta meta = (BannerMeta) button.getItemMeta();

        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.BASE));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.BORDER));

        meta.setPatterns(patterns);

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        meta.setLore(lore);

        button.setItemMeta(meta);

        NBT nbt = new NBT(button);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setInt("page", page);
        button = nbt.setItemData();

        return button;
    }

    public ItemStack backButton(String displayName, UUID uuid, LogType logType, int page, List<String> lore) {
        ItemStack button = getPageSelectorIcon();
        BannerMeta meta = (BannerMeta) button.getItemMeta();

        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.BASE));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL_MIRROR));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.BORDER));

        meta.setPatterns(patterns);

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        if (lore != null) {
            meta.setLore(lore);
        }

        button.setItemMeta(meta);

        NBT nbt = new NBT(button);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setInt("page", page);
        button = nbt.setItemData();

        return button;
    }

    public ItemStack mainMenuBackButton(String displayName, UUID uuid) {
        ItemStack button = getPageSelectorIcon();
        BannerMeta meta = (BannerMeta) button.getItemMeta();

        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.BASE));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL_MIRROR));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.BORDER));

        meta.setPatterns(patterns);

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        button.setItemMeta(meta);

        NBT nbt = new NBT(button);

        nbt.setString("uuid", uuid.toString());
        button = nbt.setItemData();

        return button;
    }

    public ItemStack inventoryMenuBackButton(String displayName, UUID uuid, LogType logType) {
        ItemStack button = getPageSelectorIcon();
        BannerMeta meta = (BannerMeta) button.getItemMeta();

        List<Pattern> patterns = new ArrayList<>();
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.BASE));
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE));
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL_MIRROR));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.BORDER));

        meta.setPatterns(patterns);

        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        if (displayName != null) {
            meta.setDisplayName(displayName);
        }

        button.setItemMeta(meta);

        NBT nbt = new NBT(button);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        button = nbt.setItemData();

        return button;
    }

    public ItemStack createInventoryButton(ItemStack item, UUID uuid, LogType logType, String location, Long time, String displayName, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        //meta.setDisplayName(name);

        if (lore != null) {
            meta.setLore(lore);
        }

        meta.setDisplayName(displayName);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setLong("timestamp", time);
        nbt.setString("location", location);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack createLogTypeButton(ItemStack item, UUID uuid, String name, LogType logType, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (lore != null) {
            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        item = nbt.setItemData();

        return item;
    }

    public ItemStack playerHead(OfflinePlayer player, List<String> lore) {
        ItemStack skull;

        if (InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS)) {
            skull = new ItemStack(Material.getMaterial("PLAYER_HEAD"));
        } else {
            skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) SkullType.PLAYER.ordinal());
        }

        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        try {
            Method method;
            if (InventoryRollback.getVersion().equals(VersionName.v1_13_PLUS)) {
                method = skullMeta.getClass().getMethod("setOwningPlayer", OfflinePlayer.class);
                method.setAccessible(true);
                method.invoke(skullMeta, player);
            } else {
                method = skullMeta.getClass().getMethod("setOwner", String.class);
                method.setAccessible(true);
                method.invoke(skullMeta, player.getName());
            }
            method.setAccessible(false);
        } catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        skullMeta.setDisplayName(ChatColor.RESET + player.getName());

        if (lore != null) {
            skullMeta.setLore(lore);
        }

        skull.setItemMeta(skullMeta);

        return skull;
    }

    public ItemStack enderPearlButton(UUID uuid, LogType logType, Long timestamp, String location) {
        ItemStack item = new ItemStack(getEnderPearlIcon());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.deathLocationMessage);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setString("location", location);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack inventoryButton(UUID uuid, LogType logType, Long timestamp) {
        ItemStack item = new ItemStack(getInventorytIcon());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.restoreInventory);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setLong("timestamp", timestamp);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack enderChestButton(UUID uuid, LogType logType, Long timestamp) {
        ItemStack item = new ItemStack(getEnderChestIcon());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.restoreEnderChest);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setLong("timestamp", timestamp);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack healthButton(UUID uuid, LogType logType, Double health) {
        ItemStack item = new ItemStack(getHealthIcon());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.restoreFood);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setDouble("health", health);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack hungerButton(UUID uuid, LogType logType, int hunger, float saturation) {
        ItemStack item = new ItemStack(getHungerIcon());

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.restoreHunger);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setInt("hunger", hunger);
        nbt.setFloat("saturation", saturation);
        item = nbt.setItemData();

        return item;
    }

    public ItemStack experiencePotion(UUID uuid, LogType logType, float xp) {
        ItemStack item = new ItemStack(getExperienceIcon());
        MessageData messages = new MessageData();

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageData.restoreExperience);

        List<String> lore = new ArrayList<>();
        lore.add(messages.restoreExperienceLevel((int) RestoreInventory.getLevel(xp) + ""));
        meta.setLore(lore);

        item.setItemMeta(meta);

        NBT nbt = new NBT(item);

        nbt.setString("uuid", uuid.toString());
        nbt.setString("logType", logType.name());
        nbt.setFloat("xp", xp);
        item = nbt.setItemData();

        return item;
    }
}
