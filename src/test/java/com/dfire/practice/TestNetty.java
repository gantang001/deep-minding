package com.dfire.practice;

import com.alibaba.rocketmq.remoting.exception.RemotingConnectException;
import com.dfire.soa.netty.client.NettyClient;
import com.dfire.soa.netty.romoting.Cmd;
import com.dfire.soa.netty.romoting.RemotingCode;

import java.util.UUID;

/**
 * @author gantang
 * @Date 2017/7/22
 */
public class TestNetty {
    public static void main(String[] args) throws RemotingConnectException, InterruptedException {
        NettyClient client = new NettyClient();
        client.start();
        Cmd cmd = new Cmd();
        cmd.setCode(RemotingCode.UPLOAD);
        cmd.setBody(UUID.randomUUID().toString().getBytes());
        client.send("localhost:10085", cmd, 3000L);
    }
}
