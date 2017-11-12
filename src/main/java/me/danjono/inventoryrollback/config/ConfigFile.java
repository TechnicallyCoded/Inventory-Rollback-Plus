package me.danjono.inventoryrollback.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.danjono.inventoryrollback.InventoryRollback;

public class ConfigFile {
	
	private File configFile;
	private FileConfiguration config;
	
	public ConfigFile() {
		this.configFile = new File(folderLocation, "config.yml");
		if(!configFile.exists())
			InventoryRollback.instance.saveDefaultConfig();
		
		this.configFile = new File(folderLocation, "config.yml");
		this.config = YamlConfiguration.loadConfiguration(configFile);
	}

	public File getConfigFile() {
		return this.configFile;
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public boolean saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static boolean enabled = false;

	public void setEnabled(boolean enabled) {		
		ConfigFile.enabled = enabled;
		config.set("enabled", enabled);
		saveConfig();
	}
	
	public static File folderLocation = InventoryRollback.instance.getDataFolder();
	
	public static int maxSavesJoin;
	public static int maxSavesQuit;
	public static int maxSavesDeath;
	public static int maxSavesWorldChange;
	public static File savesLocation;
	
	public static String deathIcon;
	public static String joinIcon;
	public static String quitIcon;
	public static String worldChangeIcon;
	
	public static String timeZone;
	public static String timeFormat;
	
	public static boolean updateChecker;
	
	public void setVariables() {		
		String folder = config.getString("folderLocation");

		if (folder.equalsIgnoreCase("DEFAULT")) {
			folderLocation = InventoryRollback.instance.getDataFolder();
		} else {
			try {
				folderLocation = new File(folder);
			} catch (NullPointerException e) {
				folderLocation = InventoryRollback.instance.getDataFolder();
			}
		}

		enabled = config.getBoolean("enabled");
		
		maxSavesJoin = config.getInt("maxSaves.join");
		maxSavesQuit = config.getInt("maxSaves.quit");	
		maxSavesDeath = config.getInt("maxSaves.death");
		maxSavesWorldChange = config.getInt("maxSaves.worldChange");	
		savesLocation = new File(InventoryRollback.instance.getDataFolder(), "saves/");
		
		deathIcon = config.getString("icons.mainMenu.deathIcon.item");
		joinIcon = config.getString("icons.mainMenu.joinIcon.item");
		quitIcon = config.getString("icons.mainMenu.quitIcon.item");
		worldChangeIcon = config.getString("icons.mainMenu.worldChangeIcon.item");
		
		timeZone = config.getString("icons.rollbackMenu.time.timeZone");
		timeFormat = config.getString("icons.rollbackMenu.time.timeFormat");
		
		updateChecker = config.getBoolean("updateChecker", true);
		
		new Messages().setMessages(config); 	
		new Sounds().setSounds(config);
	}
	
	public void createStorageFolders() {		
		//Create folder for where player inventories will be saved
		File savesFolder = new File(folderLocation, "saves");
		if(!savesFolder.exists())
			savesFolder.mkdir();

		//Create folder for joins
		File joinsFolder = new File(folderLocation, "saves/joins");
		if(!joinsFolder.exists())
			joinsFolder.mkdir();

		//Create folder for quits
		File quitsFolder = new File(folderLocation, "saves/quits");
		if(!quitsFolder.exists())
			quitsFolder.mkdir();

		//Create folder for deaths
		File deathsFolder = new File(folderLocation, "saves/deaths");
		if(!deathsFolder.exists())
			deathsFolder.mkdir();

		//Create folder for world changes
		File worldChangesFolder = new File(folderLocation, "saves/worldChanges");
		if(!worldChangesFolder.exists())
			worldChangesFolder.mkdir();
	}

}
