package me.danjono.inventoryrollback.inventory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class RestoreInventory {

	public ItemStack[] retrieveMainInventory(FileConfiguration playerData, Long timestamp) {
		ItemStack[] inv = null;

		try {
			inv = stacksFromBase64(playerData.getString("data." + timestamp + ".inventory"));
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}

		return inv;
	}
	
	public ItemStack[] retrieveEnderChestInventory(FileConfiguration playerData, Long timestamp) {
		ItemStack[] inv = null;

		try {
			inv = stacksFromBase64(playerData.getString("data." + timestamp + ".enderchest"));
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}

		return inv;
	}
	
	private ItemStack[] stacksFromBase64(String data) throws IOException {
		try {
			if(data == null || Base64Coder.decodeLines(data) == null) return new ItemStack[]{};
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] stacks = new ItemStack[dataInput.readInt()];

			for (int i = 0; i < stacks.length; i++) {
				stacks[i] = (ItemStack) dataInput.readObject();
			}
			dataInput.close();
			return stacks;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}
	
	public Double getHealth(FileConfiguration playerData, Long timestamp) {
		return playerData.getDouble("data." + timestamp + ".health");
	}

	public int getHunger(FileConfiguration playerData, Long timestamp) {
		return playerData.getInt("data." + timestamp + ".hunger");
	}
	
	public float getSaturation(FileConfiguration playerData, Long timestamp) {
		return (float) playerData.getDouble("data." + timestamp + ".saturation");
	}

	public float getXP(FileConfiguration playerData, Long timestamp) {
		return (float) playerData.getDouble("data." + timestamp + ".xp");
	}

	//Credits to Dev_Richard (https://www.spigotmc.org/members/dev_richard.38792/)
	//https://gist.github.com/RichardB122/8958201b54d90afbc6f0
	public void setTotalExperience(Player player, float xpFloat) {
        int xp = (int) xpFloat;
		
		//Levels 0 through 15
        if(xp >= 0 && xp < 351) {
            //Calculate Everything
            int a = 1; int b = 6; int c = -xp;
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
        } else if(xp >= 352 && xp < 1507) {
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
            player.setLevel(level);
            player.setExp(experience);
            //Level 31 and greater
        } else {
            //Calculate Everything
            double a = 4.5; double b = -162.5; int c = -xp + 2220;
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
	
	public float getLevel(float floatXP) {
        int xp = (int) floatXP;
		
		//Levels 0 through 15
        if(xp >= 0 && xp < 351) {
            //Calculate Everything
            int a = 1; int b = 6; int c = -xp;
            int level = (int) (-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a);
            int xpForLevel = (int) (Math.pow(level, 2) + (6 * level));
            int remainder = xp - xpForLevel;
            int experienceNeeded = (2 * level) + 7;
            float experience = (float) remainder / (float) experienceNeeded;
            experience = round(experience, 2);

            return level;
            //Levels 16 through 30
        } else if(xp >= 352 && xp < 1507) {
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
            double a = 4.5; double b = -162.5; int c = -xp + 2220;
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
        BigDecimal bd = new BigDecimal((double) d); //works the same, see http://stackoverflow.com/a/916110
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd.floatValue();
    }

}
