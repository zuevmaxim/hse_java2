package com.hse.java.cannon;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CannonUI extends Application {
    private final AtomicBoolean isTargetArchived = new AtomicBoolean(false);
    private int bombSize = 5;
    private int score = 0;
    private Thread timerThread;

    private final Cannon cannon = new Cannon(55);
    private final Pane pane = new Pane();
    private final Scene scene = new Scene(pane, WIDTH, HEIGHT);
    private Circle target;
    private final Label scoreText = new Label();
    private Tank tank;
    private final ProgressBar progressBar = new ProgressBar(1);

    private static final double HEIGHT = 600;
    private static final double WIDTH = 600;
    private static final double STEP_SIZE = 1.0;
    private static final double TANK_HEIGHT = 10;
    private static final double TANK_WIDTH = 30;
    private static final double TARGET_SIZE = 5;
    private static final double BARREL_STEP_SIZE = 2.0;
    private static final double BARREL_HEIGHT = 3;
    private static final double BARREL_WIDTH = 25;
    private static final int GAME_TIME = 60;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(scene);

        var mountains = new ArrayList<Line>();
        for (double i = 0; i < 100; i += STEP_SIZE) {
            double y = Landscape.getY(i);
            double nextX = i + STEP_SIZE;
            double nextY = Landscape.getY(nextX);
            mountains.add(new Line(
                    getXFromPerCent(i),
                    getYFromPerCent(y),
                    getXFromPerCent(nextX),
                    getYFromPerCent(nextY)));
        }

        progressBar.setLayoutX(WIDTH - 120);
        progressBar.setLayoutY(HEIGHT - 150);
        scoreText.setLayoutX(WIDTH - 120);
        scoreText.setLayoutY(HEIGHT - 100);

        pane.getChildren().addAll(mountains);
        pane.getChildren().addAll(scoreText, progressBar);

        var endGameButton = new Button("End game.");
        endGameButton.setLayoutX(WIDTH - 120);
        endGameButton.setLayoutY(HEIGHT - 50);
        pane.getChildren().add(endGameButton);
        endGameButton.setOnAction(event -> endGame());
        endGameButton.setFocusTraversable(false);

        startGame();

        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void setScoreText() {
        scoreText.setText("Score: " + score);
    }

    private void startGame() {
        progressBar.setProgress(1);
        timerThread = new Thread(() -> {
            int timeLeft = GAME_TIME;
            while (timeLeft > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                timeLeft--;
                progressBar.setProgress(timeLeft / (double) GAME_TIME);
            }
            Platform.runLater(this::endGame);
        });
        timerThread.start();
        score = 0;
        setScoreText();
        tank = new Tank(0, 0, TANK_WIDTH, TANK_HEIGHT);
        tank.set(cannon.getState());
        pane.getChildren().addAll(tank, tank.barrel);
        scene.setOnKeyPressed(tank);
        makeTarget();
    }

    private void endGame() {
        if (progressBar.getProgress() > 0) {
            timerThread.interrupt();
        }
        pane.getChildren().removeAll(tank, tank.barrel);
        var result = new Label("Your score: " + score);
        var layout = new FlowPane();
        layout.setOrientation(Orientation.VERTICAL);
        layout.setVgap(8);
        layout.setHgap(4);
        layout.setPadding(new Insets(15,15,15,15));


        var scene = new Scene(layout, 200, 300);
        var newWindow = new Stage();
        newWindow.setTitle("Game over");
        newWindow.setScene(scene);

        var newGameButton = new Button("Start game");
        newGameButton.setOnAction(event -> {
            newWindow.close();
            startGame();
        });

        var exitButton = new Button("Exit");
        exitButton.setOnAction(event -> Platform.exit());
        layout.getChildren().addAll(result, newGameButton, exitButton);
        newWindow.show();
    }

    private void makeTarget() {
        if (target != null) {
            pane.getChildren().remove(target);
        }
        isTargetArchived.set(false);
        var targetPoint = cannon.getNewTarget();
        target = new Circle(
                getXFromPerCent(targetPoint.getX()),
                getYFromPerCent(targetPoint.getY()),
                TARGET_SIZE,
                Color.BLACK);
        pane.getChildren().add(target);
    }

    private class Tank extends Rectangle implements EventHandler<KeyEvent> {
        private final Barrel barrel;

        private Tank(double x, double y, double width, double height) {
            super(x, y, width, height);
            setFill(Color.valueOf("#808000"));
            barrel = new Barrel(x, y, BARREL_WIDTH, BARREL_HEIGHT);
        }

        @Override
        public void handle(KeyEvent event) {
            double k = Math.max(1, Math.abs(cannon.getState().getAngle()) / 10);
            switch (event.getCode()) {
                case LEFT:
                    cannon.move(-STEP_SIZE / k);
                    break;
                case RIGHT:
                    cannon.move(STEP_SIZE / k);
                    break;
                case UP:
                    cannon.moveBarrel(BARREL_STEP_SIZE);
                    break;
                case DOWN:
                    cannon.moveBarrel(-BARREL_STEP_SIZE);
                    break;
                case SPACE:
                    final int size = bombSize;
                    final var f = cannon.fire(
                            size,
                            (2.0 * size + TARGET_SIZE) / HEIGHT * 100,
                            getBarrelEnding(cannon.getState()));
                    Runnable task = () -> {
                        double t = 0.0;
                        Point point = f.apply(t);
                        if (point == null) {
                            return;
                        }
                        var circle = new Circle(
                                getXFromPerCent(point.getX()),
                                getYFromPerCent(point.getY()),
                                size,
                                Color.RED);
                        boolean invisible = false;
                        final Runnable addCircle = () -> pane.getChildren().add(circle);
                        final Runnable removeCircle = () -> pane.getChildren().remove(circle);
                        Platform.runLater(addCircle);
                        while (point != null) {
                            circle.setCenterX(getXFromPerCent(point.getX()));
                            double y = getYFromPerCent(point.getY());
                            if (y >= 0) {
                                if (invisible) {
                                    invisible = false;
                                    Platform.runLater(addCircle);
                                }
                                circle.setCenterY(y);
                            } else {
                                if (!invisible) {
                                    invisible = true;
                                    Platform.runLater(removeCircle);
                                }
                            }
                            t += 0.1 - 0.07 / size;
                            point = f.apply(t);
                            if (cannon.isTargetArchived() && isTargetArchived.compareAndSet(false, true)) {
                                score++;
                                Platform.runLater(CannonUI.this::setScoreText);
                                for (double i = size; i <= 2.5 * size; i += 0.2) {
                                    circle.setRadius(i);
                                    try {
                                        Thread.sleep(50 / size);
                                    } catch (InterruptedException ignored) { }
                                }
                                Platform.runLater(CannonUI.this::makeTarget);
                                break;
                            }
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException ignored) { }
                        }
                        Platform.runLater(removeCircle);
                    };
                    new Thread(task).start();
                    break;
            }
            if (!event.getText().isEmpty() && Character.isDigit(event.getText().charAt(0)) && event.getText().charAt(0) != '0') {
                bombSize = Integer.parseInt(event.getText());
            }
            set(cannon.getState());
        }

        private final double betta = Math.toDegrees(Math.atan(TANK_HEIGHT / (TANK_WIDTH / 2)));

        private void set(Cannon.CannonState state) {
            double alpha = state.getAngle();
            double gamma = alpha - betta;
            double d = Math.sqrt(MyMath.sqr(TANK_HEIGHT) + MyMath.sqr(TANK_WIDTH / 2));
            setX(getXFromPerCent(state.getX()) - d * Math.cos(Math.toRadians(gamma)));
            setY(getYFromPerCent(state.getY()) + d * Math.sin(Math.toRadians(gamma)));
            getTransforms().clear();
            getTransforms().add(new Rotate(-state.getAngle(), getX(), getY()));

            double barrelAngle = state.getAngle() + 90 - state.getBarrelAngle();
            double barrelX = getXFromPerCent(state.getX()) - TANK_HEIGHT * Math.sin(Math.toRadians(alpha));
            double barrelY = getYFromPerCent(state.getY()) - TANK_HEIGHT * Math.cos(Math.toRadians(alpha));
            barrel.setX(barrelX - BARREL_HEIGHT / 2 * Math.sin(Math.toRadians(barrelAngle)));
            barrel.setY(barrelY + BARREL_HEIGHT / 2 * Math.cos(Math.toRadians(barrelAngle)));
            var barrelRotate = new Rotate(-barrelAngle, barrelX, barrelY);
            barrel.getTransforms().clear();
            barrel.getTransforms().add(barrelRotate);
        }

        private Point getBarrelEnding(Cannon.CannonState state) {
            double alpha = state.getAngle() + 90 - state.getBarrelAngle();
            double betta = Math.toDegrees(Math.atan((BARREL_HEIGHT / 2) / BARREL_WIDTH));
            double gamma = alpha - betta;
            double d = Math.sqrt(MyMath.sqr(BARREL_WIDTH) + MyMath.sqr(BARREL_HEIGHT / 2));
            double x = getXInPerCent(barrel.getX() + d * Math.cos(Math.toRadians(gamma)));
            double y = getYInPerCent(barrel.getY() - d * Math.sin(Math.toRadians(gamma)));
            return new Point(x, y);
        }

        private class Barrel extends Rectangle {
            private Barrel(double x, double y, double width, double height) {
                super(x, y, width, height);
                setFill(Color.BLACK);
            }
        }
    }

    private double getXFromPerCent(double x) {
        return x / 100 * WIDTH;
    }

    private double getYFromPerCent(double y) {
        return HEIGHT - y / 100 * HEIGHT;
    }

    private double getXInPerCent(double x) {
        return x * 100 / WIDTH;
    }

    private double getYInPerCent(double y) {
        return (HEIGHT - y) * 100 / HEIGHT;
    }
}