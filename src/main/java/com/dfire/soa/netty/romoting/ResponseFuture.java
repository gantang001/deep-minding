package com.dfire.soa.netty.romoting;

import com.dfire.soa.netty.romoting.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author gantang
 * @Date 2017/7/18
 */
public class ResponseFuture {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    Response response;

    long timeOutMills;

    public ResponseFuture(long timeOutMills) {
        this.timeOutMills = timeOutMills;
    }

    public Response waitResponse() throws InterruptedException {
        this.countDownLatch.await(timeOutMills, TimeUnit.MILLISECONDS);
        return response;
    }
}
