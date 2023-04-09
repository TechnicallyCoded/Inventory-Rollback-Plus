package com.nuclyon.technicallycoded.inventoryrollback;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {
		
    private final JavaPlugin plugin;
    private URL checkURL;

    private final String currentVersion;
    private final String[] currVersionSections;
    private String availableVersion;
   
    private final UpdateResult result;
   
    public static class UpdateResult {

        private Type type;
        private String latestVer;
        private String currentVer;

        public UpdateResult(Type typeIn, String latestVerIn, String currentVerIn) {
            this.type = typeIn;
            this.latestVer = latestVerIn;
            this.currentVer = currentVerIn;
        }

        public void setType(Type typeIn) { this.type = typeIn; }
        public void setLatestVer(String latestVerIn) { this.latestVer = latestVerIn; }

        public Type getType() { return this.type; }
        public String getCurrentVer() { return this.currentVer; }
        public String getLatestVer() { return this.latestVer; }

        public enum Type {
            NO_UPDATE,
            FAIL_SPIGOT,
            UNKNOWN_VERSION,
            UPDATE_LOW,
            UPDATE_MEDIUM,
            UPDATE_HIGH,
            DEV_BUILD
        }
    }
   
    public UpdateChecker(JavaPlugin plugin, Integer resourceId) {
        this.plugin = plugin;      
        this.currentVersion = this.plugin.getDescription().getVersion();
        this.currVersionSections = currentVersion.split("\\.");

        this.result = new UpdateResult(UpdateResult.Type.FAIL_SPIGOT, null, this.currentVersion);

        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (MalformedURLException e) {
        	result.setType(UpdateResult.Type.FAIL_SPIGOT);
            return;
        }

        run();
    }
    
    private void run() {
        URLConnection con;
		try {
			con = checkURL.openConnection();
		} catch (IOException e1) {
            result.setType(UpdateResult.Type.FAIL_SPIGOT);
			return;
		}
        
        try {
			availableVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
		} catch (IOException e) {
            result.setType(UpdateResult.Type.FAIL_SPIGOT);
			return;
		}

        if (availableVersion.isEmpty()) {
            result.setType(UpdateResult.Type.FAIL_SPIGOT);
            return;
        }

        result.setLatestVer(availableVersion);

        // Version sections of remote
        String[] versionSections = availableVersion.split("\\.");
        // Test diff
        for (int i = 0; i < versionSections.length || i < currVersionSections.length; i++) { // Continue until both versions run out of sub sections
            try {
                // - Detailed walk through -
                // Statement below means: if number of sections is i+1 or greater
                // (Example: 5.3.2 has 3 sections and i = 0, true)
                //   - Explanation -
                // 5.3.2 has indexes 0-2, which also means if i = 3, we are on the 4th iteration and
                // ran out of available sub-sections
                boolean vSecExists = versionSections.length - i > 0;
                boolean cvSecExists = currVersionSections.length - i > 0; // current version has that many sections too?
                if (!vSecExists) { // if remote version doesn't have that many sub sections (aka, we are running something newer)
                    result.setType(UpdateResult.Type.DEV_BUILD);
                    return;
                } else if (!cvSecExists) { // if local version doesn't have that many sub sections (aka remote is running something newer)
                    result.setType(getUpdateResultPriority(i));
                    return;
                }
                int vSecInt = Integer.parseInt(versionSections[i]); // get int value of remote sub-section value
                int cvSecInt = Integer.parseInt(currVersionSections[i]); // get int value of local sub-section value
                if (vSecInt > cvSecInt) { // remote  > local ? We are out of date.
                    result.setType(getUpdateResultPriority(i));
                    return;
                } else if (cvSecInt > vSecInt) { // local > remote ? We are running something not yet released!
                    result.setType(UpdateResult.Type.DEV_BUILD);
                    return;
                }
            } catch (NumberFormatException e) {
                result.setType(UpdateResult.Type.UNKNOWN_VERSION); // Not a parsable number? Unknown version, since we can't compare!
                return;
            }
        }
        result.setType(UpdateResult.Type.NO_UPDATE);
    }
   
    public UpdateResult getResult() {
        return this.result;
    }
   
    public String getVersion() {
        return this.availableVersion;
    }

    public UpdateResult.Type getUpdateResultPriority(int i) {
        switch (i) {
            case 0:
                return UpdateResult.Type.UPDATE_HIGH;
            case 1:
                return UpdateResult.Type.UPDATE_MEDIUM;
            default:
                return UpdateResult.Type.UPDATE_LOW;
        }
    }
	
}
