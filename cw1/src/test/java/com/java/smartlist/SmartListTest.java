package com.java.smartlist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SmartListTest {
    private SmartList<Integer> integerSmartList;
    private SmartList<Character> characterSmartList;

    @BeforeEach
    void init() {
        integerSmartList = new SmartList<>();

        ArrayList<Character> arrayList = new ArrayList<>();
        arrayList.add('a');
        arrayList.add('b');
        arrayList.add('c');

        characterSmartList = new SmartList<>(arrayList);
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, integerSmartList.size());
    }

    @Test
    void size() {
        assertEquals(3, characterSmartList.size());
    }

    @Test
    void getEmptyException() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> integerSmartList.get(0));
    }

    @Test
    void getOne() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);

        integerSmartList = new SmartList<>(arrayList);
        assertEquals(1, integerSmartList.get(0));
    }

    @Test
    void getSmallArray() {
        assertEquals('a', characterSmartList.get(0));
        assertEquals('b', characterSmartList.get(1));
        assertEquals('c', characterSmartList.get(2));
    }

    @Test
    void getBigArray() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(i);
        }

        integerSmartList = new SmartList<>(arrayList);
        for (int i = 0; i < 10; i++) {
            assertEquals(i, integerSmartList.get(i));
        }
    }

    @Test
    void setOne() {
        integerSmartList.add(0, 1);
        integerSmartList.set(0, 5);
        assertEquals(5, integerSmartList.get(0));
    }

    @Test
    void setSmallArray() {
        assertEquals('a', characterSmartList.set(0, 'd'));
        assertEquals('d', characterSmartList.get(0));
    }

    @Test
    void setBigArray() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            arrayList.add(i);
        }

        integerSmartList = new SmartList<>(arrayList);
        for (int i = 0; i < 10; i++) {
            assertEquals(i, integerSmartList.set(i, i + 10));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i + 10, integerSmartList.get(i));
        }
    }

    @Test
    void addOne() {
        integerSmartList.add(0, 1);
        assertEquals(1, integerSmartList.size());
        assertEquals(1, integerSmartList.get(0));
    }

    @Test
    void addTwo() {
        integerSmartList.add(0, 5);
        integerSmartList.add(1, 6);
        assertEquals(2, integerSmartList.size());
        assertEquals(5, integerSmartList.get(0));
        assertEquals(6, integerSmartList.get(1));
    }

    @Test
    void addSmallArray() {
        integerSmartList.add(0, 5);
        integerSmartList.add(1, 6);
        integerSmartList.add(1, 7);
        assertEquals(3, integerSmartList.size());
        assertEquals(5, integerSmartList.get(0));
        assertEquals(7, integerSmartList.get(1));
        assertEquals(6, integerSmartList.get(2));
    }

    @Test
    void addBigArray() {
        integerSmartList.add(0, 5);
        integerSmartList.add(1, 7);
        integerSmartList.add(1, 6);
        integerSmartList.add(0, 4);
        integerSmartList.add(0, 3);
        integerSmartList.add(0, 2);
        integerSmartList.add(0, 1);
        assertEquals(7, integerSmartList.size());
        for (int i = 0; i < integerSmartList.size(); i++) {
            assertEquals(i + 1, integerSmartList.get(i));
        }
    }

    @Test
    void removeEmptyException() {
        assertThrows(IndexOutOfBoundsException.class,
                () -> integerSmartList.remove(0));
    }

    @Test
    void removeBigArray() {
        integerSmartList.add(0, 5);
        integerSmartList.add(1, 7);
        integerSmartList.add(1, 6);
        integerSmartList.add(0, 4);
        integerSmartList.add(0, 3);
        integerSmartList.add(0, 2);
        integerSmartList.add(0, 1);

        for (int i = 0; i < 7; i++) {
            assertEquals(i + 1, integerSmartList.remove(0));
        }
        assertEquals(0, integerSmartList.size());
    }

    @Test
    public void testSimple() {
        List<Integer> list = newList();

        assertEquals(Collections.<Integer>emptyList(), list);

        list.add(1);
        assertEquals(Collections.singletonList(1), list);

        list.add(2);
        assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void testGetSet() {
        List<Object> list = newList();

        list.add(1);

        assertEquals(1, list.get(0));
        assertEquals(1, list.set(0, 2));
        assertEquals(2, list.get(0));
        assertEquals(2, list.set(0, 1));

        list.add(2);

        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));

        assertEquals(1, list.set(0, 2));

        assertEquals(Arrays.asList(2, 2), list);
    }

    @Test
    public void testRemove() throws Exception {
        List<Object> list = newList();

        list.add(1);
        list.remove(0);
        assertEquals(Collections.emptyList(), list);

        list.add(2);
        list.remove((Object) 2);
        assertEquals(Collections.emptyList(), list);

        list.add(1);
        list.add(2);
        assertEquals(Arrays.asList(1, 2), list);

        list.remove(0);
        assertEquals(Collections.singletonList(2), list);

        list.remove(0);
        assertEquals(Collections.emptyList(), list);
    }

    @Test
    public void testIteratorRemove() throws Exception {
        List<Object> list = newList();
        assertFalse(list.iterator().hasNext());

        list.add(1);

        Iterator<Object> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        iterator.remove();
        assertFalse(iterator.hasNext());
        assertEquals(Collections.emptyList(), list);

        list.addAll(Arrays.asList(1, 2));

        iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next());

        iterator.remove();
        assertTrue(iterator.hasNext());
        assertEquals(Collections.singletonList(2), list);
        assertEquals(2, iterator.next());

        iterator.remove();
        assertFalse(iterator.hasNext());
        assertEquals(Collections.emptyList(), list);
    }


    @Test
    public void testCollectionConstructor() throws Exception {
        assertEquals(Collections.emptyList(), newList(Collections.emptyList()));
        assertEquals(
                Collections.singletonList(1),
                newList(Collections.singletonList(1)));

        assertEquals(
                Arrays.asList(1, 2),
                newList(Arrays.asList(1, 2)));
    }

    @Test
    public void testAddManyElementsThenRemove() throws Exception {
        List<Object> list = newList();
        for (int i = 0; i < 7; i++) {
            list.add(i + 1);
        }

        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), list);

        for (int i = 0; i < 7; i++) {
            list.remove(list.size() - 1);
            assertEquals(6 - i, list.size());
        }

        assertEquals(Collections.emptyList(), list);
    }

    private static <T> List<T> newList() {
        try {
            return (List<T>) getListClass().getConstructor().newInstance();
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<T> newList(Collection<T> collection) {
        try {
            return (List<T>) getListClass().getConstructor(Collection.class).newInstance(collection);
        } catch (InstantiationException | ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getListClass() throws ClassNotFoundException {
        return Class.forName("com.java.smartlist.SmartList");
    }

}