package com.dfire.soa.netty.romoting;

import io.netty.channel.Channel;

/**
 * @author gantang
 * @Date 2017/7/22
 */
public class Dispatcher {
    public void dispatch(Channel channel, Cmd cmd) {
        // TODO: 2017/7/24 业务处理
        System.out.println(new String(cmd.getBody()));
    }
}
