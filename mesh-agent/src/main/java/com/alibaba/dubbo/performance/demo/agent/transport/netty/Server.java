package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Server {

    private static Server server;

    public static void init() throws Exception{
        server = new Server();
    }

    private Server() throws Exception{
        registerServer();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            int port = Integer.valueOf(System.getProperty("server.port"));
            ChannelFuture future = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServiceInitializer())
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

    private void registerServer(){
        IRegistry etcdRegistry = new EtcdRegistry(System.getProperty("etcd.url"));
        try {
            int port = Integer.valueOf(System.getProperty("server.port"));
            etcdRegistry.register("com.alibaba.dubbo.performance.demo.provider.IHelloService",port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
