import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.junit.Assert.*;

/**
 * Created by mtumilowicz on 2018-08-20.
 */
public class FindMinimumTest {

    @Test
    public void compute() {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();

        Random r = new Random();
        int[] arr = new int[10000];
        for (int i = 0; i < arr.length - 1; i++) {
            arr[i] = r.nextInt();
        }

        int min = Arrays.stream(arr).min().getAsInt() - 1;

        arr[arr.length - 1] = min;

        int result = commonPool.invoke(new FindMinimum(arr, 0, arr.length));

        assertEquals(min, result);
    }
}