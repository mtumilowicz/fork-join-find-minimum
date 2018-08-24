[![Build Status](https://travis-ci.com/mtumilowicz/fork-join-find-minimum.svg?branch=master)](https://travis-ci.com/mtumilowicz/fork-join-find-minimum)

# fork-join-find-minimum
The fork/join framework is an implementation of the ExecutorService interface that helps 
you take advantage of multiple processors. It is designed for work that can be broken 
into smaller pieces recursively. The goal is to use all the available processing power 
to enhance the performance of your application.

As with any ExecutorService implementation, the fork/join framework distributes tasks to 
worker threads in a thread pool. The fork/join framework is distinct because it uses a 
work-stealing algorithm. Worker threads that run out of things to do can steal tasks from 
other threads that are still busy.

_Reference_: https://docs.oracle.com/javase/tutorial/essential/concurrency/forkjoin.html  
_Reference_: https://www.igvita.com/2012/02/29/work-stealing-and-recursive-partitioning-with-fork-join/  

# preface
One of the key challenges in parallelizing any type of workload is the partitioning 
step: ideally we want to partition the work such that every piece will take the exact 
same amount of time. In reality, we often have to guess at what the partition should be, 
which means that some parts of the problem will take longer, either because of the inefficient 
partitioning scheme, or due to some other, unanticipated reasons (e.g. external service, 
slow disk access, etc).

This is where work-stealing comes in. If some of the CPU cores finish their jobs early, then 
we want them to help to finish the problem. However, now we have to be careful: trying to 
"steal" work from another worker will require synchronization, which will slowdown the 
processing. Hence, we want work-stealing, but with minimal synchronization.

# description
![](https://www.igvita.com/posts/12/xwork-stealing.png.pagespeed.ic.6YNpZvb8Ww.png)

Given a problem, we divide the problem into N large pieces, and hand each piece to one of 
the workers (2 in the diagram above). Each worker then recursively subdivides the first 
problem at the head of the deque and appends the split tasks to the head of the same deque. 
After a few iterations we will end up with some number of smaller tasks at the front of the 
deque, and a few larger and yet to be partitioned tasks on end.

Imagine the second worker has finished all of its work, while the first worker is busy. 
To minimize synchronization the second worker grabs a job from the end of the deque 
(hence the reason for efficient head and tail access). By doing so, it will get the largest 
available block of work, allowing it to minimize the number of times it has to interact with 
the other worker (aka, minimize synchronization).

**Fork**:  Split larger task into smaller tasks.  
**Join**: Get result from same level subtasks.

# manual
```
if (my portion of the work is small enough)
  do the work directly
else
  split my work into two pieces
  invoke the two pieces and wait for the results
```

# project description
We find the minimum in the table using fork-join.

1. define THRESHOLD if satisfied, we compute minimum in a straight way:
    ```
    private static final int THRESHOLD = 20;
    
    if (left - right <= THRESHOLD) {
        return calculateMin(arr);
    } else {
        // fork / join
    }
    
    private Integer calculateMin(int[] arr) {
        return Arrays.stream(arr).min().orElseThrow(() -> new RuntimeException("Minimum doesnt exist in the array: " + Arrays.toString(arr)));
    }    
    ```

1. divide task
    ```
    if (left - right <= THRESHOLD) {
        // 
    } else {
        int middle = (left + right) / 2;
        FindMinimum findMinimum = new FindMinimum(arr, left, middle);
        findMinimum.fork();
    
        return Math.min(new FindMinimum(arr, middle, right).compute(), findMinimum.join());
    }
    ```
    
# tests
1. we randomly generate array with range `[-1_000_000; 1_000_000]`
    ```
    Random r = new Random();
    for (int i = 0; i < arr.length; i++) {
        arr[i] = r.nextInt(2_000_000) - 1_000_000;
    }
    ```
1. set the minimum `-1_000_005` somewhere in the table
    ```
    arr[50_000] = min;
    ```
1. run tests:
    * **computeUsingForkJoin** - on my CPU - around 15 ms
    * **computeOrdinary** - on my CPU - around 115 ms