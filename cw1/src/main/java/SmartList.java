import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmartList<E>  extends AbstractList<E> implements List<E> {

    private int size;
    private Object data;

    public SmartList() { }

    public SmartList(Collection<? extends E> collection) {
        size = collection.size();
        if (size == 0) {
            return;
        }
        if (size == 1) {
            data = collection.iterator().next();
            return;
        }
        if (size <= 5) {
            var array = new Object[5];
            int i = 0;
            for (var element : collection) {
                array[i] = element;
                i++;
            }
            data = array;
            return;
        }
        data = new ArrayList<>(collection);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            return (E) data;
        }
        if (size <= 5) {
            return (E) (((Object[]) data)[index]);
        }
        return (E) ((ArrayList<Object>) data).get(index);
    }


}
