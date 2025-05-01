package com.nuclyon.technicallycoded.inventoryrollback.util.test;

import com.nuclyon.technicallycoded.inventoryrollback.InventoryRollbackPlus;
import com.nuclyon.technicallycoded.inventoryrollback.util.serialization.DeserializationResult;
import com.nuclyon.technicallycoded.inventoryrollback.util.serialization.Version2Serialization;
import com.tcoded.lightlibs.bukkitversion.MCVersion;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class SelfTestSerialization {

    public static void runTests() {
        List<SelfTest> tests = Arrays.asList(
                SelfTestSerialization.buildTestSerializeAndDeserializeItem(),
                SelfTestSerialization.buildTestSerializeAndDeserializeEmptyInventory(),
                SelfTestSerialization.buildTestSerializeAndDeserializeFullInventory(),
                SelfTestSerialization.buildTestSerializeAndDeserializeShulkerBoxWithItems(),
                SelfTestSerialization.buildTestSerializeAndDeserializeCustomNamedItem()
        );

        MCVersion currentVersion = InventoryRollbackPlus.getInstance().getVersion().getMcVersions()[0];

        int completed = 0;
        int skipped = 0;
        int failed = 0;

        for (SelfTest test : tests) {
            if (currentVersion.greaterOrEqThan(test.getMinVersion()) && currentVersion.lessOrEqThan(test.getMaxVersion())) {
                try {
                    test.run();
                    completed++;
                } catch (Exception e) {
                    e.printStackTrace();
                    failed++;
                }
            } else {
                skipped++;
            }
        }

        Logger logger = InventoryRollbackPlus.getInstance().getLogger();
        logger.info("Tests completed: " + completed + ", Skipped: " + skipped + ", Failed: " + failed);

        if (failed > 0) {
            logger.severe("Some tests failed. Please check the logs for details.");
        } else {
            logger.info("All tests passed successfully.");
        }

    }

    public static SelfTest buildTestSerializeAndDeserializeItem() {
        Runnable test = () -> {
            // Create a non-null item (requires a valid Bukkit Material environment)
            ItemStack original = new ItemStack(Material.DIRT, 10);
            ItemStack[] items = new ItemStack[]{original};
            String serialized = Version2Serialization.serialize(items);
            TestAssertions.assertNotNull(serialized, "Serialized data should not be null");

            DeserializationResult result = Version2Serialization.deserialize(serialized);
            TestAssertions.assertNull(result.getErrorMessage(), "There should be no error during deserialization");
            TestAssertions.assertNotNull(result.getItems(), "The deserialized array should not be null");
            TestAssertions.assertEquals(1, result.getItems().length, "The deserialized array should have one item");

            ItemStack deserialized = result.getItems()[0];
            TestAssertions.assertNotNull(deserialized, "Deserialized item should not be null");
            TestAssertions.assertEquals(original.getType(), deserialized.getType(), "Item types should be equal");
            TestAssertions.assertEquals(original.getAmount(), deserialized.getAmount(), "Item amounts should be equal");
        };

        return new SelfTest("Serialize and Deserialize Item", test);
    }

    public static SelfTest buildTestSerializeAndDeserializeEmptyInventory() {
        Runnable test = () -> {
            ItemStack[] items = new ItemStack[0];
            String serialized = Version2Serialization.serialize(items);
            TestAssertions.assertNotNull(serialized, "Serialized data should not be null");

            DeserializationResult result = Version2Serialization.deserialize(serialized);
            TestAssertions.assertNull(result.getErrorMessage(), "There should be no error during deserialization");
            TestAssertions.assertNotNull(result.getItems(), "The deserialized array should not be null");
            TestAssertions.assertEquals(0, result.getItems().length, "The deserialized array should be empty");
        };

        return new SelfTest("Serialize and Deserialize Empty Inventory", test);
    }

    public static SelfTest buildTestSerializeAndDeserializeFullInventory() {
        Runnable test = () -> {
            ItemStack[] items = new ItemStack[36];
            for (int i = 0; i < items.length; i++) {
                items[i] = new ItemStack(Material.STONE, i + 1);
            }
            String serialized = Version2Serialization.serialize(items);
            TestAssertions.assertNotNull(serialized, "Serialized data should not be null");

            DeserializationResult result = Version2Serialization.deserialize(serialized);
            TestAssertions.assertNull(result.getErrorMessage(), "There should be no error during deserialization");
            TestAssertions.assertNotNull(result.getItems(), "The deserialized array should not be null");
            TestAssertions.assertEquals(36, result.getItems().length, "The deserialized array should have 36 items");

            for (int i = 0; i < items.length; i++) {
                TestAssertions.assertEquals(items[i].getType(), result.getItems()[i].getType(), "Item types should match");
                TestAssertions.assertEquals(items[i].getAmount(), result.getItems()[i].getAmount(), "Item amounts should match");
            }
        };

        return new SelfTest("Serialize and Deserialize Full Inventory", test);
    }

    public static SelfTest buildTestSerializeAndDeserializeShulkerBoxWithItems() {
        Consumer<List<String>> test = logs -> {
            ItemStack shulkerBox = new ItemStack(Material.SHULKER_BOX);

            // Add items to the shulker box (requires a valid Bukkit API environment)
            ItemStack[] shulkerContents = new ItemStack[] {
                    new ItemStack(Material.DIAMOND, 5),
                    new ItemStack(Material.GOLD_INGOT, 10)
            };

            if (shulkerBox.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) shulkerBox.getItemMeta();
                if (meta.getBlockState() instanceof ShulkerBox) {
                    ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
                    shulker.getSnapshotInventory().setContents(shulkerContents);
                    meta.setBlockState(shulker);
                } else {
                    throw new IllegalStateException("BlockState is not ShulkerBox");
                }
                shulkerBox.setItemMeta(meta);
            } else {
                throw new IllegalStateException("ItemMeta is not BlockStateMeta");
            }

            logs.add("shulkerBox = " + shulkerBox);


            ItemStack[] items = new ItemStack[]{shulkerBox};
            String serialized = Version2Serialization.serialize(items);
            TestAssertions.assertNotNull(serialized, "Serialized data should not be null");

            logs.add("serialized = " + serialized);

            DeserializationResult result = Version2Serialization.deserialize(serialized);
            TestAssertions.assertNull(result.getErrorMessage(), "There should be no error during deserialization");
            TestAssertions.assertNotNull(result.getItems(), "The deserialized array should not be null");
            TestAssertions.assertEquals(1, result.getItems().length, "The deserialized array should have one item");

            ItemStack deserializedShulker = result.getItems()[0];
            TestAssertions.assertNotNull(deserializedShulker, "Deserialized shulker box should not be null");
            TestAssertions.assertEquals(Material.SHULKER_BOX, deserializedShulker.getType(), "Item type should be SHULKER_BOX");

            logs.add("deserializedShulker = " + deserializedShulker);

            // Verify shulker box contents
            ItemStack[] deserializedContents;
            if (deserializedShulker.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) deserializedShulker.getItemMeta();
                if (meta.getBlockState() instanceof ShulkerBox) {
                    ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
                    deserializedContents = shulker.getInventory().getContents();
                } else {
                    throw new IllegalStateException("BlockState is not ShulkerBox");
                }
            } else {
                throw new IllegalStateException("ItemMeta is not BlockStateMeta");
            }

            logs.add("deserializedContents = " + Arrays.toString(deserializedContents));

            int itemCount = 0;
            for (ItemStack item : deserializedContents) {
                if (item != null) itemCount++;
            }
            logs.add("itemCount = " + itemCount);

            TestAssertions.assertEquals(2, itemCount, "Shulker box should contain 2 items");
            TestAssertions.assertEquals(shulkerContents[0].getType(), deserializedContents[0].getType(), "First item type should match");
            TestAssertions.assertEquals(shulkerContents[0].getAmount(), deserializedContents[0].getAmount(), "First item amount should match");
            TestAssertions.assertEquals(shulkerContents[1].getType(), deserializedContents[1].getType(), "Second item type should match");
            TestAssertions.assertEquals(shulkerContents[1].getAmount(), deserializedContents[1].getAmount(), "Second item amount should match");
        };

        return new SelfTest("Serialize and Deserialize Shulker Box with Items", MCVersion.v1_11, test);
    }

    public static SelfTest buildTestSerializeAndDeserializeCustomNamedItem() {
        Runnable test = () -> {
            ItemStack customItem = new ItemStack(Material.DIAMOND_SWORD);
            // Set a custom name for the item
            ItemMeta itemMeta = customItem.getItemMeta();
            itemMeta.setDisplayName("Excalibur");
            customItem.setItemMeta(itemMeta);

            ItemStack[] items = new ItemStack[]{customItem};
            String serialized = Version2Serialization.serialize(items);
            TestAssertions.assertNotNull(serialized, "Serialized data should not be null");

            DeserializationResult result = Version2Serialization.deserialize(serialized);
            TestAssertions.assertNull(result.getErrorMessage(), "There should be no error during deserialization");
            TestAssertions.assertNotNull(result.getItems(), "The deserialized array should not be null");
            TestAssertions.assertEquals(1, result.getItems().length, "The deserialized array should have one item");

            ItemStack deserializedItem = result.getItems()[0];
            TestAssertions.assertNotNull(deserializedItem, "Deserialized item should not be null");
            TestAssertions.assertEquals(Material.DIAMOND_SWORD, deserializedItem.getType(), "Item type should be DIAMOND_SWORD");
            TestAssertions.assertEquals("Excalibur", deserializedItem.getItemMeta().getDisplayName(), "Custom name should match");
        };

        return new SelfTest("Serialize and Deserialize Custom Named Item", test);
    }

}
