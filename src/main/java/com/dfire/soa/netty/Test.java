package com.dfire.soa.netty;

import com.dfire.soa.netty.client.NettyClient;
import com.dfire.soa.netty.romoting.Cmd;
import com.dfire.soa.netty.romoting.RemotingCode;
import com.dfire.soa.netty.server.NettySrv;
import com.dfire.soa.netty.server.PushCenter;

import java.util.UUID;

/**
 * @author gantang
 * @Date 2017/7/18
 */
public class Test {
    public static void main(String[] args) throws Exception {
        NettySrv nettySrv = new NettySrv();
        nettySrv.start();
        Cmd cmd = new Cmd();
        cmd.setCode(RemotingCode.UPLOAD);
        cmd.setBody(UUID.randomUUID().toString().getBytes());
        NettyClient client = new NettyClient();
        client.start();
        client.send("localhost:10085", cmd, 3000L);
        cmd.setCode(RemotingCode.PUSH);
        cmd.setBody(UUID.randomUUID().toString().getBytes());
        PushCenter.pushToAll(cmd);
        //PushCenter.pushSingle(cmd, "");
    }
}
