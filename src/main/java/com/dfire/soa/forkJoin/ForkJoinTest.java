package com.dfire.soa.forkJoin;

import java.util.concurrent.*;

/**
 * @author gantang
 * @Date 2017/7/17
 */
public class ForkJoinTest extends RecursiveTask<Long> {

    // TODO: 2017/7/17  有返回值用RecursiveTask ，无返回值用RecursiveAction

    private long start, end;

    public ForkJoinTest(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= Integer.MAX_VALUE) {
            long val = start;
            for (long i = start; i < end; i++) {
                val += i / 2.5;
            }
            return val;
        } else {
            long middle = (start + end) / 2;
            ForkJoinTest a = new ForkJoinTest(start, middle);
            ForkJoinTest b = new ForkJoinTest(middle, end);
            a.fork();
            b.fork();
            return b.join() + a.join();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        ForkJoinTest forkJoinTest = new ForkJoinTest(0, Long.MAX_VALUE);
        long curr = System.currentTimeMillis();
        Long aLong = forkJoinPool.invoke(forkJoinTest);
        forkJoinPool.execute(forkJoinTest);
        Future future = forkJoinPool.submit(forkJoinTest);
        System.out.println(future.get(2000L, TimeUnit.MILLISECONDS));
        System.out.println(System.currentTimeMillis() - curr);
    }


}
