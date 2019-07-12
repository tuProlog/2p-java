package alice.util;

import java.util.Iterator;
import java.util.Objects;

public class ArrayIterator<T> implements Iterator<T> {

    private final T[] array;
    private int i = 0;

    public ArrayIterator(final T[] array) {
        this.array = Objects.requireNonNull(array);
    }


    @Override
    public boolean hasNext() {
        return i < array.length;
    }

    @Override
    public T next() {
        return array[i++];
    }
}
