package me.danjono.inventoryrollback.reflections;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.tcoded.lightlibs.bukkitversion.BukkitVersion;
import me.danjono.inventoryrollback.config.ConfigData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Consumer;

public class NBTWrapper {

	private ItemStack item;
	private final NMSHandler nmsHandler;

	private static HashMap<Class<?>, String> getTagElementMethodNames;
	private static HashMap<Class<?>, String> setTagElementMethodNames;

	// 1.8 - 1.20.4
	private static String getTagMethodName;
	private static String setTagMethodName;

    private static Method getDataComponentMapMethod;

	private static Object customDataComponentMapKey; // custom_data key
	private static Method getDataComponentValueMethod;
	private static Method getCustomDataNBTCopyMethod;
	private static Method updateCustomDataNBTStaticMethod;

	public NBTWrapper(ItemStack item) {
		this.nmsHandler = new NMSHandler();
		this.item = item;
		
		if (getTagElementMethodNames == null) {
			InventoryRollbackPlus irp = InventoryRollbackPlus.getInstance();
			if (ConfigData.isDebugEnabled())
				irp.getLogger().info("NBTWrapper created for the first time since startup!");

			BukkitVersion nmsVersion = irp.getVersion();
			if (ConfigData.isDebugEnabled()) irp.getLogger().info("Using NMS Version: " + nmsVersion.toString());

			if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_20_R4)) {
				resolve1_20_5OrHigherReflectionNames(nmsVersion);
			} else {
				resolvePre1_20_5ReflectionNames(nmsVersion);
			}
			
			resolveNbtTagCompoundReflectionNames(nmsVersion);
		}
	}

	private static void resolve1_20_5OrHigherReflectionNames(BukkitVersion nmsVersion) {
		try {
            // 1.20.5 or higher (1.20.5 now places custom NBT in a custom_data component)
            String getDataComponentMapMethodName = "a";

			Class<?> dataComponentsClass = Class.forName("net.minecraft.core.component.DataComponents");
			customDataComponentMapKey = dataComponentsClass.getField("b").get(null);

			Class<?> nmsItemStackClass = Class.forName("net.minecraft.world.item.ItemStack");
			getDataComponentMapMethod = nmsItemStackClass.getMethod(getDataComponentMapMethodName);

			Class<?> dataComponentMapClass = Class.forName("net.minecraft.core.component.DataComponentMap");
			Class<?> dataComponentTypeClass = Class.forName("net.minecraft.core.component.DataComponentType");
			getDataComponentValueMethod = dataComponentMapClass.getMethod("a", dataComponentTypeClass);
			
			Class<?> customDataClass = Class.forName("net.minecraft.world.item.component.CustomData");
			Class<?> itemStackClass = Class.forName("net.minecraft.world.item.ItemStack");
			getCustomDataNBTCopyMethod = customDataClass.getMethod("c");
			updateCustomDataNBTStaticMethod = customDataClass.getMethod("a", dataComponentTypeClass, itemStackClass, Consumer.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void resolvePre1_20_5ReflectionNames(BukkitVersion nmsVersion) {
		if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_18_R1)) {

			if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_20_R1)) {
				getTagMethodName = "v";
			}
			else if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_19_R1)) {
				getTagMethodName = "u";
			}
			else if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_18_R2)) {
				getTagMethodName = "t";
			}
			else {
				getTagMethodName = "s";
			}

			setTagMethodName = "c";
		} else {
			getTagMethodName = "getTag";
			setTagMethodName = "setTag";
		}
	}
	
	private void resolveNbtTagCompoundReflectionNames(BukkitVersion nmsVersion) {
		getTagElementMethodNames = new HashMap<>();
		setTagElementMethodNames = new HashMap<>();
		
		if (nmsVersion.greaterOrEqThan(BukkitVersion.v1_18_R1)) {
			getTagElementMethodNames.put(Integer.class, "h");
			getTagElementMethodNames.put(Long.class, "i");
			getTagElementMethodNames.put(Float.class, "j");
			getTagElementMethodNames.put(Double.class, "k");
			getTagElementMethodNames.put(String.class, "l");

			setTagElementMethodNames.put(Integer.class, "a");
			setTagElementMethodNames.put(Long.class, "a");
			setTagElementMethodNames.put(Float.class, "a");
			setTagElementMethodNames.put(Double.class, "a");
			setTagElementMethodNames.put(String.class, "a");
		} else {
			getTagElementMethodNames.put(Integer.class, "getInt");
			getTagElementMethodNames.put(Long.class, "getLong");
			getTagElementMethodNames.put(Float.class, "getFloat");
			getTagElementMethodNames.put(Double.class, "getDouble");
			getTagElementMethodNames.put(String.class, "getString");

			setTagElementMethodNames.put(Integer.class, "setInt");
			setTagElementMethodNames.put(Long.class, "setLong");
			setTagElementMethodNames.put(Float.class, "setFloat");
			setTagElementMethodNames.put(Double.class, "setDouble");
			setTagElementMethodNames.put(String.class, "setString");
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
		return writeDataToBukkitItem(key, String.class, data);
	}

	public ItemStack setInt(String key, Integer data) {
		return writeDataToBukkitItem(key, int.class, data);
	}
	
	public ItemStack setLong(String key, Long data) {
		return writeDataToBukkitItem(key, long.class, data);
	}
	
	public ItemStack setDouble(String key, Double data) {
		return writeDataToBukkitItem(key, double.class, data);
	}
	
	public ItemStack setFloat(String key, Float data) {
		return writeDataToBukkitItem(key, float.class, data);
	}
							
	public String getString(String key) {
		return readDataFromBukkitItem(key, String.class, String.class);
	}

	public int getInt(String key) {
		return readDataFromBukkitItem(key, int.class, Integer.class);
	}
	
	public Long getLong(String key) {
		return readDataFromBukkitItem(key, long.class, Long.class);
	}
	
	public double getDouble(String key) {
		return readDataFromBukkitItem(key, double.class, Double.class);
	}
	
	public Float getFloat(String key) {
		return readDataFromBukkitItem(key, float.class, Float.class);
	}

	private @Nullable <T> T readDataFromBukkitItem(String key, Class<T> dataType, Class<?> mapType) {
		T result = null;

		try {
			Object itemstack = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack")
					.getMethod("asNMSCopy", ItemStack.class)
					.invoke(null, item);

			if (InventoryRollbackPlus.getInstance().getVersion().greaterOrEqThan(BukkitVersion.v1_20_R4)) {
				result = readCustomDataFromNmsItem(itemstack, key, dataType, mapType);
			} else {
				result = readNbtFromNmsItem(itemstack, key, dataType, mapType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        // noinspection ReassignedVariable
        return result;
	}

	private <T> T readCustomDataFromNmsItem(Object nmsItem, String key, Class<T> dataType, Class<?> mapType)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object compMap = getDataComponentMapMethod.invoke(nmsItem);
		Object customData = getDataComponentValueMethod.invoke(compMap, customDataComponentMapKey);
		if (customData == null) return null;
		Object nbtComp = getCustomDataNBTCopyMethod.invoke(customData);
		return this.readNbtValue(key, mapType, nbtComp);
	}

	private <T> T readNbtFromNmsItem(Object nmsItem, String key, Class<T> dataType, Class<?> mapType)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object comp = null;
		try {
			comp = nmsItem.getClass().getMethod(getTagMethodName).invoke(nmsItem);
		} catch (NullPointerException e) {
			return null;
		}

		return readNbtValue(key, mapType, comp);
	}

	private <T> @Nullable T readNbtValue(String key, Class<?> mapType, Object nbtComponent) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		try {
            // noinspection unchecked
			return (T) nbtComponent.getClass().getMethod(getTagElementMethodNames.get(mapType), String.class)
					.invoke(nbtComponent, key);
		} catch (NullPointerException e) {
			return null;
		}
	}

	private ItemStack writeDataToBukkitItem(String key, Class<?> dataType, Object data) {
		try {
			Object nmsItem = nmsHandler.getCraftBukkitClass("inventory.CraftItemStack")
					.getMethod("asNMSCopy", ItemStack.class)
					.invoke(null, item);

			if (InventoryRollbackPlus.getInstance().getVersion().greaterOrEqThan(BukkitVersion.v1_20_R4)) {
				writeCustomDataToNmsItem(nmsItem, key, dataType, data);
			} else {
				writeNbtToNmsItem(nmsItem, key, dataType, data);
			}

			item = (ItemStack) nmsHandler.getCraftBukkitClass("inventory.CraftItemStack")
					.getMethod("asBukkitCopy", nmsItem.getClass())
					.invoke(null, nmsItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}

	private void writeCustomDataToNmsItem(Object nmsItem, String key, Class<?> dataType, Object data)
			throws InvocationTargetException, IllegalAccessException {
		updateCustomDataNBTStaticMethod.invoke(null, customDataComponentMapKey, nmsItem, (Consumer<Object>) (comp) -> {
            try {
                writeNbtValue(comp, key, dataType, data);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        });
	}

	private void writeNbtToNmsItem(Object nmsItem, String key, Class<?> dataType, Object data)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
		Object comp = nmsItem.getClass().getMethod(getTagMethodName).invoke(nmsItem);

		if (comp == null) {
			if (InventoryRollbackPlus.getInstance().getVersion().greaterOrEqThan(BukkitVersion.v1_17_R1)) {
				comp = nmsHandler.getNMSClass("nbt.NBTTagCompound").newInstance();
			} else {
				comp = nmsHandler.getNMSClass("NBTTagCompound").newInstance();
			}
		}

		writeNbtValue(comp, key, dataType, data);

		nmsItem.getClass().getMethod(setTagMethodName, comp.getClass()).invoke(nmsItem, comp);
	}

	private static void writeNbtValue(Object nbtComponent, String key, Class<?> dataType, Object data) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		nbtComponent.getClass().getMethod(setTagElementMethodNames.get(data.getClass()), String.class, dataType)
				.invoke(nbtComponent, key, data);
	}

}
