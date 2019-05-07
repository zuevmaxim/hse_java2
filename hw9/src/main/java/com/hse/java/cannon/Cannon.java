package com.hse.java.cannon;

import java.util.Random;
import java.util.function.Function;

public class Cannon {
    private CannonState state;
    private Point target;
    private boolean isTargetArchived = false;
    private static final Random RANDOM = new Random();
    private static final double GRAVITY = 9.8;

    public Cannon(double x) {
        this(x, 0);
    }

    public Cannon(double x, double barrelAngle) {
        state = new CannonState(x, Landscape.getY(x), Landscape.getAngle(x), barrelAngle);
    }

    public void move(double delta) {
        double x = state.x + delta;
        if (x < 0 || x > 100) {
            return;
        }
        state.x += delta;
        state.y = Landscape.getY(state.x);
        state.angle = Landscape.getAngle(state.x);
    }

    public void moveBarrel(double delta) {
        double barrelAngle = state.barrelAngle + delta;
        if (barrelAngle < -90 || barrelAngle > 90) {
            return;
        }
        state.barrelAngle += delta;
    }

    public Function<Double, Point> fire(int size, double distance, double barrelLength) {
        final double angle = Math.toRadians(state.angle + 90 - state.barrelAngle);
        final double x0 = state.x + barrelLength * Math.cos(angle);
        final double y0 = state.y + barrelLength * Math.sin(angle);
        final double v0 = 100.0 / size ;
        final Cannon cannon = this;
        return (t) -> {
          assert t >= 0;
          double x = x0 + v0 * t * Math.cos(angle);
          double y = y0 + v0 * t * Math.sin(angle) - GRAVITY * t * t / 2;
          if (x < 0 || x > 100 || y < Landscape.getY(x)) {
              return null;
          }

          var point = new Point(x, y);
          if (Point.distance(point, target) < distance) {
              cannon.isTargetArchived = true;
          }
          return point;
        };
    }

    public Point getNewTarget() {
        isTargetArchived = false;
        double x = RANDOM.nextInt(100);
        target = new Point(x, Landscape.getY(x));
        return new Point(target);
    }

    public CannonState getState() {
        return state;
    }

    static class CannonState {
        private CannonState(double x, double y, double angle, double barrelAngle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.barrelAngle = barrelAngle;
        }
        private double x;
        private double y;
        private double angle;
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
