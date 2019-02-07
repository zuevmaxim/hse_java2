package com.hse.java.treeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BSTSetTest {
    private BSTSet<Integer> bstSet;
    private final int N = 10;

    private static class Time {
        private final int hours;
        private final int minutes;
        private Time(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }
    }

    private BSTSet<Time> timeBSTSet;

    @BeforeEach
    void setUp() {
        bstSet = new BSTSet<>();
        timeBSTSet = new BSTSet<>((Time a, Time b) -> {
            if (a.hours == b.hours) {
                if (a.minutes == b.minutes) {
                    return 0;
                }
                return a.minutes < b.minutes ? -1 : 1;
            }
            return a.hours < b.hours ? -1 : 1;
        });
    }

    @Test
    void nonComparableTypeWithoutComparator() {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        BSTSet<Time> timeSetWithoutComparator = new BSTSet<>();
        timeSetWithoutComparator.add(new Time(1, 1));
        //noinspection ResultOfMethodCallIgnored
        assertThrows(ClassCastException.class, () -> timeSetWithoutComparator.contains(new Time(1, 1)));
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, bstSet.size());
    }

    @Test
    void sizeEmptyComparator() {
        assertEquals(0, timeBSTSet.size());
    }


    @Test
    void sizeNonRepeatingElements() {
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        assertEquals(N, bstSet.size());
    }

    @Test
    void sizeNonRepeatingElementsComparator() {
        for (int i = 0; i < N; i++) {
            timeBSTSet.add(new Time(N - i, i + 10));
        }
        assertEquals(N, timeBSTSet.size());
    }

    @Test
    void sizeRepeatingElements() {
        //noinspection OverwrittenKey
        bstSet.add(1);
        //noinspection OverwrittenKey
        bstSet.add(1);
        //noinspection OverwrittenKey
        bstSet.add(1);
        assertEquals(1, bstSet.size());
    }

    @Test
    void sizeRepeatingElementsComparator() {
        var time = new Time(1, 1);
        timeBSTSet.add(time);
        timeBSTSet.add(time);
        timeBSTSet.add(time);
        assertEquals(1, timeBSTSet.size());
    }

    @Test
    void containsEmptySet() {
        assertFalse(bstSet.contains(1));
    }

    @Test
    void containsNullElement() {
        //noinspection ResultOfMethodCallIgnored,ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.contains(null));
    }

    @Test
    void containsEmptySetComparator() {
        assertFalse(timeBSTSet.contains(new Time(0, 0)));
    }


    @Test
    void containsExistingElements() {
        for (int i = 0; i < N; i++) {
            bstSet.add(N -1 - i);
        }
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.contains(i));
        }
    }

    @Test
    void containsExistingElementsComparator() {
        for (int i = 0; i < N; i++) {
            timeBSTSet.add(new Time(0, N - 1 - i));
        }
        for (int i = 0; i < N; i++) {
            assertTrue(timeBSTSet.contains(new Time(0, i)));
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
    void containsNonExistingElementsComparator() {
        for (int i = 0; i < 5; i++) {
            timeBSTSet.add(new Time(i, 2 * i));
        }
        assertFalse(timeBSTSet.contains(new Time(1, 1)));
        assertFalse(timeBSTSet.contains(new Time(3, 3)));
        assertFalse(timeBSTSet.contains(new Time(5, 5)));

    }

    @Test
    void addToEmptySet() {
        assertTrue(bstSet.add(1));
    }

    @Test
    void addNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.add(null));
    }

    @Test
    void addToEmptySetComparator() {
        assertTrue(timeBSTSet.add(new Time(1, 1)));
    }

    @Test
    void addsNonRepeatingElements() {
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.add(i));
        }
    }

    @Test
    void addsNonRepeatingElementsComparator() {
        for (int i = 0; i < N; i++) {
            assertTrue(timeBSTSet.add(new Time(i, 0)));
        }
    }

    @Test
    void addRepeatingElements() {
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.add(i));
            assertFalse(bstSet.add(i));
        }
    }

    @Test
    void addElementsInDisorder() {
        assertTrue(bstSet.add(2));
        assertTrue(bstSet.add(1));
        assertTrue(bstSet.add(5));
        assertTrue(bstSet.add(4));
        assertTrue(bstSet.add(6));
        assertTrue(bstSet.add(3));
    }

    @Test
    void addRepeatingElementsComparator() {
        for (int i = 0; i < N; i++) {
            assertTrue(timeBSTSet.add(new Time(i, i)));
            assertFalse(timeBSTSet.add(new Time(i, i)));
        }
    }

    @Test
    void removeFromEmpty() {
        assertFalse(bstSet.remove(1));
    }

    @Test
    void removeNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.remove(null));
    }

    @Test
    void removeFromEmptyComparator() {
        assertFalse(timeBSTSet.remove(new Time(1, 1)));
    }

    @Test
    void removeExistingElements() {
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.remove(i));
        }
    }

    @Test
    void removeExistingElementsInDisorder() {
        for (int i = 0; i < 5; i++) {
            bstSet.add(i);
        }
        assertTrue(bstSet.remove(3));
        assertTrue(bstSet.remove(4));
        assertTrue(bstSet.remove(1));
        assertTrue(bstSet.remove(0));
        assertTrue(bstSet.remove(2));
    }

    @Test
    void removeExistingElementsComparator() {
        for (int i = 0; i < N; i++) {
            timeBSTSet.add(new Time(N - 1 - i, i));
        }
        for (int i = 0; i < N; i++) {
            assertTrue(timeBSTSet.remove(new Time(N - 1 -i, i)));
        }
    }

    @Test
    void removeNonExistingElements() {
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
    void descendingEmptySet() {
        var descendingSet = bstSet.descendingSet();
        assertEquals(0, descendingSet.size());
    }

    @Test
    void descendingSet() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        var descendingSet = bstSet.descendingSet();
        assertEquals(3, descendingSet.first());
        assertEquals(1, descendingSet.last());
    }

    @Test
    void descendingSetConnection() {
        var descendingSet = bstSet.descendingSet();
        for (int i = 0; i < N; i++) {
            descendingSet.add(2 * i);
        }
        descendingSet.add(N - 1);
        for (int i = 0; i < N; i++) {
            assertTrue(bstSet.contains(2 * i));
            assertTrue(descendingSet.contains(2 * i));
        }
        assertTrue(bstSet.contains(N - 1));
    }

    @Test
    void descendingDescendingSet() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        var descendingDescendingSet = bstSet.descendingSet().descendingSet();
        assertEquals(1, descendingDescendingSet.first());
        assertEquals(3, descendingDescendingSet.last());
    }

    @Test
    void descendingDescendingSetConnection() {
        var descendingDescendingSet = bstSet.descendingSet().descendingSet();
        descendingDescendingSet.add(1);
        assertTrue(bstSet.contains(1));
        assertSame(descendingDescendingSet, bstSet);
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
    void lowerEmpty() {
        assertNull(bstSet.lower(1));
    }

    @Test
    void lowerNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.lower(null));
    }

    @Test
    void lowerNotExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertNull(bstSet.lower(1));
    }

    @Test
    void lowerExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertEquals(1, bstSet.lower(2));
        assertEquals(2, bstSet.lower(3));
        assertEquals(3, bstSet.lower(4));
    }

    @Test
    void floorEmpty() {
        assertNull(bstSet.floor(1));
    }

    @Test
    void floorNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.floor(null));
    }

    @Test
    void floorNotExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertNull(bstSet.higher(3));
    }

    @Test
    void floorExists() {
        bstSet.add(1);
        bstSet.add(3);
        bstSet.add(5);
        assertEquals(1, bstSet.floor(1));
        assertEquals(1, bstSet.floor(2));
        assertEquals(3, bstSet.floor(3));
        assertEquals(3, bstSet.floor(4));
        assertEquals(5, bstSet.floor(5));
        assertEquals(5, bstSet.floor(6));
    }

    @Test
    void ceilingEmpty() {
        assertNull(bstSet.ceiling(1));
    }

    @Test
    void ceilingNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.ceiling(null));
    }

    @Test
    void ceilingNotExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertNull(bstSet.ceiling(4));
    }

    @Test
    void ceilingExists() {
        bstSet.add(1);
        bstSet.add(3);
        bstSet.add(5);
        assertEquals(1, bstSet.ceiling(0));
        assertEquals(1, bstSet.ceiling(1));
        assertEquals(3, bstSet.ceiling(2));
        assertEquals(3, bstSet.ceiling(3));
        assertEquals(5, bstSet.ceiling(4));
        assertEquals(5, bstSet.ceiling(5));
    }

    @Test
    void higherEmpty() {
        assertNull(bstSet.higher(1));
    }

    @Test
    void higherNullElement() {
        //noinspection ConstantConditions
        assertThrows(IllegalArgumentException.class, () -> bstSet.higher(null));
    }

    @Test
    void higherNotExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertNull(bstSet.higher(3));
    }

    @Test
    void higherExists() {
        bstSet.add(1);
        bstSet.add(2);
        bstSet.add(3);
        assertEquals(1, bstSet.higher(0));
        assertEquals(2, bstSet.higher(1));
        assertEquals(3, bstSet.higher(2));
    }

    @Test
    void iteratorEmpty() {
        var it = bstSet.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    void iterator() {
        for (int i = 0; i < N; ++i) {
            bstSet.add(i);
        }
        int i = 0;
        for (var it = bstSet.iterator(); it.hasNext();i++) {
            assertEquals(i, it.next());
        }
    }

    @Test
    void iteratorInvalidation() {
        var it = bstSet.iterator();
        bstSet.add(4);
        //noinspection ResultOfMethodCallIgnored
        assertThrows(IllegalStateException.class, it::hasNext);
    }

    @Test
    void descendingIteratorEmpty() {
        var it = bstSet.descendingIterator();
        assertFalse(it.hasNext());
    }

    @Test
    void descendingIterator() {
        for (int i = 0; i < N; ++i) {
            bstSet.add(i);
        }
        int i = N - 1;
        for (var it = bstSet.descendingIterator(); it.hasNext(); i--) {
            assertEquals(i, it.next());
        }
    }

    @Test
    void descendingIteratorInvalidation() {
        bstSet.add(3);
        var it = bstSet.descendingIterator();
        bstSet.add(4);
        //noinspection ResultOfMethodCallIgnored
        assertThrows(IllegalStateException.class, it::hasNext);
    }

    @Test
    void differentTypeIterators() {
        var descendingSet = bstSet.descendingSet();
        for (int i = 0; i < N; i++) {
            bstSet.add(i);
        }

        var it00 = bstSet.iterator();
        var it01 = bstSet.descendingIterator();
        var it10 = descendingSet.iterator();
        var it11 = descendingSet.descendingIterator();

        for (int i = 0; i < 5; i++) {
            assertEquals(i, it00.next());
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(N - 1 -i, it01.next());
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(N - 1 -i, it10.next());
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(i, it11.next());
        }
    }
}