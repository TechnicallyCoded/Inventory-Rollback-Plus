package me.danjono.inventoryrollback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.plugin.java.JavaPlugin;

public class UpdateChecker {
	
	//Credit to PatoTheBest and TRollStar12345 on SpigotMC for the below code
	//https://www.spigotmc.org/threads/resource-updater-for-your-plugins-v1-1.37315/
	//https://www.spigotmc.org/threads/check-for-updates-using-the-new-spigot-api.266310/
		
    private JavaPlugin plugin;
    private URL checkURL;

    private String currentVersion;
    private String availableVersion;
   
    private UpdateResult result = UpdateResult.FAIL_SPIGOT;
   
    public enum UpdateResult {
        NO_UPDATE,
        FAIL_SPIGOT,
        UPDATE_AVAILABLE
    }
   
    public UpdateChecker(JavaPlugin plugin, Integer resourceId) {
        this.plugin = plugin;      
        this.currentVersion = regex(this.plugin.getDescription().getVersion());
       
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (MalformedURLException e) {
        	result = UpdateResult.FAIL_SPIGOT;
            return;
        }

        run();
    }
    
    private void run() {
        URLConnection con = null;
		try {
			con = checkURL.openConnection();
		} catch (IOException e1) {
			result = UpdateResult.FAIL_SPIGOT;
			return;
		}
        
        try {
			availableVersion = regex(new BufferedReader(new InputStreamReader(con.getInputStream())).readLine());
		} catch (IOException e) {
			result = UpdateResult.FAIL_SPIGOT;
			return;
		}
        
        if (availableVersion.isEmpty()) {
        	result = UpdateResult.FAIL_SPIGOT;
        	return;
        } else if (availableVersion.equalsIgnoreCase(currentVersion)) {
        	result = UpdateResult.NO_UPDATE;
        	return;
    	} else if (!availableVersion.equalsIgnoreCase(currentVersion)) {
    		result = UpdateResult.UPDATE_AVAILABLE;
    		return;
    	}
    	
    	result = UpdateResult.FAIL_SPIGOT;
    	
    }
   
    public UpdateResult getResult() {
        return this.result;
    }
   
    public String getVersion() {
        return regex(this.availableVersion);
    }
    
    private String regex(String version) {
    	return version.replaceAll("[^0-9.]", "");
    }
	
}
