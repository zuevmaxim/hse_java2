package com.hse.java.cannon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void setLocation() {
        var point = new Point(0, 0);
        point.setLocation(1, 1);
        assertEquals(1, point.getX());
        assertEquals(1, point.getY());
    }

    @Test
    void distanceEqualPoints() {
        var point = new Point(0, 0);
        assertEquals(0, Point.distance(point, point));
    }

    @Test
    void distancePoints() {
        var point = new Point(3, 4);
        var zeroPoint = new Point(0, 0);
        assertEquals(5, Point.distance(point, zeroPoint));
    }
}