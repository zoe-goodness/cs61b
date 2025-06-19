package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private int nextFirst;
    private int nextLast;
    private T[] array;
    private final double factor = 0.25;
    public ArrayDeque() {
        size = 0;
        nextFirst = 0;
        nextLast = 1;
        array = (T[]) new Object[8];
    }
    @Override
    public void addFirst(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        array[nextFirst] = item;
        nextFirst = parseIndex(nextFirst - 1);
        size++;
    }
    private void resize(int capacity) {
        T[] newArray = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = get(i);
        }
        nextFirst = newArray.length - 1;
        nextLast = size;
        array = newArray;
    }
    @Override
    public void addLast(T item) {
        if (size == array.length) {
            resize(size * 2);
        }
        array[nextLast] = item;
        nextLast = parseIndex(nextLast + 1);
        size++;
    }



    @Override
    public int size() {
        return size;
    }
    private int parseIndex(int index) {
        if (index == array.length) {
            return 0;
        } else if (index == -1) {
            return array.length - 1;
        } else {
            return index;
        }
    }
    @Override
    public void printDeque() {
        int tempIndex = parseIndex(nextFirst + 1);
        for (int i = 0; i < size; i++) {
            System.out.print(array[tempIndex] + " ");
            tempIndex = parseIndex(tempIndex + 1);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size < factor * array.length) {
            resize(array.length / 2);
        }
        T item = array[parseIndex(nextFirst + 1)];
        nextFirst = parseIndex(nextFirst + 1);
        size--;
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size < factor * array.length) {
            resize(array.length / 2);
        }
        T item = array[parseIndex(nextLast - 1)];
        nextLast = parseIndex(nextLast - 1);
        size--;
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return array[(nextFirst + index + 1) % array.length];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;
        ArrayDequeIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }

    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
//        if (o.getClass() != this.getClass()) {
//            return false;
//        }
        Deque deque = (Deque) o;
        if (deque.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!get(i).equals(deque.get(i))) {
                return false;
            }
        }
        return true;

    }
}
