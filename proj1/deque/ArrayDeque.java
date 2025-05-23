package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    private  T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private final int init_capacity = 8;
    ArrayDeque() {
        items = (T[]) new Object[init_capacity];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }
    private void resize(int newCapacity) {
        T[] newItems = (T[]) new Object[newCapacity];
        int oldIndex = addOne(nextFirst);

        for (int i = 0; i < size; i++) {
            newItems[i] = items[oldIndex];
            oldIndex = addOne(oldIndex);
        }

        items = newItems;
        nextFirst = newCapacity - 1;
        nextLast = size;
    }
    private int minusOne(int pos) {
        return (pos - 1 + items.length) % items.length;
    }
    private int addOne(int pos) {
        return (pos + 1 ) % items.length;
    }
    @Override
    public void addFirst(Object item) {
        if(size == items.length) {
            resize(items.length * 2);
        }
        items[nextFirst] = (T)item;
        nextFirst = minusOne(nextFirst);
        size++;
    }
    @Override
    public void addLast(Object item) {
        if(size == items.length) {
            resize(items.length * 2);
        }
        items[nextLast] = (T)item;
        nextLast = addOne(nextLast);
        size++;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for(int i = 0; i < items.length; i++) {
            if(items[i] != null) {
                System.out.println(items[i].toString());
            }
            else {
                System.out.println("null");
            }
        }
    }

    @Override
    public T removeFirst() {
        if(size == 0) {
            return null;
        }
        int tmp_index = nextFirst;
        tmp_index = addOne(tmp_index);
        T tmp = items[tmp_index];
        nextFirst = addOne(nextFirst);
        size--;
        if (items.length >= 16 && size < items.length / 4) {
            resize(Math.max(8, items.length / 2));
        }
        return tmp;
    }

    @Override
    public T removeLast() {
        if( size == 0 ) {
            return null;
        }
        int tmp_index = nextLast;
        tmp_index = minusOne(tmp_index);
        T tmp = items[tmp_index];
        nextLast = minusOne(nextLast);
        size--;
        if (items.length >= 16 && size < items.length / 4) {
            resize(Math.max(8, items.length / 2));
        }
        return tmp;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        int actualIndex = (nextFirst + 1 + index) % items.length;
        return items[actualIndex];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int pos = addOne(nextFirst);
        private int count = 0;

        @Override
        public boolean hasNext() {
            return count < size;
        }

        @Override
        public T next() {
            T item = items[pos];
            pos = addOne(pos);
            count++;
            return item;
        }
    }
}
