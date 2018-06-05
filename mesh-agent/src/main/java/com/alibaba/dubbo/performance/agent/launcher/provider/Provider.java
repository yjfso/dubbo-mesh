package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.registry.IRegistry;
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
public class Provider {

    private static Provider INSTANCE;

    static DubboClient dubboClient;
    private int weight = 1;
    ExecutorService providerExecutor;

    private Provider() throws Exception{
        dubboClient = new DubboClient();
        this.weight = Integer.valueOf(System.getProperty("server.weight"));
        registerServer();
        startWorkThread();
        startServer();
    }
    public static void init() throws Exception{
        INSTANCE = new Provider();
    }

    private void startServer() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ProviderInitializer(this))
//                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .childOption(ChannelOption.TCP_NODELAY, false)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void startWorkThread(){
        int num = 110;// + weight * 2;
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
