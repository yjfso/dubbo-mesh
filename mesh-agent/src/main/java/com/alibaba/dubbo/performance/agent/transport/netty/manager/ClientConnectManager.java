package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientConnectManager extends AbstractConnectManager implements ConnectManager {

    private EventLoopGroup eventLoopGroup;
    private ChannelHandler handler;
    private Map<Channel, Endpoint> channelEndpointMap = new ConcurrentHashMap<>();

    private List<Endpoint> endpoints = new ArrayList<>();


    public ClientConnectManager(ChannelHandler handler){
        eventLoopGroup = new NioEventLoopGroup();
        this.handler = handler;
        this.initBootstrap();
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.TCP_NODELAY, false)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(handler);
    }

    public ConnectManager removeEndpoint(Endpoint endpoint){
        synchronized (this){
            this.endpoints.remove(endpoint);
        }
        return this;
    }

    public ConnectManager addEndpoint(Endpoint endpoint){
        synchronized (this){
            endpoint.initChannelManager(this);
            this.endpoints.add(endpoint);
        }
        return this;
    }

    public void registerChannel(Channel channel, Endpoint endpoint){
        channelEndpointMap.put(channel, endpoint);
    }

    public void removeChannel(Channel channel) {
        Endpoint endpoint = channelEndpointMap.get(channel);
        if (endpoint!=null){
            endpoint.getChannelManager().removeChannel(channel);
            channelEndpointMap.remove(channel);
        }
    }

    public Endpoint getEndpoint() throws Exception {
        Iterator<Endpoint> iterator = endpoints.iterator();
        if(!iterator.hasNext()){
            throw new Exception("lack of endpoint");
        }
        Endpoint min = iterator.next();
        while (iterator.hasNext()){
            Endpoint endpoint = iterator.next();
            if (min.nowRequestNum * endpoint.weight > endpoint.nowRequestNum * min.weight){
                min = endpoint;
            }
        }
        System.out.println("route to "+ min + "\n-----------------------------");
        return min;
    }

}
