import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.assertEquals;

/**
 * Created by mtumilowicz on 2018-08-20.
 */
public class FindMinimumTest {

    private static final int[] arr = new int[100_000];
    private static final int min = -1_000_005;
    
    @BeforeClass
    public static void before() {
        Random r = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = r.nextInt(2_000_000) - 1_000_000;
        }
        
        arr[50_000] = min;
    }
    
    @Test
    public void computeUsingForkJoin() {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        
        int result = commonPool.invoke(new FindMinimum(arr, 0, arr.length));

        assertEquals(min, result);
    }

    @Test
    public void computeOrdinary() {
        assertEquals(min, Arrays.stream(arr).min().orElseThrow(RuntimeException::new));
    }
}