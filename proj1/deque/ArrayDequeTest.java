package deque;

import org.junit.Test;

public class ArrayDequeTest {
    @Test
    public void testGet() {
        ArrayDeque<Integer> integerArrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 7; i++) {
            integerArrayDeque.addFirst(i);
        }
        Integer i = integerArrayDeque.get(3);
        System.out.println(i);
    }
}
