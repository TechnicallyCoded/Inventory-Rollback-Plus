package me.danjono.inventoryrollback.reflections;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NBT {

    private static final Method BUKKIT_AS_NMS_ITEM;
    private static final Method NMS_AS_BUKKIT_ITEM;
    private static final Method ITEM_GET_TAG;
    private static final Method ITEM_SET_TAG;
    private static final Constructor<?> NBT_TAG_CONSTRUCTOR;

    static {
        try {
            Class<?> nmsItemStackClass = Packets.getNMSClass("ItemStack");
            Class<?> craftItemStackClass = Packets.getCraftBukkitClass("inventory.CraftItemStack");
            Class<?> nbtClass = Packets.getCraftBukkitClass("NBTTagCompound");
            BUKKIT_AS_NMS_ITEM = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            NMS_AS_BUKKIT_ITEM = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
            NBT_TAG_CONSTRUCTOR = nbtClass.getConstructor();
            ITEM_GET_TAG = nmsItemStackClass.getMethod("getTag");
            ITEM_SET_TAG = nmsItemStackClass.getMethod("setTag", nbtClass);

        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ItemStack item;

    public NBT(ItemStack item) {
        this.item = item;
    }

    public ItemStack setItemData() {
        return item;
    }

    public boolean hasUUID() {
        String uuid = getString("uuid");

        return uuid != null && !uuid.isEmpty();
    }

    public ItemStack setString(String key, String data) {
        try {
            Object itemstack = BUKKIT_AS_NMS_ITEM.invoke(null, item);
            Object comp = ITEM_GET_TAG.invoke(itemstack);

            if (comp == null) {
                comp = NBT_TAG_CONSTRUCTOR.newInstance();
            }

            comp.getClass().getMethod("setString", String.class, String.class).invoke(comp, key, data);

            ITEM_SET_TAG.invoke(itemstack, comp);
            item = (ItemStack) NMS_AS_BUKKIT_ITEM.invoke(null, itemstack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public ItemStack setInt(String key, int data) {
        try {
            Object itemstack = BUKKIT_AS_NMS_ITEM.invoke(null, item);
            Object comp = ITEM_GET_TAG.invoke(itemstack);

            if (comp == null) {
                comp = NBT_TAG_CONSTRUCTOR.newInstance();
            }

            comp.getClass().getMethod("setInt", String.class, int.class).invoke(comp, key, data);

            ITEM_SET_TAG.invoke(itemstack, comp);
            item = (ItemStack) NMS_AS_BUKKIT_ITEM.invoke(null, itemstack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public ItemStack setLong(String key, long data) {
        try {
            Object itemstack = NMS_AS_BUKKIT_ITEM.invoke(null, item);
            Object comp = ITEM_GET_TAG.invoke(itemstack);

            if (comp == null) {
                comp = NBT_TAG_CONSTRUCTOR.newInstance();
            }

            comp.getClass().getMethod("setLong", String.class, long.class).invoke(comp, key, data);

            ITEM_SET_TAG.invoke(itemstack, comp);
            item = (ItemStack) NMS_AS_BUKKIT_ITEM.invoke(null, itemstack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public ItemStack setDouble(String key, double data) {
        try {
            Object itemstack = BUKKIT_AS_NMS_ITEM.invoke(null, item);
            Object comp = ITEM_GET_TAG.invoke(itemstack);

            if (comp == null) {
                comp = NBT_TAG_CONSTRUCTOR.newInstance();
            }

            comp.getClass().getMethod("setDouble", String.class, double.class).invoke(comp, key, data);

            ITEM_SET_TAG.invoke(itemstack, comp);
            item = (ItemStack) NMS_AS_BUKKIT_ITEM.invoke(null, itemstack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public ItemStack setFloat(String key, float data) {
        try {
            Object itemstack =BUKKIT_AS_NMS_ITEM.invoke(null, item);
            Object comp = ITEM_GET_TAG.invoke(itemstack);

            if (comp == null) {
                comp = NBT_TAG_CONSTRUCTOR.newInstance();
            }

            comp.getClass().getMethod("setFloat", String.class, float.class).invoke(comp, key, data);

            ITEM_SET_TAG.invoke(itemstack, comp);
            item = (ItemStack) NMS_AS_BUKKIT_ITEM.invoke(null, itemstack);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public String getString(String key) {
        if (item == null || key == null) {
            return null;
        }

        try {
            Object comp = getNBTCompound();

            if (comp == null) {
                return null;
            }
            return (String) comp.getClass().getMethod("getString", String.class).invoke(comp, key);

        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }

    }

    public int getInt(String key) {

        if (item == null || key == null) {
            return 0;
        }

        try {
            Object comp = getNBTCompound();

            return (int) comp.getClass().getMethod("getInt", String.class).invoke(comp, key);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Long getLong(String key) {

        if (item == null || key == null) {
            return null;
        }

        try {
            Object comp = getNBTCompound();
            if (comp == null) {
                return null;
            }
            return (Long) comp.getClass().getMethod("getLong", String.class).invoke(comp, key);
        } catch (ReflectiveOperationException  e) {
            e.printStackTrace();
        }
        return null;
    }

    public Double getDouble(String key) {

        if (item == null || key == null) {
            return null;
        }

        try {
            Object comp = getNBTCompound();
            if (comp == null) {
                return null;
            }

            return (Double) comp.getClass().getMethod("getDouble", String.class).invoke(comp, key);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Float getFloat(String key) {

        if (item == null || key == null) {
            return null;
        }

        try {
            Object comp = getNBTCompound();
            if (comp == null)  {
                return null;
            }
            return (Float) comp.getClass().getMethod("getFloat", String.class).invoke(comp, key);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Object getNBTCompound() throws ReflectiveOperationException {
        Object itemstack = BUKKIT_AS_NMS_ITEM.invoke(null, item);
        return ITEM_GET_TAG.invoke(itemstack);
    }

}
