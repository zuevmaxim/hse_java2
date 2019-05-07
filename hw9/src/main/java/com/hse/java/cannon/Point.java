package com.hse.java.cannon;

import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;

public class Point extends Point2D {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(@NotNull Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double distance(@NotNull Point first, @NotNull Point second) {
        return Math.sqrt(MyMath.sqr(first.x - second.x) + MyMath.sqr(first.y - second.y));
    }
}