package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;


import java.net.InetSocketAddress;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Consumer {

    public static Consumer INSTANCE;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    private Consumer() throws Exception{

        INSTANCE = this;

        bossGroup = Const.EVENT_LOOP_GROUP.getConstructor(int.class).newInstance(Const.CONSUMER_SER_BOSS);
        workerGroup = Const.EVENT_LOOP_GROUP.getConstructor(int.class).newInstance(Const.CONSUMER_SER_WORKER);
        AgentClient.init();
        startServer();
    }

    public static void init() throws Exception{
        new Consumer();
    }

    private void startServer() throws Exception{
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(Const.SERVER_SOCKET_CHANNEL)
                    .childHandler(new ConsumerInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
