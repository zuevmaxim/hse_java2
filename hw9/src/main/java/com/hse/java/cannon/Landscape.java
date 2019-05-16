package com.hse.java.cannon;

/** Fixed landscape. */
public class Landscape {
    /** Calculate y = f(x). */
    public static double getY(double x) {
        assert 0 <= x && x <= 100;
        if (x < 10) {
            return 5 * x;
        } else if (x < 20) {
            return -0.75 * x + 57.5;
        } else if (x < 30) {
            return -0.25 * x + 47.5;
        } else if (x < 40) {
            return 0.25 * x + 32.5;
        } else if (x < 50) {
            return 0.75 * x + 12.5;
        } else if (x < 60) {
            return 50;
        } else if (x < 70) {
            return 4 * x - 190;
        } else if (x < 80) {
            return -2 * x + 230;
        } else if (x < 90) {
            return x - 10;
        } else {
            return 2 * x - 100;
        }
    }

    /** Calculate angle = atan(f'(x)) */
    public static double getAngle(double x) {
        assert 0 <= x && x <= 100;
        double k;
        if (x < 10) {
            k = 5;
        } else if (x < 20) {
            k = -0.75;
        } else if (x < 30) {
            k = -0.25;
        } else if (x < 40) {
            k = 0.25;
        } else if (x < 50) {
            k = 0.75;
        } else if (x < 60) {
            k = 0;
        } else if (x < 70) {
            k = 4;
        } else if (x < 80) {
            k = -2;
        } else if (x < 90) {
            k = 1;
        } else {
            k = 2;
        }
        return Math.toDegrees(Math.atan(k));
    }
}
