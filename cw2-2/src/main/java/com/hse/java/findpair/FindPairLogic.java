package com.hse.java.findpair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class FindPairLogic {
    private int size;
    private final Random random = new Random();
    private final int[][] numbers;
    private boolean state = false;
    private int previousI = -1;
    private int previousJ = -1;
    private int rest;

    public FindPairLogic(int size) {
        this.size = size;
        rest = size * size / 2;
        numbers = new int[size][size];
        fillRandom();
    }

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



    public class State {
        private boolean endGame;
        private int i;
        private int j;
        private int n;
        private int previousI;
        private int previousJ;
        private int previousN;
        private boolean hasResult;
        private boolean success;

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
