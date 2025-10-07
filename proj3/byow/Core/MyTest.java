package byow.Core;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MyTest {
    @Test
    public void testExtractSeed() {
        Engine engine = new Engine();
        assertEquals(engine.extractSeed("N1234SSSSS"), 1234);
    }
    @Test
    public void randomUtilsEqualSeed() {
        Random random = new Random(111);
        System.out.println(RandomUtils.poisson(random, 2));
        System.out.println(RandomUtils.poisson(random, 2));
        System.out.println(RandomUtils.poisson(random, 2));

    }

}
