import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    void getArray() {
        assertEquals('a', characterSmartList.get(0));
        assertEquals('b', characterSmartList.get(1));
        assertEquals('c', characterSmartList.get(2));
    }

    @Test
    void setArray() {
        assertEquals('a', characterSmartList.set(0, 'd'));
        assertEquals('d', characterSmartList.get(0));
    }
}