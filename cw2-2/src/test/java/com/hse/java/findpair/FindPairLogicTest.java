package com.hse.java.findpair;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FindPairLogicTest {
    private FindPairLogic logic;
    private static int N = 4;

    @BeforeEach
    void init() {
        logic = new FindPairLogic(N);
    }

    @Test
    void stateOneStepResultTest() {
        assertFalse(logic.getState(0, 0).hasResult());
    }

    @Test
    void stateTwoStepsResultTest() {
        var state1 = logic.getState(0, 0);
        var state2 = logic.getState(1, 1);
        assertTrue(state2.hasResult());
        assertEquals(0, state1.getI());
        assertEquals(0, state1.getJ());
        assertEquals(1, state2.getI());
        assertEquals(1, state2.getJ());
        assertEquals(0, state2.getPreviousI());
        assertEquals(0, state2.getPreviousJ());
        assertEquals(state1.getN(), state2.getPreviousN());
        assertTrue(0 <= state2.getN() && state2.getN() < N * N / 2);
        assertTrue(0 <= state2.getPreviousN() && state2.getPreviousN() < N * N / 2);
        if (state2.getN() == state2.getPreviousN()) {
            assertTrue(state2.isSuccess());
        }
    }

    @Test
    void gameTest() {
        boolean endGame = false;
        int i = 0;
        int rest = N * N / 2;
        while (!endGame && i < N * N) {
            var status1 = logic.getState(i / N, i % N);
            assertFalse(status1.hasResult());
            i++;
            var status2 = logic.getState(i / N, i % N);
            assertTrue(status2.hasResult());
            if (status2.isSuccess()) {
                rest--;
                assertEquals(status2.getN(), status2.getPreviousN());
            }
            assertEquals(rest == 0, status2.isEndGame());
            i++;
            endGame = rest == 0;
        }
    }

}