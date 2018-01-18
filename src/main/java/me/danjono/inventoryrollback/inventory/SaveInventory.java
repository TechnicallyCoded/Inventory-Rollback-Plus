package me.danjono.inventoryrollback.inventory;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.danjono.inventoryrollback.InventoryRollback;
import me.danjono.inventoryrollback.config.PlayerData;

public class SaveInventory {
		
	public void createSave(Player player, String logType, String saveReason) {
		PlayerData data = new PlayerData(player, logType);
		FileConfiguration inventoryData = data.getData();
		
		PlayerInventory inv = player.getInventory();
		Inventory enderchest = player.getEnderChest();
		
		ItemStack[] armour = null;
		if (InventoryRollback.isVersion_1_8())
			armour = inv.getArmorContents();
		
		int maxSaves = data.getMaxSaves();
				
		float xp = getTotalExperience(player);
		Long time = System.currentTimeMillis();	
		int saves = inventoryData.getInt("saves");

		if (data.getFile().exists() && maxSaves > 0) {
			if (saves >= maxSaves) {
				List<Double> timeSaved = new ArrayList<Double>();

				for (String times : inventoryData.getConfigurationSection("data").getKeys(false)) {
					timeSaved.add(Double.parseDouble(times));
				}
				
				int deleteAmount = saves - maxSaves + 1;
				
				for (int i = 0; i < deleteAmount; i++) {
					Double deleteData = Collections.min(timeSaved);
					DecimalFormat df = new DecimalFormat("#.##############");
					
					inventoryData.set("data." + df.format(deleteData), null);
					timeSaved.remove(deleteData);
					saves--;
				}
			}
		}

		inventoryData.set("data." + time + ".inventory", toBase64(inv));
		
		if (InventoryRollback.isVersion_1_8() && armour != null)
			inventoryData.set("data." + time + ".armour", toBase64(armour));
		
		inventoryData.set("data." + time + ".enderchest", toBase64(enderchest));
		inventoryData.set("data." + time + ".xp", xp);
		inventoryData.set("data." + time + ".health", player.getHealth());
		inventoryData.set("data." + time + ".hunger", player.getFoodLevel());
		inventoryData.set("data." + time + ".saturation", player.getSaturation());
		inventoryData.set("data." + time + ".location.world", player.getWorld().getName());
		inventoryData.set("data." + time + ".location.x", (int) player.getLocation().getX());
		inventoryData.set("data." + time + ".location.y", (int) player.getLocation().getY());
		inventoryData.set("data." + time + ".location.z", (int) player.getLocation().getZ());
		inventoryData.set("data." + time + ".logType", logType);

		if (saveReason != null)
			inventoryData.set("data." + time + ".deathReason", saveReason);
		
		inventoryData.set("saves", saves + 1);

		data.saveData();
	}
		
	//Conversion to Base64 code courtesy of github.com/JustRayz	
	private String toBase64(Inventory inventory) {
		return toBase64(inventory.getContents());
	}

	private String toBase64(ItemStack[] contents) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			dataOutput.writeInt(contents.length);

			for (ItemStack stack : contents) {
				dataOutput.writeObject(stack);
			}
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	//Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
	//https://gist.github.com/RichardB122/8958201b54d90afbc6f0
	private float getTotalExperience(Player player) {
        int level = player.getLevel();
        float currentExp = player.getExp();
        int experience;
        int requiredExperience;
        
        if(level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            requiredExperience = 2 * level + 7;
        } else if(level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            requiredExperience = 5 * level - 38;
        } else {
            experience = (int) Math.ceil(((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220)));
            requiredExperience = 9 * level - 158;
        }
        
        experience += Math.ceil(currentExp * requiredExperience);
        
        return experience;
    }

}
