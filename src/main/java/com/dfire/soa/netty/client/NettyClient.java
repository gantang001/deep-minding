package com.dfire.soa.netty.client;

import com.alibaba.rocketmq.remoting.exception.RemotingConnectException;
import com.dfire.soa.netty.common.ChannelWrapper;
import com.dfire.soa.netty.encode.NettyDecoder;
import com.dfire.soa.netty.encode.NettyEncoder;
import com.dfire.soa.netty.romoting.Cmd;
import com.dfire.soa.netty.romoting.Response;
import com.dfire.soa.netty.romoting.ResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author gantang
 * @Date 2017/7/18
 */
public class NettyClient {

    private Bootstrap bootstrap = new Bootstrap();

    private final ConcurrentHashMap<String, ChannelWrapper> channelTable = new ConcurrentHashMap<>();

    private AtomicLong countDown = new AtomicLong(0);

    private final ConcurrentHashMap<Long, ResponseFuture> responseTable = new ConcurrentHashMap<>();

    private int connectTimeoutMillis = 3000;

    public void start() {
        this.bootstrap.group(new NioEventLoopGroup(1)).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                //
                .option(ChannelOption.SO_KEEPALIVE, false)
                //
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyEncoder(), new NettyDecoder(), new ClientHandle());
                    }
                });
    }

    public Response send(String addr, Cmd request, long timeOut) throws InterruptedException, RemotingConnectException {
        long opaque = countDown.incrementAndGet();
        try {
            final Channel channel = this.getAndCreateChannel(addr);
            if (channel != null && channel.isActive()) {

                final ResponseFuture responseFuture = new ResponseFuture(timeOut);
                responseTable.put(opaque, responseFuture);
                channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        System.out.println("send success");
                        //发送成功
                    } else {
                        //发送失败
                        System.out.println(future.cause());
                    }
                });

                Response response = responseFuture.waitResponse();
                if (response == null) {
                    // TODO: 2017/7/18   throw new Exception();
                }
                return response;
            } else {
                throw new RemotingConnectException(addr);
            }
        } finally {
            responseTable.remove(opaque);
        }
    }

    public Channel getAndCreateChannel(String addr) {
        ChannelWrapper channelWrapper = channelTable.get(addr);
        if (channelWrapper != null && channelWrapper.isOK()) {
            return channelWrapper.getChannel();
        }
        return createChannel(addr);
    }

    public Channel createChannel(String addr) {
        ChannelWrapper channelWrapper = channelTable.get(addr);
        if (channelWrapper != null && channelWrapper.isOK()) {
            return channelWrapper.getChannel();
        }
        synchronized (channelTable) {
            channelWrapper = channelTable.get(addr);
            if (channelWrapper != null && channelWrapper.isOK()) {
                return channelWrapper.getChannel();
            }
            ChannelFuture channelFuture = this.bootstrap.connect(string2SocketAddress(addr));
            channelWrapper = new ChannelWrapper(channelFuture);
            this.channelTable.put(addr, channelWrapper);
        }
        ChannelFuture channelFuture = channelWrapper.getChannelFuture();
        if (channelFuture.awaitUninterruptibly(connectTimeoutMillis)) {
            if (channelWrapper.isOK()) {
                return channelWrapper.getChannel();
            } else {
//                log.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
            }
        } else {
//            log.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, connectTimeoutMillis,
//                    channelFuture.toString());
        }

        return null;
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        InetSocketAddress isa = new InetSocketAddress(s[0], Integer.parseInt(s[1]));
        return isa;
    }
}
