package me.danjono.inventoryrollback.util;

public class MathUtils {

    public static double squared(final double num) {
        return num * num;
    }

    public static long squared(final long num) {
        return num * num;
    }

    public static int squared(final int num) {
        return num * num;
    }

    public static double[] roots(double a, double b, double c) {
        double rhs = Math.sqrt(b * b - 4 * a * c);
        return new double[] {(-b + rhs) / (2 * a), (-b - rhs) / (2 * a)};
    }

}
