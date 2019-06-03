package com.hse.java.cannon;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Function;


/**
 * Class presents logic part of game Cannon.
 * All the coordinates are defined here as in usual XY system.
 */
public class Cannon {
    /** Current cannon state. */
    private final CannonState state;

    /** Current target point. */
    private Point target;

    /** Flag if target has been archived. */
    private boolean isTargetArchived = false;

    /** Random device. */
    private static final Random RANDOM = new Random();

    /** Gravity acceleration constant. */
    private static final double GRAVITY = 9.8;

    /** Target should be placed not too close to the screen end.*/
    private static final int TARGET_SCREEN_BOUND = 5;

    /** Minimum distance between new and previous target. */
    private static final double TARGET_PREVIOUS_OFFSET = 5;

    /**
     * Cannon constructor.
     * @param x tank abscissa in percent
     */
    public Cannon(double x) {
        this(x, 0);
    }

    /**
     * Cannon constructor.
     * @param x tank abscissa in percent
     * @param barrelAngle initial barrel angle in [-90, 90] (from vertical)
     */
    private Cannon(double x, @SuppressWarnings("SameParameterValue") double barrelAngle) {
        if (x < 0 || x > 100) {
            throw new IllegalArgumentException("X should be in [0, 100]");
        }
        state = new CannonState(x, Landscape.getY(x), Landscape.getAngle(x), barrelAngle);
    }

    /**
     * Move tank left or right.
     * Does nothing if tack reached screen end
     * @param delta step size
     */
    public void move(double delta) {
        double x = state.x + delta;
        if (x < 0 || x > 100) {
            return;
        }
        state.x += delta;
        state.y = Landscape.getY(state.x);
        state.angle = Landscape.getAngle(state.x);
    }

    /**
     * Move barrel up/down.
     * Does nothing if barrel reached horizontal position
     * @param delta angle diff
     */
    public void moveBarrel(double delta) {
        double barrelAngle = state.barrelAngle + delta;
        if (barrelAngle < -90 || barrelAngle > 90) {
            return;
        }
        state.barrelAngle += delta;
    }

    /**
     * Get fire function.
     * @param size bomb size
     * @param distance distance from target on which bomb fires
     * @param barrelEnding point of barrel ending
     * @return a function f : Time -> Point
     * that describes the bomb movement. The function returns null if screen end or target is archived.
     *
     * The bigger size is the smaller speed bomb has.
     */
    public Function<Double, Point> fire(int size, double distance, @NotNull Point barrelEnding) {
        final double angle = Math.toRadians(state.angle + 90 - state.barrelAngle);
        final double x0 = barrelEnding.getX();
        final double y0 = barrelEnding.getY();
        final double v0 = 50.0 / size + 10;
        final Cannon cannon = this;
        return (t) -> {
          assert t >= 0;
          double x = x0 + v0 * t * Math.cos(angle);
          double y = y0 + v0 * t * Math.sin(angle) - GRAVITY * t * t / 2;
          var point = new Point(x, y);
          if (x < 0 || x > 100 || y < Landscape.getY(x)) {
              if (Point.distance(point, target) < distance) {
                  cannon.isTargetArchived = true;
              }
              return null;
          }
          return point;
        };
    }

    /**
     * Set new target.
     * @return point where target is
     */
    public Point getNewTarget() {
        isTargetArchived = false;
        double x = RANDOM.nextInt(100 - 2 * TARGET_SCREEN_BOUND) + TARGET_SCREEN_BOUND;
        if (target != null) {
            double previousX = target.getX();
            while (Math.abs(x - previousX) < TARGET_PREVIOUS_OFFSET) {
                x = RANDOM.nextInt(100 - 2 * TARGET_SCREEN_BOUND) + TARGET_SCREEN_BOUND;
            }
        }
        target = new Point(x, Landscape.getY(x));
        return new Point(target);
    }

    /** Get current state. */
    public CannonState getState() {
        return state;
    }

    /** Cannon state class. */
    public static class CannonState {
        /** Constructor. */
        private CannonState(double x, double y, double angle, double barrelAngle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.barrelAngle = barrelAngle;
        }

        /** X coordinate. */
        private double x;

        /** Y coordinate. */
        private double y;

        /** Current mount angle where tank is. */
        private double angle;

        /** Current barrel angle from tank vertical. */
        private double barrelAngle;

        public double getAngle() {
            return angle;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getBarrelAngle() {
            return barrelAngle;
        }
    }

    public boolean isTargetArchived() {
        return isTargetArchived;
    }
}
