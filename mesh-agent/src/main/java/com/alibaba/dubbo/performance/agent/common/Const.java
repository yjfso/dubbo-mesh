package com.alibaba.dubbo.performance.agent.common;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by yinjianfeng on 18/6/10.
 */
public class Const {

    public final static byte CR = 13;
    public final static byte QUOTA = 34;
    public final static byte PERCENT = 37;
    public final static byte[] DUBBO_VERSION = "2.0.1".getBytes();
    public final static byte[] NULL = "null".getBytes();
    public final static byte[] VOID_JSON = "{}".getBytes();

    public final static int LOAD_BALANCE_REFRESH_TIME = 5;

    public final static int AGENT_REQUEST_NUM = 500;
    public final static int DUBBO_REQUEST_NUM = 400;

    public final static int MAX_DUBBO_REQUEST = 200;
    public final static int CONSUMER_SER_BOSS = 1;
    public final static int CONSUMER_SER_WORKER = 8;
    public final static int PROVIDER_SER_BOSS = 1;
    public final static int PROVIDER_SER_WORKER = 8;

//    public final static Class<? extends ServerSocketChannel> SERVER_SOCKET_CHANNEL = EpollServerSocketChannel.class;
//    public final static Class<? extends SocketChannel> SOCKET_CHANNEL = EpollSocketChannel.class;
//    public final static Class<? extends EventLoopGroup> EVENT_LOOP_GROUP = EpollEventLoopGroup.class;

    public final static Class<? extends ServerSocketChannel> SERVER_SOCKET_CHANNEL = KQueueServerSocketChannel.class;
    public final static Class<? extends SocketChannel> SOCKET_CHANNEL = KQueueSocketChannel.class;
    public final static Class<? extends EventLoopGroup> EVENT_LOOP_GROUP = KQueueEventLoopGroup.class;
}
