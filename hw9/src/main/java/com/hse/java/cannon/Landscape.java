package com.hse.java.cannon;

public class Landscape {
    public static double getY(double x) {
        assert 0 <= x && x <= 100;
        if (x < 10) {
            return 10;
        } else if (x < 20) {
            //noinspection SuspiciousNameCombination
            return x;
        } else if (x < 30) {
            return 40 - x;
        } else if (x < 50) {
            return 2 * x - 50;
        } else if (x < 70) {
            return 100 - x;
        } else {
            return x - 40;
        }
    }

    public static double getAngle(double x) {
        assert 0 <= x && x <= 100;
        if (x < 10) {
            return 0;
        } else if (x < 20) {
            return 45;
        } else if (x < 30) {
            return -45;
        } else if (x < 50) {
            return Math.toDegrees(Math.atan(2));
        } else if (x < 70) {
            return -45;
        } else {
            return 45;
        }
    }
}
