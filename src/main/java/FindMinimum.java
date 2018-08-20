import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

import static java.util.Objects.nonNull;

/**
 * Created by mtumilowicz on 2018-08-20.
 */
class FindMinimum extends RecursiveTask<Integer> {
    private final int left;
    private final int right;
    private final int[] arr;

    private static final int THRESHOLD = 20;

    FindMinimum(int[] arr, int left, int right) {
        Preconditions.checkArgument(nonNull(arr));
        Preconditions.checkArgument(left >= 0);
        Preconditions.checkArgument(right <= arr.length);
        this.arr = arr;
        this.left = left;
        this.right = right;
    }

    @Override
    protected Integer compute() {
        if (left - right <= THRESHOLD) {
            return calculateMin(arr);
        } else {
            int middle = (left + right) / 2;
            FindMinimum findMinimum = new FindMinimum(arr, left, middle);
            findMinimum.fork();

            return Math.min(new FindMinimum(arr, middle, right).compute(), findMinimum.join());
        }
    }

    private Integer calculateMin(int[] arr) {
        return Arrays.stream(arr).min().orElseThrow(() -> new RuntimeException("Minimum doesnt exist in the array: " + Arrays.toString(arr)));
    }
}
