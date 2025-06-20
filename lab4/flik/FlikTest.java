package flik;

import org.junit.Test;

public class FlikTest {
    @Test
    public void test() {
        Integer i = new Integer(128);
        Integer i1 = new Integer(128);

        System.out.println(i.equals(i1));
        Integer i2 = new Integer(127);
        Integer i3 = new Integer(127);
        System.out.println(i2.equals(i3));
    }
}
