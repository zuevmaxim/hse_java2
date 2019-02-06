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

    @Override
    public E set(int index, E element) throws IndexOutOfBoundsException {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            E tmp = (E) data;
            data = element;
            return tmp;
        }
        if (size <= 5) {
            E tmp = (E) (((Object[]) data)[index]);
            ((Object[]) data)[index] = element;
            return tmp;
        }
        return (E) ((ArrayList<Object>) data).set(index, element);
    }

    @Override
    public void add(int index, E element) throws IndexOutOfBoundsException {
        if (size == 0 && index == 0) {
            data = element;
            size++;
            return;
        }
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            size++;
            var tmp = data;
            data = new Object[5];
            if (index == 0) {
                ((Object[]) data)[0] = element;
                ((Object[]) data)[1] = tmp;
            } else {
                ((Object[]) data)[0] = tmp;
                ((Object[]) data)[1] = element;
            }
            return;
        }
        if (size < 5) {
            var tmpData = (Object[]) data;
            for (int i = size - 1; i >= index; i--) {
                tmpData[i + 1] = tmpData[i];
            }
            size++;
            tmpData[index] = element;
        }
        if (size == 5) {
            size++;
            var tmp = data;
            data = new ArrayList<>();
            for (int i = 0; i < 5; ++i) {
                ((ArrayList) data).add(((Object[])tmp)[i]);
            }
            ((ArrayList) data).add(index, element);
        }
        size++;
        ((ArrayList) data).add(index, element);
    }

}
