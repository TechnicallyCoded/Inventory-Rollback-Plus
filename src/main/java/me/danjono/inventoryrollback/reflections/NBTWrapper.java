package me.danjono.inventoryrollback.reflections;

import org.bukkit.inventory.ItemStack;

public class NBTWrapper {
	
	private ItemStack item;
	private NMSHandler nmsHandler;
	
	public NBTWrapper(ItemStack item) {
		nmsHandler = new NMSHandler();
		this.item = item;
	}
	
	public ItemStack setItemData() {
		return item;
	}
	
	public boolean hasUUID() {
		String uuid = getString("uuid");
		
		return (uuid != null && !uuid.isEmpty());
	}
		
	public ItemStack setString(String key, String data) {        
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod("setString", String.class, String.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod("setTag", comp.getClass()).invoke(itemstack, comp);	
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setInt(String key, int data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod("setInt", String.class, int.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod("setTag", comp.getClass()).invoke(itemstack, comp);	
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return item;
	}
	
	public ItemStack setLong(String key, Long data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod("setLong", String.class, long.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod("setTag", comp.getClass()).invoke(itemstack, comp);	
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setDouble(String key, Double data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod("setDouble", String.class, double.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod("setTag", comp.getClass()).invoke(itemstack, comp);	
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setFloat(String key, Float data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod("setFloat", String.class, float.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod("setTag", comp.getClass()).invoke(itemstack, comp);	
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
							
	public String getString(String key) {
		Object result = null;
		Object comp = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			try { comp = itemstack.getClass().getMethod("getTag").invoke(itemstack); } catch (NullPointerException e) { return null; }
			
			try { result = comp.getClass().getMethod("getString", String.class).invoke(comp, key); } catch (NullPointerException e) { return null; }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (String) result;
	}
	
	public int getInt(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			try { result = comp.getClass().getMethod("getInt", String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (int) result;
	}
	
	public Long getLong(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			try { result = comp.getClass().getMethod("getLong", String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (long) result;
	}
	
	public double getDouble(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			try { result = comp.getClass().getMethod("getDouble", String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (double) result;
	}
	
	public Float getFloat(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod("getTag").invoke(itemstack);
			
			try { result = comp.getClass().getMethod("getFloat", String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (float) result;
	}
				
}
