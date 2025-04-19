package com.nuclyon.technicallycoded.inventoryrollback.util.test;

public class TestAssertions {
    
    public static void assertNull(Object obj, String error) {
        if (obj != null) {
            throw new AssertionError(error);
        }
    }
    
    public static void assertNotNull(Object obj, String error) {
        if (obj == null) {
            throw new AssertionError(error);
        }
    }
    
    public static void assertEquals(Object expected, Object actual, String error) {
        if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
            throw new AssertionError(error);
        }
    }

}
