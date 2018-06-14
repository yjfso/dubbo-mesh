package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Consumer {

    public static Consumer INSTANCE;

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    public static ExecutorService executorService;

    private Consumer() throws Exception{
        INSTANCE = this;
        bossGroup = new NioEventLoopGroup(Const.CONSUMER_SER_BOSS);
        workerGroup = new NioEventLoopGroup(Const.CONSUMER_SER_WORKER);
//        startWorkThread();
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
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ConsumerInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWorkThread(){
        executorService = Executors.newFixedThreadPool(Const.CONSUMER_EXECUTOR);
    }

}
