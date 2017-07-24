//package com.dfire.soa.base;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author gantang
// * @Date 2017/5/26
// */
//public class ThreadLocalTest {
//
//    public static void main(String[] args) throws InterruptedException {
//        ThreadLocal threadLocal = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
//        System.out.println(Runtime.getRuntime().totalMemory());
//        System.out.println(Runtime.getRuntime().freeMemory());
//        byte[] buffer = new byte[2 * 1024 * 1024];
//        threadLocal.set(buffer);
//        System.out.println(Runtime.getRuntime().freeMemory());
//        System.gc();
//        System.out.println(threadLocal.get());
//        System.out.println(Runtime.getRuntime().freeMemory());
//    }
//}
