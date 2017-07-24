//package com.dfire.soa.proxy;
//
//import org.apache.commons.lang3.time.StopWatch;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Proxy;
//import java.util.UUID;
//
///**
// * @author gantang
// * @Date 2017/4/17
// */
//public class TestProxy {
//    private static final StopWatch stopWatch = new StopWatch();
//
//    public static void main(String[] args) {
//        final ClientBase client = new ClientImpl();
//        final String constant = UUID.randomUUID().toString().replace("-", "");
//        InvocationHandler invocationHandler = new JdkInvocationHandler(client);
//        final ClientBase jdkInvokerProxy = (ClientBase) Proxy.newProxyInstance(invocationHandler.getClass().getClassLoader(),
//                client.getClass().getInterfaces(), invocationHandler);
//        final ClientBase cglibInvokerProxy = CglibInvocationHandler.newProxyInstance(ClientImpl.class);
//        int num = 1 << 25;
//        invokeClosureAndTraceTime(() -> client.send(constant), num);
//        invokeClosureAndTraceTime(() -> jdkInvokerProxy.send(constant), num);
//        invokeClosureAndTraceTime(() -> cglibInvokerProxy.send(constant), num);
//    }
//
//
//    private static void invokeClosureAndTraceTime(Closure closure, int num) {
//        stopWatch.reset();
//        stopWatch.start();
//        for (int i = 0; i < num; i++) {
//            closure.loop();
//        }
//        stopWatch.stop();
//        System.out.println(stopWatch.getTime());
//    }
//
//    interface Closure {
//        void loop();
//    }
//}