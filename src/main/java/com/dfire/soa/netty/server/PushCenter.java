package com.dfire.soa.netty.server;

import com.dfire.soa.netty.common.RouterCenter;
import com.dfire.soa.netty.romoting.Cmd;
import io.netty.channel.Channel;

/**
 * @author gantang
 * @Date 2017/7/22
 */
public class PushCenter {

    public static void pushToAll(Cmd msg) {
        RouterCenter.getAllChannel().forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(msg);
            }
        });
    }

    public static void pushSingle(Cmd msg, String channelId) {
        Channel channel = RouterCenter.getSingle(channelId);
        channel.writeAndFlush(msg);
    }
}
