package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> c;
    public MaxArrayDeque(Comparator<T> c) {
        this.c = c;
    }
    public T max() {
        if (size() == 0) {
            return null;
        }
        T maxItem = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), maxItem) > 0) {
                maxItem = get(i);
            }
        }
        return maxItem;
    }
    public T max(Comparator<T> comparator) {
        MaxArrayDeque<T> ts = new MaxArrayDeque<>(comparator);
        for (int i = 0; i < size(); i++) {
            ts.addLast(get(i));
        }
        return ts.max();
    }
}
