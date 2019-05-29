package com.hse.java.findpair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Find pair game logic part.
 */
public class FindPairLogic {
    /** Size of the board. */
    private int size;

    /** Random device. */
    private final Random random = new Random();

    /** Numbers on the board. */
    private final int[][] numbers;

    /** Current state. True iff button was clicked once. */
    private boolean state = false;

    /** X coordinate of the last move. */
    private int previousI = -1;

    /** Y coordinate of the last move. */
    private int previousJ = -1;

    /** Number of closed buttons. Game ends when it equals to zero. */
    private int rest;

    /**
     * Logic constructor
     * @param size board size
     *             Supposed to be even positive integer.
     */
    public FindPairLogic(int size) {
        this.size = size;
        rest = size * size / 2;
        numbers = new int[size][size];
        fillRandom();
    }

    /**
     * Fill numbers array with random numbers in pairs from 0 to size^2 / 2.
     */
    private void fillRandom() {
        int pairs = size * size / 2;
        var randoms = new ArrayList<Integer>(2 * pairs);
        for (int i = 0; i < pairs; i++) {
            int rand = random.nextInt(pairs);
            randoms.add(rand);
            randoms.add(rand);
        }
        Collections.shuffle(randoms);

        int t = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                numbers[i][j] = randoms.get(t);
                t++;
            }
        }
    }

    /**
     * Make move method.
     * @param i x coordinate
     * @param j y coordinate
     * @return State of the game after the move.
     */
    public State getState(int i, int j) {
        if (!state) {
            previousI = i;
            previousJ = j;
            int n = numbers[i][j];
            state = true;
            return new State(rest == 0, i, j, n, -1, -1, -1, false, false);
        } else {
            int previousN = numbers[previousI][previousJ];
            int n = numbers[i][j];
            if (n == previousN) {
                rest--;
            }
            var result = new State(rest == 0, i, j, n, previousI, previousJ, previousN, true, n == previousN);
            previousI = -1;
            previousJ = -1;
            state = false;
            return result;
        }
    }


    /** Game state. */
    public class State {
        /** True iff game should be finished. */
        private boolean endGame;

        /** X coordinate of the last move. */
        private int i;

        /** Y coordinate of the last move. */
        private int j;

        /** Number of the (i, j) button. */
        private int n;

        /** X coordinate of the previous move. */
        private int previousI;

        /** Y coordinate of the previous move. */
        private int previousJ;

        /** Number of the (previousI, previousJ) button. */
        private int previousN;

        /** If result is available. True iff even number of steps was made. */
        private boolean hasResult;

        /** True if the last move opened two equal numbers. */
        private boolean success;


        /** State constructor. */
        public State(boolean endGame, int i, int j, int n, int previousI, int previousJ, int previousN, boolean hasResult, boolean success) {
            this.endGame = endGame;
            this.i = i;
            this.j = j;
            this.n = n;
            this.previousI = previousI;
            this.previousJ = previousJ;
            this.previousN = previousN;
            this.hasResult = hasResult;
            this.success = success;
        }

        public boolean isEndGame() {
            return endGame;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean hasResult() {
            return hasResult;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public int getPreviousI() {
            return previousI;
        }

        public int getPreviousJ() {
            return previousJ;
        }

        public int getN() {
            return n;
        }

        public int getPreviousN() {
            return previousN;
        }
    }

}
