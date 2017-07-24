package com.dfire.soa.netty.client;

import com.dfire.soa.netty.romoting.Cmd;
import com.dfire.soa.netty.romoting.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author gantang
 * @Date 2017/7/22
 */
public class ClientHandle extends SimpleChannelInboundHandler<Cmd> {

    private Dispatcher dispatcher = new Dispatcher();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
        dispatcher.dispatch(ctx.channel(), msg);
    }
}