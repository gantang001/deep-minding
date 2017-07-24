package com.dfire.soa.netty.server;

import com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.alibaba.rocketmq.remoting.common.RemotingUtil;
import com.dfire.soa.netty.common.RouterCenter;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author gantang
 * @Date 2017/7/22
// */
public class ConnectManagerHandle extends ChannelDuplexHandler {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        System.out.println("NETTY SERVER PIPELINE: channelRegistered :" + remoteAddress);
        RouterCenter.put(remoteAddress, ctx.channel());
        super.channelRegistered(ctx);
    }


    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

        super.channelUnregistered(ctx);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        System.out.println("NETTY SERVER PIPELINE: channelRegistered :" + remoteAddress);
        RouterCenter.put(remoteAddress, ctx.channel());
        super.channelActive(ctx);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        System.out.println("NETTY SERVER PIPELINE: channelUnregistered, the channel:" + remoteAddress);
        RouterCenter.remove(remoteAddress);
        super.channelInactive(ctx);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            if (evnet.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
                System.out.println("NETTY SERVER PIPELINE: IDLE exception:" + remoteAddress);
                RouterCenter.remove(remoteAddress);
                closeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingHelper.parseChannelRemoteAddr(ctx.channel());
        System.out.println("NETTY SERVER PIPELINE: exceptionCaught {}" + remoteAddress);
        System.out.println("NETTY SERVER PIPELINE: exceptionCaught exception." + cause);
        RouterCenter.remove(remoteAddress);
        closeChannel(ctx.channel());
    }

    public static void closeChannel(Channel channel) {
        //final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener((ChannelFutureListener) future -> {
//                log.info("closeChannel: close the connection to remote address[{}] result: {}", addrRemote,
//                        future.isSuccess());
        });
    }

}
