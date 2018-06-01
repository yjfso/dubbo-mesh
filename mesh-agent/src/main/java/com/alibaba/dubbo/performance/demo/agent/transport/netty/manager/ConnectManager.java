package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

public interface ConnectManager {

    Bootstrap getBootstrap();

    Endpoint getEndpoint() throws Exception;

    void registerChannel(Channel channel, Endpoint endpoint);

    void removeChannel(Channel channel);
}
