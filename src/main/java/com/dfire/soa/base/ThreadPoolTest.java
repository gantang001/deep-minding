//package com.dfire.soa.base;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author gantang
// * @Date 2017/5/27
// */
//public class ThreadPoolTest {
//    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2500));
//
//    public static void main(String[] args) throws InterruptedException {
//
//        long time = System.currentTimeMillis();
//        executor.execute(() -> {
//            final AtomicInteger atomicInteger = new AtomicInteger();
//            do {
//                atomicInteger.getAndIncrement();
//            } while (atomicInteger.getAndIncrement() < Integer.MAX_VALUE / 8);
//            System.out.println(111);
//        });
//
//        for (int i = 0; i < 2000; i++) {
//            executor.execute(() -> {
//                final AtomicInteger atomicInteger = new AtomicInteger();
//                do {
//                    atomicInteger.getAndIncrement();
//                } while (atomicInteger.getAndIncrement() < Integer.MAX_VALUE / 8);
//                System.out.println(111);
//            });
//        }
//        executor.shutdown();
//        BlockingQueue blockingQueue=executor.getQueue();
//        blockingQueue.clear();
//        boolean status = executor.awaitTermination(30000, TimeUnit.MILLISECONDS);
//        System.out.println(System.currentTimeMillis() - time);
//        System.out.println(status);
//        System.exit(1);
//    }
//}
