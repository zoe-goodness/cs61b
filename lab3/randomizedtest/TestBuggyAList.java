package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> integerAListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> integerBuggyAList = new BuggyAList<>();
        integerBuggyAList.addLast(4);
        integerAListNoResizing.addLast(4);
        integerBuggyAList.addLast(5);
        integerAListNoResizing.addLast(5);
        integerBuggyAList.addLast(6);
        integerAListNoResizing.addLast(6);
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
        assertEquals(integerBuggyAList.removeLast(), integerAListNoResizing.removeLast());
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }
    @Test
    public void moreRandomizedTest() {
        AListNoResizing<Integer> aListNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> integerBuggyAList = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                aListNoResizing.addLast(randVal);
                integerBuggyAList.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                assertEquals(aListNoResizing.size(), integerBuggyAList.size());
            } else if (operationNumber == 2) {
                // getLast
                if (aListNoResizing.size() > 0 && integerBuggyAList.size() > 0) {
                    assertEquals(aListNoResizing.getLast(), integerBuggyAList.getLast());
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (aListNoResizing.size() > 0 && integerBuggyAList.size() > 0) {
                    assertEquals(aListNoResizing.removeLast(), integerBuggyAList.removeLast());
                }
            }
        }
    }
}
