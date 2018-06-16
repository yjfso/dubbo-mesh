package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.registry.IRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;


import java.net.InetSocketAddress;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Provider {

    public static Provider INSTANCE;

    static DubboClient dubboClient;
    private int weight = Integer.valueOf(System.getProperty("server.weight"));
//    private ExecutorService providerExecutor;
    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;

    private Provider() throws Exception{
        INSTANCE = this;

        bossGroup = Const.EVENT_LOOP_GROUP.getConstructor(int.class).newInstance(Const.PROVIDER_SER_BOSS);
        workerGroup = Const.EVENT_LOOP_GROUP.getConstructor(int.class).newInstance(Const.PROVIDER_SER_WORKER);

        dubboClient = new DubboClient();
        registerServer();
//        startWorkThread();
        startServer();
    }
    public static void init() throws Exception{
        new Provider();
    }

    private void startServer() throws Exception{
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(Const.SERVER_SOCKET_CHANNEL)
                    .childHandler(new ProviderInitializer())
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, false)
                    .bind(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

//    private void startWorkThread(){
//        int num = 210;
//        providerExecutor = Executors.newFixedThreadPool(num);
//    }

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
