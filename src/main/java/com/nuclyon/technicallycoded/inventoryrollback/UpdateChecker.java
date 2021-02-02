package com.nuclyon.technicallycoded.inventoryrollback;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
	
	// Credit to PatoTheBest and TRollStar12345 on SpigotMC for the below code
	// https://www.spigotmc.org/threads/resource-updater-for-your-plugins-v1-1.37315/
	// https://www.spigotmc.org/threads/check-for-updates-using-the-new-spigot-api.266310/
		
    private JavaPlugin plugin;
    private URL checkURL;

    private String currentVersion;
    private String[] currVersionSections;
    private String availableVersion;
   
    private UpdateResult result = UpdateResult.FAIL_SPIGOT;
   
    public enum UpdateResult {
        NO_UPDATE,
        FAIL_SPIGOT,
        UNKNOWN_VERSION,
        UPDATE_LOW,
        UPDATE_MEDIUM,
        UPDATE_HIGH,
        DEV_BUILD
    }
   
    public UpdateChecker(JavaPlugin plugin, Integer resourceId) {
        this.plugin = plugin;      
        this.currentVersion = this.plugin.getDescription().getVersion();
        this.currVersionSections = currentVersion.split("\\.");
       
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
			availableVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
		} catch (IOException e) {
			result = UpdateResult.FAIL_SPIGOT;
			return;
		}

        if (availableVersion.isEmpty()) {
            result = UpdateResult.FAIL_SPIGOT;
            return;
        }

        String[] versionSections = availableVersion.split("\\.");
        for (int i = 0; i < versionSections.length || i < currVersionSections.length; i++) {
            try {
                boolean vSecExists = versionSections.length - i > 0;
                boolean cvSecExists = currVersionSections.length - i > 0;
                if (!vSecExists) {
                    result = UpdateResult.DEV_BUILD;
                    return;
                } else if (!cvSecExists) {
                    result = getUpdateResultPriority(i);
                    return;
                }
                int vSecInt = Integer.parseInt(versionSections[i]);
                int cvSecInt = Integer.parseInt(currVersionSections[i]);
                if (vSecInt > cvSecInt) {
                    result = getUpdateResultPriority(i);
                    return;
                } else if (cvSecInt > vSecInt) {
                    result = UpdateResult.DEV_BUILD;
                    return;
                }
            } catch (NumberFormatException e) {
                result = UpdateResult.UNKNOWN_VERSION;
                return;
            }
        }
        result = UpdateResult.NO_UPDATE;
    }
   
    public UpdateResult getResult() {
        return this.result;
    }
   
    public String getVersion() {
        return this.availableVersion;
    }

    public UpdateResult getUpdateResultPriority(int i) {
        switch (i) {
            case 0:
                return UpdateResult.UPDATE_HIGH;
            case 1:
                return UpdateResult.UPDATE_MEDIUM;
            default:
                return UpdateResult.UPDATE_LOW;
        }
    }
	
}
