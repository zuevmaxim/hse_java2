import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BSTSetTest {

    private BSTSet<Integer> bstSet;

    @BeforeEach
    void setUp() {
        bstSet = new BSTSet<>();
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, bstSet.size());
    }

    @Test
    void sizeNonRepeatingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        assertEquals(N, bstSet.size());
    }

    @Test
    void sizeRepeatingElements() {
        bstSet.add(1);
        bstSet.add(1);
        bstSet.add(1);
        assertEquals(1, bstSet.size());
    }

    @Test
    void containsEmptySet() {
        assertFalse(bstSet.contains(1));
    }

    @Test
    void containsExistingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.contains(i));
        }
    }

    @Test
    void containsNonExistingElements() {
        for (int i = 0; i < 5; i++) {
            bstSet.add(2 * i);
        }
        assertFalse(bstSet.contains(1));
        assertFalse(bstSet.contains(3));
        assertFalse(bstSet.contains(5));

    }

    @Test
    void addToEmptySet() {
        assertTrue(bstSet.add(1));
    }

    @Test
    void addsNonRepeatingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.add(i));
        }
    }

    @Test
    void addRepeatingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.add(i));
            assertFalse(bstSet.add(i));
        }
    }

    @Test
    void removeFromEmpty() {
        assertFalse(bstSet.remove(1));
    }

    @Test
    void removeExistingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.remove(i));
        }
    }

    @Test
    void removeNonExistingElements() {
        final int N = 100;
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        for (int i = 0; i < N; i++) {
            assertFalse(bstSet.remove(i + N));
        }
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.contains(i));
        }
    }

    @Test
    void removeSizeTest() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertEquals(3, bstSet.size());
        bstSet.remove(1);
        assertEquals(2, bstSet.size());
        bstSet.remove(1);
        assertEquals(2, bstSet.size());
        bstSet.remove(2);
        assertEquals(1, bstSet.size());

    }


    @Test
    void descendingIterator() {
    }

    @Test
    void descendingSet() {
    }

    @Test
    void firstEmpty() {
        assertNull(bstSet.first());
    }

    @Test
    void first() {
        bstSet.add(1);
        bstSet.add(-1);
        bstSet.add(0);
        assertEquals(-1, bstSet.first());
    }

    @Test
    void firstRemove() {
        bstSet.add(1);
        bstSet.add(-1);
        bstSet.add(0);
        bstSet.remove(-1);
        assertEquals(0, bstSet.first());
    }

    @Test
    void lastEmpty() {
        assertNull(bstSet.last());
    }

    @Test
    void last() {
        bstSet.add(1);
        bstSet.add(-1);
        bstSet.add(0);
        assertEquals(1, bstSet.last());
    }

    @Test
    void lastRemove() {
        bstSet.add(1);
        bstSet.add(-1);
        bstSet.add(0);
        bstSet.remove(1);
        assertEquals(0, bstSet.last());
    }

    @Test
    void lower() {
    }

    @Test
    void floor() {
    }

    @Test
    void ceiling() {
    }

    @Test
    void higher() {
    }

    @Test
    void iterator() {
    }
}