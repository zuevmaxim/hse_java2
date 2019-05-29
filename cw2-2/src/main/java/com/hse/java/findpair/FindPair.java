package com.hse.java.findpair;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Find pair game.
 */
public class FindPair extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static int size;
    private Button[][] buttons;
    private FindPairLogic logic;
    private AtomicBoolean isGameEnable = new AtomicBoolean(true);
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);

    @Override
    public void start(Stage primaryStage) throws Exception {
        buttons = new Button[size][size];
        logic = new FindPairLogic(size);

        var pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        var scene = new Scene(pane, WIDTH, HEIGHT);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j] = new Button();
                buttons[i][j].setMaxWidth(Double.MAX_VALUE);
                buttons[i][j].setMaxHeight(Double.MAX_VALUE);
                buttons[i][j].setFocusTraversable(false);
                GridPane.setHgrow(buttons[i][j], Priority.ALWAYS);
                GridPane.setVgrow(buttons[i][j], Priority.ALWAYS);
                pane.add(buttons[i][j], i, j);
                int i1 = i;
                int j1 = j;
                buttons[i][j].setOnMouseClicked(event -> {
                    if (isGameEnable.get()) {
                        dealWithState(logic.getState(i1, j1));
                    }
                });
            }
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void dealWithState(FindPairLogic.State state) {
        buttons[state.getI()][state.getJ()].setText(Integer.toString(state.getN()));
        buttons[state.getI()][state.getJ()].setDisable(true);
        if (state.hasResult()) {
            if (!state.isSuccess()) {
                isGameEnable.set(false);
                threadPool.schedule(() -> Platform.runLater(() -> {
                    buttons[state.getI()][state.getJ()].setText("");
                    buttons[state.getI()][state.getJ()].setDisable(false);
                    buttons[state.getPreviousI()][state.getPreviousJ()].setText("");
                    buttons[state.getPreviousI()][state.getPreviousJ()].setDisable(false);
                    isGameEnable.set(true);
                }), 1, TimeUnit.SECONDS);
            }
        }
        if (state.isEndGame()) {
            endGame();
        }
    }

    private void endGame() {
        var gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(new Font(35));
        gameOverLabel.setTextFill(Color.RED);
        var result = new Label("Your score: " + (size * size / 2));
        result.setFont(new Font(30));
        var layout = new FlowPane();
        layout.setOrientation(Orientation.VERTICAL);
        layout.setVgap(8);
        layout.setHgap(4);
        layout.setPadding(new Insets(15,15,15,15));


        var scene = new Scene(layout, 300, 300);
        var newWindow = new Stage();
        newWindow.setTitle("Game over");
        newWindow.setScene(scene);
        var exitButton = new Button("Exit");
        exitButton.setFocusTraversable(false);
        exitButton.setOnAction(event -> System.exit(0));
        layout.getChildren().addAll(gameOverLabel, result, exitButton);
        newWindow.show();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Enter one argument -- size of the board.");
            System.exit(1);
        }
        size = Integer.parseInt(args[0]);
        if (size <= 0 || size % 2 != 0) {
            System.out.println("Size should be even positive number.");
            System.exit(1);
        }
        launch(args);
    }
}
