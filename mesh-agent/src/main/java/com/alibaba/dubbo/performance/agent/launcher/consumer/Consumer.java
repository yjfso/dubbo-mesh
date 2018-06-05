package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.launcher.provider.ProviderInitializer;
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
public class Consumer {

    private static Consumer INSTANCE;

    static AgentClient agentClient;
    ExecutorService providerExecutor;

    private Consumer() throws Exception{
        agentClient = new AgentClient();
        registerServer();
        startWorkThread();
        startServer();
    }

    public static void init() throws Exception{
        INSTANCE = new Consumer();
    }

    private void startServer() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ConsumerHandler(this))
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
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
