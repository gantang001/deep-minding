package com.dfire.soa.netty.server;

import com.dfire.soa.netty.encode.NettyDecoder;
import com.dfire.soa.netty.encode.NettyEncoder;
import com.dfire.soa.netty.romoting.Dispatcher;
import com.dfire.soa.netty.romoting.Cmd;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gantang on 2016/12/14.
 */
public class NettySrv {

    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private static volatile boolean isLinuxPlatform = false;
    private int serverSelectorThreads = Runtime.getRuntime().availableProcessors() << 1;
    private int port = 10085;
    //线程命名计数器
    private AtomicInteger selectorNums = new AtomicInteger();
    private AtomicInteger threadNums = new AtomicInteger();
    public static final String OS_NAME = System.getProperty("os.name");
    private Dispatcher dispatcher = new Dispatcher();

    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().contains("linux")) {
            isLinuxPlatform = true;
        }

        if (OS_NAME != null && OS_NAME.toLowerCase().contains("windows")) {
            isLinuxPlatform = false;
        }
    }

    //默认bossEventLoop 为1个线程
    private EventLoopGroup boss = new NioEventLoopGroup(1);
    //默认workerEventLoop 为CPU核数*2个线程
    private EventLoopGroup worker;
    //事件处理线程池
    private Executor executor = Executors.newFixedThreadPool(20, (r) -> new Thread(r, "Event Handle Thread-" + threadNums.incrementAndGet()));

    public void start() throws Exception {
        if (isLinuxPlatform) {
            this.worker = new EpollEventLoopGroup(serverSelectorThreads, (r) -> new Thread(r, String.format("NettyServerEPOLLSelector_%d", selectorNums.incrementAndGet())));
        } else {
            this.worker = new NioEventLoopGroup(serverSelectorThreads, (r) -> new Thread(r, String.format("NettyServerEPOLLSelector_%d", selectorNums.incrementAndGet())));
        }
        ServerBootstrap childHandler = bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                /**ChannelOption.SO_BACKLOG 对应的是tcp/ip协议listen函数中的backlog参数，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，
                 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，
                 backlog参数指定了队列的大小 */
                .option(ChannelOption.SO_BACKLOG, 1024)
                /**　ChanneOption.SO_REUSEADDR 对应于套接字选项中的SO_REUSEADDR，这个参数表示允许重复使用本地地址和端口，
                 比如，某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误，使用该参数就可以解决问题，
                 该参数允许共用该端口，这个在服务器程序中比较常使用，比如某个进程非正常退出，该程序占用的端口可能要被占用一段时间才能允许其他进程使用，
                 而且程序死掉以后，内核一需要一定的时间才能够释放此端口，不设置SO_REUSEADDR
                 */
                .option(ChannelOption.SO_REUSEADDR, true)
                /**
                 * ChannelOption.SO_LINGER参数对应于套接字选项中的SO_LINGER,Linux内核默认的处理方式是当用户调用close（）方法的时候，函数返回，在可能的情况下，
                 * 尽量发送数据，不一定保证会发生剩余的数据，造成了数据的不确定性，使用SO_LINGER可以阻塞close()的调用时间，直到数据完全发送。在使用NIO时不建议设置
                 */
                //.option(ChannelOption.SO_LINGER, 0)
                /**Channeloption.SO_KEEPALIVE 参数对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，
                 * 这个选项用于可能长时间没有数据交流的连接。当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
                 */
                .option(ChannelOption.SO_KEEPALIVE, false)
                /**　ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关Nagle算法是将小的数据包组装为更大的帧然后进行发送，
                 * 而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效负载，但是却造成了延时，
                 * 而该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输，于TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送数据，适用于文件传输。
                 */
                .childOption(ChannelOption.TCP_NODELAY, true)
                /**
                 * 每个套接口都有一个发送缓冲区和一个接收缓冲区，使用SO_SNDBUF & SO_RCVBUF可以改变缺省缓冲区大小。
                 * 对于客户端，SO_RCVBUF选项须在connect之前设置。对于服务器，SO_RCVBUF选项须在listen前设置.
                 */
                .option(ChannelOption.SO_SNDBUF, 65535)
                /**
                 * 先明确一个概念：每个TCP socket在内核中都有一个发送缓冲区和一个接收缓冲区，TCP的全双工的工作模式以及TCP的滑动窗口便是依赖于这两个独立的buffer以及此buffer的填充状态。
                 * 接收缓冲区把数据缓存入内核，应用进程一直没有调用read进行读取的话，此数据会一直缓存在相应socket的接收缓冲区内。再啰嗦一点，不管进程是否读取socket，
                 * 对端发来的数据都会经由内核接收并且缓存到socket的内核接收缓冲区之中。read所做的工作，就是把内核缓冲区中的数据拷贝到应用层用户的buffer里面，仅此而已。进程调用send发送的数据的时候，
                 * 最简单情况（也是一般情况），将数据拷贝进入socket的内核发送缓冲区之中，然后send便会在上层返回。换句话说，send返回之时，数据不一定会发送到对端去（和write写文件有点类似），
                 * send仅仅是把应用层buffer的数据拷贝进socket的内核发送buffer中。后续我会专门用一篇文章介绍read和send所关联的内核动作。每个UDP socket都有一个接收缓冲区，没有发送缓冲区，
                 * 从概念上来说就是只要有数据就发，不管对方是否可以正确接收，所以不缓冲，不需要发送缓冲区。
                 * 接收缓冲区被TCP和UDP用来缓存网络上来的数据，一直保存到应用进程读走为止。对于TCP，如果应用进程一直没有读取，buffer满了之后，发生的动作是：通知对端TCP协议中的窗口关闭。这个便是滑动窗口的实现。
                 * 保证TCP套接口接收缓冲区不会溢出，从而保证了TCP是可靠传输。因为对方不允许发出超过所通告窗口大小的数据。 这就是TCP的流量控制，如果对方无视窗口大小而发出了超过窗口大小的数据，则接收方TCP将丢弃它。
                 * UDP：当套接口接收缓冲区满时，新来的数据报无法进入接收缓冲区，此数据报就被丢弃。UDP是没有流量控制的；快的发送者可以很容易地就淹没慢的接收者，导致接收方的UDP丢弃数据报。
                 * 以上便是TCP可靠，UDP不可靠的实现。
                 * 这两个选项就是来设置TCP连接的两个buffer尺寸的。
                 */
                .option(ChannelOption.SO_RCVBUF, 65535)
                //绑定端口
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyEncoder(), new NettyDecoder(),
                                new TestHandler(),new ConnectManagerHandle());
                    }
                });
        childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        ChannelFuture sync = bootstrap.bind().sync();
        System.out.println(sync.isDone());
    }

    private class TestHandler extends SimpleChannelInboundHandler<Cmd> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Cmd msg) throws Exception {
            dispatcher.dispatch(ctx.channel(), msg);
        }
    }


    public void requestProcessor(ChannelHandlerContext ctx, Cmd msg) {
        executor.execute(() -> doSomting(ctx));
    }

    public void responseProcessor(ChannelHandlerContext ctx, Cmd msg) {
        executor.execute(() -> doSomting(ctx));
    }

    public <T> void doSomting(T t) {
        //do buziness
    }

}
