![](https://i.imgur.com/KmwQQoi.png)
# Inventory Rollback

### Minecraft Bukkit Plugin - Tested with versions 1.8.8 - 1.16.3

This plugin will log a players' inventory, health, hunger, experience, and ender chest during certain events. Perfect if someone loses their gear because of an admin mishap or if a bad plugin accidentally wipes a players data for example! These logged events include:-  

-   Player death
-   Player joining the server
-   Player disconnecting from the server
-   Player changing worlds

Staff with the required permission can open a GUI and select the required backup for the player. They can then click and drag the items the player requires off the GUI so they can pick them up. Clicking on the other icons enables you to restore the other attributes if required directly to the player.  
  
By default, it will log 50 deaths and 10 joins, disconnects, world changes and force saves each per player before the old data is purged to save space. These values can be changed in the config.  
  
**If upgrading a current server from before 1.13 you will need to delete all your backup data due to the changes with materials in the newest versions.**

## Commands
/ir restore %**PLAYERNAME**% - Opens a GUI to select the backup you require.  
/ir forcebackup %**PLAYERNAME**% - Forces a backup for an online player.  

## Permissions

inventoryrollback.restore - Allows access to */ir restore* (Default: OP)  
inventoryrollback.forcebackup - Allows access to */ir forcebackup* (Default: OP)  

inventoryrollback.deathsave - Saves inventory on a player death. (Default: All)  
inventoryrollback.joinsave - Saves inventory on joining the server. (Default: All)  
inventoryrollback.leavesave - Saves inventory on leaving the server. (Default: All)  
inventoryrollback.worldchangesave  - Saves inventory when changing to a different world. (Default: All)  

## Spigot Link
[https://www.spigotmc.org/resources/inventory-rollback.48074/](https://www.spigotmc.org/resources/inventory-rollback.48074/)
