// File: src/test/java/com/nuclyon/technicallycoded/inventoryrollback/util/serialization/Version2SerializationIntTest.java

package com.nuclyon.technicallycoded.inventoryrollback.util.serialization;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Version2SerializationIntTest {

    private void invokeWriteInt(OutputStream os, int value) throws Exception {
        Method writeInt = Version2Serialization.class.getDeclaredMethod("writeInt", OutputStream.class, int.class);
        writeInt.setAccessible(true);
        writeInt.invoke(null, os, value);
    }

    private int invokeReadInt(InputStream is) throws Exception {
        Method readInt = Version2Serialization.class.getDeclaredMethod("readInt", InputStream.class);
        readInt.setAccessible(true);
        Object result = readInt.invoke(null, is);
        return (Integer) result;
    }

    @Test
    public void testWriteReadIntZero() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int expected = 0;
        invokeWriteInt(baos, expected);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int actual = invokeReadInt(bais);
        assertEquals(expected, actual, "Value 0 should be read correctly");
    }

    @Test
    public void testWriteReadIntPositive() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int expected = 123456789;
        invokeWriteInt(baos, expected);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int actual = invokeReadInt(bais);
        assertEquals(expected, actual, "Positive value should be read correctly");
    }

    @Test
    public void testWriteReadIntNegative() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int expected = -987654321;
        invokeWriteInt(baos, expected);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int actual = invokeReadInt(bais);
        assertEquals(expected, actual, "Negative value should be read correctly");
    }

    @Test
    public void testWriteReadIntMaxValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int expected = Integer.MAX_VALUE;
        invokeWriteInt(baos, expected);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int actual = invokeReadInt(bais);
        assertEquals(expected, actual, "Integer.MAX_VALUE should be read correctly");
    }

    @Test
    public void testWriteReadIntMinValue() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int expected = Integer.MIN_VALUE;
        invokeWriteInt(baos, expected);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        int actual = invokeReadInt(bais);
        assertEquals(expected, actual, "Integer.MIN_VALUE should be read correctly");
    }
}