![](https://github.com/TechnicallyCoded/Inventory-Rollback/blob/master/icons/inventoryrollbackplus_icon_128.png?raw=true)
# InventoryRollbackPlus

### Introduction

**Description**

InventoryRollbackPlus is a plugin which will backup player inventories for various events. This is very useful if players lose items due to lag, griefing and more!

**When does the plugin backup player inventories?**

When the a player: Joins, Leaves, Dies, Changes world, or when requested by staff.

**What does the plugin save?**

The plugin saves the player's: Inventory, Enderchest, Location, Health, Hunger, XP.

*Note: This plugin is a fork (extended version) of InventoryRollback but with more features and faster updates.*

**Why should you I use this version?**

There are many core features missing from the original plugin. Here are some of the features in this version that are not present in the original:
 - Tab completion for commands
 - Single button click to restore the entire inventory
 - Help message if you run /inventoryrollback without anything else
 - & more coming soon..

**How do I use the plugin?**

When a backup is created, it is added to a list of available backups to view and restore.

Players with the required permission can open a rollback menu by running the command /ir restore <name>. You will be presented will all the recent backups the plugin has made. To view a backup just click on the corresponding icon. You can now choose to restore what you want or go back to the list of backups.

The plugin saves 50 deaths and 10 joins, leaves and world changes by deafult. New deaths, joins, leaves and world changes will push old backups into deleted space :O
You can change these values in the configuration file.

### Documentation

**Commands**

 - /ir restore <player> - Open a menu to view all player backups
 - /ir forcebackup <player> - Create a backup manually
 - /ir enable - Enable the plugin if disabled
 - /ir disable - Disable the plugin if enabled
 - /ir reload - Reload the configuration file

**Permissions**

 - inventoryrollback.viewbackups - (Default: OP) Allow /ir restore command (without ability to give items back)
 - inventoryrollback.restore - (Default: OP) Allow /ir restore command
 - inventoryrollback.restore.teleport - (Default: OP) Allow player to teleport to location of backup
 - inventoryrollback.forcebackup - (Default: OP) Allow /ir forcebackup command
 - inventoryrollback.enable - (Default: OP) Allow /ir enable command
 - inventoryrollback.disable - (Default: OP) Allow /ir disable command
 - inventoryrollback.reload - (Default: OP) Allow /ir reload command
 - inventoryrollback.adminalerts - (Default: OP) Allow viewing important information for admins when they join

 - inventoryrollback.deathsave - (Default: All) Allow backup on death
 - inventoryrollback.joinsave - (Default: All) Allow backup on join
 - inventoryrollback.leavesave - (Default: All) Allow backup on leave
 - inventoryrollback.worldchangesave - (Default: All) Allow backup on world change
 - inventoryrollback.help - (Default: All) Allow viewing the help message of the plugin
 - inventoryrollback.version - (Default: All) Allow viewing version of the plugin

## Spigot Link
[https://www.spigotmc.org/resources/85811/](https://www.spigotmc.org/resources/85811/)
