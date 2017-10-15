#InventoryRollback
​
This plugin will log a players' inventory, health, hunger, experience, and enderchest during certain events. Perfect if someone loses their gear because of an admin mishap or if a bad plugin accidentally wipes a players data for example! These logged events include:-

  Player death
  Player joining the server
  Player disconnecting from the server
  Player changing worlds
  
Staff with the required permission can open a GUI and select the required backup for the player. They can then click and drag the items the player requires off the GUI so they can pick them up. Clicking on the other icons enables you to restore the other attributes if required directly to the player.

##Commands
/ir restore <PLAYER> - Opens a GUI to select the backup you require.

##Permissions
inventoryrollback.restore - Allows access to /ir restore (Default: OP)

inventoryrollback.deathsave - Saves inventory on a player death. (Default: All)
inventoryrollback.joinsave - Saves inventory on joining the server. (Default: All)
inventoryrollback.leavesave - Saves inventory on leaving the server. (Default: All)
inventoryrollback.worldchangesave - Saves inventory when changing to a different world. (Default: All)