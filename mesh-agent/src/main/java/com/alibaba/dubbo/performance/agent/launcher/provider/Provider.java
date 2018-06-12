package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.registry.IRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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
public class Provider {

    public static Provider INSTANCE;

    static DubboClient dubboClient;
    private int weight = Integer.valueOf(System.getProperty("server.weight"));
    ExecutorService providerExecutor;
    static EventLoopGroup bossGroup;
    static EventLoopGroup workerGroup;

    private Provider() throws Exception{
        INSTANCE = this;
        bossGroup = new NioEventLoopGroup(2);
        workerGroup = new NioEventLoopGroup(8);
        dubboClient = new DubboClient();
        registerServer();
        startWorkThread();
        startServer();
    }
    public static void init() throws Exception{
        new Provider();
    }

    private void startServer() throws Exception{
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderInitializer(this))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, false)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWorkThread(){
        int num = 210;
        providerExecutor = Executors.newFixedThreadPool(num);
    }

    private void registerServer(){
        IRegistry etcdRegistry = new EtcdRegistry(System.getProperty("etcd.url"));
        try {
            int port = Integer.valueOf(System.getProperty("server.port"));
            etcdRegistry.register("com.alibaba.dubbo.performance.demo.provider.IHelloService", port, weight);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
