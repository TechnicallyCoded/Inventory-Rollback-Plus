package me.danjono.inventoryrollback.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerData {
	
	private UUID uuid;
	private String logType;
	private File folderLocation;
	
	private File playerFile;
	private FileConfiguration playerData;
	
	public PlayerData(Player player, String logType) {
		this.logType = logType;
		this.uuid = player.getUniqueId();		
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
		
		findPlayerFile();
		findPlayerData();
	}
	
	public PlayerData(OfflinePlayer player, String logType) {
		this.logType = logType;
		this.uuid = player.getUniqueId();	
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
		
		findPlayerFile();
		findPlayerData();
	}
	
	public PlayerData(UUID uuid, String logType) {
		this.logType = logType;
		this.uuid = uuid;	
		this.folderLocation = new File(ConfigFile.folderLocation, "saves/");
		
		findPlayerFile();
		findPlayerData();
	}
		
	private boolean findPlayerFile() {	
		System.out.println(new File(folderLocation, "joins/" + uuid + ".yml"));
		
		if (logType.equalsIgnoreCase("JOIN")) {
			this.playerFile = new File(folderLocation, "joins/" + uuid + ".yml");
		} else if (logType.equalsIgnoreCase("QUIT")) {
			this.playerFile = new File(folderLocation, "quits/" + uuid + ".yml");
		} else if (logType.equalsIgnoreCase("DEATH")) {
			this.playerFile = new File(folderLocation, "deaths/" + uuid + ".yml");
		} else if (logType.equalsIgnoreCase("WORLDCHANGE")) {
			this.playerFile = new File(folderLocation, "worldChanges/" + uuid + ".yml");
		}
		
		if (this.playerFile == null)
			return false;
			
		return true;
	}

	private boolean findPlayerData() {
		this.playerData = YamlConfiguration.loadConfiguration(playerFile);
		
		if (this.playerData == null)
			return false;
		
		return true;
	}
	
	public File getFile() {
		return this.playerFile;
	}
	
	public FileConfiguration getData() {
		return this.playerData;
	}
	
	public boolean saveData() {
		try {
			playerData.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public int getMaxSaves() {
		if (logType.equalsIgnoreCase("JOIN")) {
			return ConfigFile.maxSavesJoin;
		} else if (logType.equalsIgnoreCase("QUIT")) {
			return ConfigFile.maxSavesQuit;
		} else if (logType.equalsIgnoreCase("DEATH")) {
			return ConfigFile.maxSavesDeath;
		} else if (logType.equalsIgnoreCase("WORLDCHANGE")) {
			return ConfigFile.maxSavesWorldChange;
		} else {
			return 0;
		}
	}

}
