package com.hse.java.cannon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CannonTest {
    private Cannon cannon;

    @BeforeEach
    void init() {
        cannon = new Cannon(0);
    }

    @Test
    void moveRightXTest() {
        var state = cannon.getState();
        var x = cannon.getState().getX();
        for (int i = 0; i < 100; ++i) {
            cannon.move(1);
            assertTrue(state.getX() > x);
            x = state.getX();
        }
        for (int i = 0; i < 5; ++i) {
            cannon.move(1);
            assertEquals(x, state.getX());
        }
    }

    @Test
    void moveLeftXTest() {
        cannon = new Cannon(100);
        var state = cannon.getState();
        var x = cannon.getState().getX();
        for (int i = 0; i < 100; ++i) {
            cannon.move(-1);
            assertTrue(state.getX() < x);
            x = state.getX();
        }
        for (int i = 0; i < 5; ++i) {
            cannon.move(-1);
            assertEquals(x, state.getX());
        }
    }

    @Test
    void moveYAngleTest() {
        var state = cannon.getState();
        for (int i = 0; i < 100; ++i) {
            cannon.move(1);
            assertEquals(Landscape.getY(state.getX()), state.getY());
            assertEquals(Landscape.getAngle(state.getX()), state.getAngle());
        }
    }

    @Test
    void moveBarrelUpTest() {
        var state = cannon.getState();
        var alpha = cannon.getState().getBarrelAngle();
        assertEquals(0, alpha);
        for (int i = 0; i < 90; ++i) {
            cannon.moveBarrel(1);
            assertTrue(state.getBarrelAngle() > alpha);
            alpha = state.getBarrelAngle();
        }
        for (int i = 0; i < 5; ++i) {
            cannon.moveBarrel(1);
            assertEquals(alpha, state.getBarrelAngle());
        }
    }

    @Test
    void moveBarrelDownTest() {
        var state = cannon.getState();
        var alpha = cannon.getState().getBarrelAngle();
        assertEquals(0, alpha);
        for (int i = 0; i < 90; ++i) {
            cannon.moveBarrel(-1);
            assertTrue(state.getBarrelAngle() < alpha);
            alpha = state.getBarrelAngle();
        }
        for (int i = 0; i < 5; ++i) {
            cannon.moveBarrel(-1);
            assertEquals(alpha, state.getBarrelAngle());
        }
    }

    @Test
    void constructorExceptionTest() {
        assertThrows(IllegalArgumentException.class, () -> new Cannon(-1));
    }

    @Test
    void fire() {
        cannon = new Cannon(50);
        cannon.getNewTarget();
        var f = cannon.fire(5, 5, new Point(cannon.getState().getX(), cannon.getState().getY()));
        for (double t = 1; f.apply(t) != null; t += 0.2) {
            var point = f.apply(t);
            assertTrue(0 <= point.getX() && point.getX() <= 100);
            assertTrue(0 <= point.getY() && point.getY() <= 100);
            //assertFalse(cannon.isTargetArchived());
        }
    }

    @Test
    void getNewTarget() {
        var target = cannon.getNewTarget();
        assertTrue(0 <= target.getX() && target.getX() <= 100);
        assertTrue(0 <= target.getY() && target.getY() <= 100);
        assertFalse(cannon.isTargetArchived());
    }

    @Test
    void getSeveralNewTargets() {
        var target1 = cannon.getNewTarget();
        var target2 = cannon.getNewTarget();
        assertTrue(Math.abs(target1.getX() - target2.getX()) >= 5);
    }
}