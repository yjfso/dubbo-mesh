package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;

public class ClientConnectManager extends AbstractConnectManager {

    private EventLoopGroup eventLoopGroup;
    private ChannelHandler handler;

    public ClientConnectManager(ChannelHandler handler){
        eventLoopGroup = new NioEventLoopGroup();
        this.handler = handler;
        this.initBootstrap();
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(handler);
    }

    public ConnectManager setEndPoints(List<Endpoint> endpoints){
        endpoints.forEach(
                item -> item.setBootstrap(bootstrap)
        );
        this.endpoints = endpoints;
        return this;
    }

    public ConnectManager addEndpoint(Endpoint endpoint){
        endpoint.setBootstrap(bootstrap);
        if (this.endpoints == null){
            this.endpoints = new ArrayList<>();
        }
        this.endpoints.add(endpoint);
        return this;
    }
}
