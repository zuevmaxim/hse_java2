import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Smart list provides List interface.
 * Optimised gor small number of elements.
 * @param <E>
 */
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
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 0) {
            data = element;
            size++;
            return;
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
            return;
        }
        if (size == 5) {
            size++;
            var tmp = data;
            data = new ArrayList<>();
            for (int i = 0; i < 5; ++i) {
                ((ArrayList) data).add(((Object[])tmp)[i]);
            }
            ((ArrayList) data).add(index, element);
            return;
        }
        size++;
        ((ArrayList) data).add(index, element);
    }

    @Override
    public E remove(int index) throws IndexOutOfBoundsException {
        if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            var tmp = (E) data;
            data = null;
            size = 0;
            return tmp;
        }
        if (size == 2) {
            size--;
            var tmp0 = (E) ((Object[]) data)[0];
            var tmp1 = (E) ((Object[]) data)[1];
            if (index == 0) {
                data = tmp1;
                return tmp0;
            }
            data = tmp0;
            return tmp1;
        }
        if (size <= 5) {
            var tmpData = (Object[]) data;
            var tmp = (E) tmpData[index];
            for (int i = index; i < size - 1; ++i) {
                tmpData[i] = tmpData[i + 1];
            }
            tmpData[size - 1] = null;
            size--;
            return tmp;
        }
        if (size == 6) {
            var tmpData = (ArrayList) data;
            var tmp = (E) ((ArrayList) data).remove(index);
            size--;
            data = new Object[5];
            int i = 0;
            for (var element : tmpData) {
                ((Object[]) data)[i] = element;
                i++;
            }
            return tmp;
        }
        size--;
        return (E) ((ArrayList) data).remove(index);
    }

}
