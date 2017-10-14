package me.danjono.inventoryrollback;

import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.danjono.inventoryrollback.commands.Commands;
import me.danjono.inventoryrollback.config.ConfigFile;
import me.danjono.inventoryrollback.listeners.ClickGUI;
import me.danjono.inventoryrollback.listeners.EventLogs;

public class InventoryRollback extends JavaPlugin {

	public static final Logger log = Logger.getLogger("Minecraft");
	public static InventoryRollback instance;

	public static String version;
	public static String packageVersion;

	@Override
	public void onEnable() {
		instance = this;
		version = instance.getDescription().getVersion();	
		packageVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
		
		bStats();
		startupTasks();	

		if (!isCompatible()) {			
			log.info(String.format("[%s]" + convertConsoleMessage(ChatColor.RED + " ** WARNING... Plugin may not be compatible with this version of Minecraft. **"), getDescription().getName()));
			log.info(String.format("[%s]" + convertConsoleMessage(ChatColor.RED + " ** Please fully test the plugin before using on your server as features may be broken. **"), getDescription().getName()));
		}

		this.getCommand("inventoryrollback").setExecutor(new Commands());

		this.getServer().getPluginManager().registerEvents(new ClickGUI(), instance);
		this.getServer().getPluginManager().registerEvents(new EventLogs(), instance);
	}

	@Override
	public void onDisable() {
		instance = null;
	}
	
	private String convertConsoleMessage(String text) {
		String os = System.getProperty("os.name").substring(0, 7);
		
		if (os.equalsIgnoreCase("Windows"))
			text = ChatColor.stripColor(text);
		
		return text;
	}

	private enum Versions {
		v1_8_R1,
		v1_8_R2,
		v1_8_R3,
		v1_9_R1,
		v1_9_R2,
		v1_10_R1,
		v1_11_R1,
		v1_12_R1
	}
	
	private enum v18to19 {
		v1_8_R1,
		v1_8_R2,
		v1_8_R3,
		v1_9_R1,
		v1_9_R2
	}

	private boolean isCompatible() {
		for (Versions v : Versions.values()) {
			if (v.name().equalsIgnoreCase(packageVersion)) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean isV18to19() {
		for (v18to19 v : v18to19.values()) {
			if (v.name().equalsIgnoreCase(packageVersion)) {
				return true;
			}
		}
		
		return false;
	}

	public static void startupTasks() {
		ConfigFile config = new ConfigFile();
		
		config.setVariables();
		config.createStorageFolders();				
	}
	
	@SuppressWarnings("unused")
	private void bStats() {
        Metrics metrics = new Metrics(this);
	}

}
