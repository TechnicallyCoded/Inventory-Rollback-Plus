package me.danjono.inventoryrollback.reflections;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.nms.EnumNmsVersion;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class NBTWrapper {
	
	private ItemStack item;
	private final NMSHandler nmsHandler;

	private static String getTagMethodName;
	private static String setTagMethodName;

	private static HashMap<Class<?>, String> getTagElementMethodName;
	private static HashMap<Class<?>, String> setTagElementMethodName;

	public NBTWrapper(ItemStack item) {
		this.nmsHandler = new NMSHandler();
		this.item = item;
		
		if (getTagElementMethodName == null) {
			getTagElementMethodName = new HashMap<>();
			setTagElementMethodName = new HashMap<>();

			EnumNmsVersion nmsVersion = InventoryRollbackPlus.getInstance().getVersion();
			if (nmsVersion.isAtLeast(EnumNmsVersion.v1_18_R1)) {

				if (nmsVersion.isAtLeast(EnumNmsVersion.v1_18_R2)) {
					getTagMethodName = "t";
				} else {
					getTagMethodName = "s";
				}
				setTagMethodName = "c";

				getTagElementMethodName.put(String.class, "l");
				getTagElementMethodName.put(Integer.class, "h");
				getTagElementMethodName.put(Long.class, "i");
				getTagElementMethodName.put(Double.class, "k");
				getTagElementMethodName.put(Float.class, "j");

				setTagElementMethodName.put(String.class, "a");
				setTagElementMethodName.put(Integer.class, "a");
				setTagElementMethodName.put(Long.class, "a");
				setTagElementMethodName.put(Double.class, "a");
				setTagElementMethodName.put(Float.class, "a");

			} else {
				getTagMethodName = "getTag";
				setTagMethodName = "setTag";

				getTagElementMethodName.put(String.class, "getString");
				getTagElementMethodName.put(Integer.class, "getInt");
				getTagElementMethodName.put(Long.class, "getLong");
				getTagElementMethodName.put(Double.class, "getDouble");
				getTagElementMethodName.put(Float.class, "getFloat");

				setTagElementMethodName.put(String.class, "setString");
				setTagElementMethodName.put(Integer.class, "setInt");
				setTagElementMethodName.put(Long.class, "setLong");
				setTagElementMethodName.put(Double.class, "setDouble");
				setTagElementMethodName.put(Float.class, "setFloat");
			}
		}
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
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod(setTagElementMethodName.get(data.getClass()), String.class, String.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(itemstack, comp);
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setInt(String key, Integer data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod(setTagElementMethodName.get(data.getClass()), String.class, int.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(itemstack, comp);
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return item;
	}
	
	public ItemStack setLong(String key, Long data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod(setTagElementMethodName.get(data.getClass()), String.class, long.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(itemstack, comp);
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setDouble(String key, Double data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod(setTagElementMethodName.get(data.getClass()), String.class, double.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(itemstack, comp);
			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asBukkitCopy", itemstack.getClass()).invoke(null, itemstack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}
	
	public ItemStack setFloat(String key, Float data) {
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			if (comp == null) {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
			
			comp.getClass().getMethod(setTagElementMethodName.get(data.getClass()), String.class, float.class).invoke(comp, key, data);
			
			itemstack.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(itemstack, comp);
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
			try { comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack); } catch (NullPointerException e) { return null; }
			
			try { result = comp.getClass().getMethod(getTagElementMethodName.get(String.class), String.class).invoke(comp, key); } catch (NullPointerException e) { return null; }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (String) result;
	}
	
	public int getInt(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);

			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			try { result = comp.getClass().getMethod(getTagElementMethodName.get(Integer.class), String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (int) result;
	}
	
	public Long getLong(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			try { result = comp.getClass().getMethod(getTagElementMethodName.get(Long.class), String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (long) result;
	}
	
	public double getDouble(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			try { result = comp.getClass().getMethod(getTagElementMethodName.get(Double.class), String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (double) result;
	}
	
	public Float getFloat(String key) {
		Object result = null;
		
		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			Object comp = itemstack.getClass().getMethod(getTagMethodName).invoke(itemstack);
			
			try { result = comp.getClass().getMethod(getTagElementMethodName.get(Float.class), String.class).invoke(comp, key); } catch (NullPointerException e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (float) result;
	}
				
}
