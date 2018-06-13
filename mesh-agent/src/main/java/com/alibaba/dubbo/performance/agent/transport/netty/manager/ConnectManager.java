package com.alibaba.dubbo.performance.agent.transport.netty.manager;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;

public interface ConnectManager {

    Bootstrap getBootstrap();

    Endpoint getEndpoint() throws Exception;

//    void registerChannel(Channel channel, Endpoint endpoint);
//
//    void removeChannel(Channel channel);

    ConnectManager addEndpoint(Endpoint endpoint);

    ConnectManager removeEndpoint(Endpoint endpoint);

}
