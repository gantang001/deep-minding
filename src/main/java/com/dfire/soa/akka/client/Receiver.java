package com.dfire.soa.akka.client;

import akka.actor.UntypedActor;

import java.util.concurrent.ForkJoinPool;

/**
 * @author gantang
 * @Date 2017/7/17
 */
public class Receiver extends UntypedActor {

    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

    private ConsumeListener consumeListener;

    Receiver(ConsumeListener consumeListener) {
        this.consumeListener = consumeListener;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        forkJoinPool.execute(() -> consumeListener.handle());

    }
}
