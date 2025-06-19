package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.introcs.StdRandom;

public class TestArrayDequeEC {
    @Test
    public void myTest() {
        StudentArrayDeque<Integer> studentArrayDeque= new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> correctArrayDeque = new ArrayDequeSolution<>();
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                studentArrayDeque.addLast(randVal);
                correctArrayDeque.addLast(randVal);
                message.append("addLast(" + randVal + ")\n");
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                studentArrayDeque.addFirst(randVal);
                correctArrayDeque.addFirst(randVal);
                message.append("addFirst(" + randVal + ")\n");
            } else if (operationNumber == 2) {
                // removeFirst
                if (studentArrayDeque.size() == 0 || correctArrayDeque.size() == 0) {
                    continue;
                }
                Integer i1 = studentArrayDeque.removeFirst();
                Integer i2 = correctArrayDeque.removeFirst();
                message.append("removeFirst()\n");
                assertEquals(message.toString(), i1, i2);
            } else if (operationNumber == 3) {
                // removeLast
                if (studentArrayDeque.size() == 0 || correctArrayDeque.size() == 0) {
                    continue;
                }
                Integer i1 = studentArrayDeque.removeLast();
                Integer i2 = correctArrayDeque.removeLast();
                message.append("removeLast()\n");
                assertEquals(message.toString(), i1, i2);
            }
        }
    }
}
