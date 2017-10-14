package me.danjono.inventoryrollback.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import me.danjono.inventoryrollback.config.Messages;
import me.danjono.inventoryrollback.inventory.RestoreInventory;
import me.danjono.inventoryrollback.reflections.NBT;

public class Buttons {

	public ItemStack nextButton(String displayName, UUID uuid, String logType, int page, List<String> lore) {
		ItemStack button = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) button.getItemMeta();
        
        List<Pattern> patterns = new ArrayList<Pattern>();
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.BASE)); 
        patterns.add(new Pattern(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)); 
        patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL));
        patterns.add(new Pattern(DyeColor.GRAY, PatternType.BORDER));
        
        meta.setPatterns(patterns);
        
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        
        if (displayName != null) {
        	meta.setDisplayName(displayName);
        }
        
        if (lore != null) {
        	meta.setLore(lore);
        } else {
        	meta.setLore(null);
        }
        
        button.setItemMeta(meta);
        
        NBT nbt = new NBT(button);
        
        nbt.setString("uuid", uuid + "");
        nbt.setString("logType", logType);
        nbt.setInt("page", page);
        button = nbt.setItemData();   
        
        return button;
	}
	
	public ItemStack backButton(String displayName, UUID uuid, String logType, int page, List<String> lore) {
		ItemStack button = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) button.getItemMeta();
        
        List<Pattern> patterns = new ArrayList<Pattern>();
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
        
        nbt.setString("uuid", uuid + "");
        nbt.setString("logType", logType);
        nbt.setInt("page", page);
        button = nbt.setItemData();
        
        return button;
	}
	
	public ItemStack mainMenuBackButton(String displayName, UUID uuid) {
		ItemStack button = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) button.getItemMeta();
        
        List<Pattern> patterns = new ArrayList<Pattern>();
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
        
        nbt.setString("uuid", uuid + "");
        button = nbt.setItemData();
                
        return button;
	}
	
	public ItemStack inventoryMenuBackButton(String displayName, UUID uuid, String logType) {
		ItemStack button = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) button.getItemMeta();
        
        List<Pattern> patterns = new ArrayList<Pattern>();
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
        
        nbt.setString("uuid", uuid + "");
        nbt.setString("logType", logType);
        button = nbt.setItemData();
        
        return button;
	}
	
	public ItemStack createInventoryButton(ItemStack item, UUID uuid, String logType, Long time, String displayName, List<String> lore) {    	
    	ItemMeta meta = item.getItemMeta();
		//meta.setDisplayName(name);
    	
		if (lore != null) {	    	
   			meta.setLore(lore);
   		}
		
		meta.setDisplayName(displayName);
		
		item.setItemMeta(meta);
		
		NBT nbt = new NBT(item);
   		
		nbt.setString("uuid", uuid + "");
		nbt.setString("logType", logType);
		nbt.setLong("timestamp", time);
		item = nbt.setItemData();
		
    	return item;
    }
	
	public ItemStack createLogTypeButton(ItemStack item, UUID uuid, String name, String logType, List<String> lore) {    	
    	ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
    	
		if (lore != null) {	    	
   			meta.setLore(lore);
   		}
		
		item.setItemMeta(meta);
		
		NBT nbt = new NBT(item);
   		
		nbt.setString("logType", logType);
		nbt.setString("uuid", uuid + "");
		item = nbt.setItemData();
		
    	return item;
    }
	
	public ItemStack playerHead(String playerName, List<String> lore) {    	
    	ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
    	
    	SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    	skullMeta.setOwner(playerName);
    	skullMeta.setDisplayName(ChatColor.RESET + playerName);
    	
		if (lore != null) {	    	
			skullMeta.setLore(lore);
   		}
    	
    	skull.setItemMeta(skullMeta);
		    			
    	return skull;
    }
	
	public ItemStack enderChestButton(UUID uuid, String logType, Long timestamp) {    	
    	ItemStack item = new ItemStack(Material.ENDER_CHEST);
    	
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(Messages.restoreEnderChest);
    	    	
    	item.setItemMeta(meta);
    	
    	NBT nbt = new NBT(item);
    	
    	nbt.setString("uuid", uuid + "");
    	nbt.setString("logType", logType);
    	nbt.setLong("timestamp", timestamp);
    	item = nbt.setItemData();
		    			
    	return item;
    }
	
	public ItemStack healthButton(UUID uuid, String logType, Double health) {    	
    	ItemStack item = new ItemStack(Material.MELON);
    	
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(Messages.restoreFood);
    	    	
    	item.setItemMeta(meta);
    	
    	NBT nbt = new NBT(item);
    	
    	nbt.setString("uuid", uuid + "");
    	nbt.setString("logType", logType);
    	nbt.setDouble("health", health);
    	item = nbt.setItemData();
		    			
    	return item;
    }
	
	public ItemStack hungerButton(UUID uuid, String logType, int hunger, float saturation) {    	
    	ItemStack item = new ItemStack(Material.ROTTEN_FLESH);
    	
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(Messages.restoreHunger);
    	    	
    	item.setItemMeta(meta);
    	
    	NBT nbt = new NBT(item);
    	
    	nbt.setString("uuid", uuid + "");
    	nbt.setString("logType", logType);
    	nbt.setInt("hunger", hunger);
    	nbt.setFloat("saturation", saturation);
    	item = nbt.setItemData();
		    			
    	return item;
    }
	
	public ItemStack experiencePotion(UUID uuid, String logType, float xp) {    	
    	ItemStack item = new ItemStack(Material.EXP_BOTTLE);
    	Messages messages = new Messages();
    	
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName(Messages.restoreExperience);
    	
    	List<String> lore = new ArrayList<String>();
    	lore.add(messages.restoreExperienceLevel((int) new RestoreInventory().getLevel(xp) + ""));
   		meta.setLore(lore);
    	
    	item.setItemMeta(meta);
    	
    	NBT nbt = new NBT(item);
    	
    	nbt.setString("uuid", uuid + "");
    	nbt.setString("logType", logType);
    	nbt.setFloat("xp", xp);
    	item = nbt.setItemData();
		    			
    	return item;
    }
	
}
