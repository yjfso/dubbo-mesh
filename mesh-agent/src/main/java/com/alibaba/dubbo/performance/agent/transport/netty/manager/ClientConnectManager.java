package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

public class ClientConnectManager extends AbstractConnectManager implements ConnectManager {

    private final static Logger log = LoggerFactory.getLogger(ClientConnectManager.class);
    private EventLoopGroup eventLoopGroup;
    private ChannelHandler handler;
    private Map<Channel, Endpoint> channelEndpointMap = new ConcurrentHashMap<>();

    private List<Endpoint> endpoints;
    private Endpoint activeEndpoint;


    public ClientConnectManager(ChannelHandler handler){
        eventLoopGroup = new NioEventLoopGroup();
        this.handler = handler;
        this.initEndpoints();
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

    private void initEndpoints(){
        this.endpoints = new ArrayList<>();
        Executors.newSingleThreadExecutor().submit(()->{
            while(true){
                try{
                    Iterator<Endpoint> iterator = endpoints.iterator();
                    if(iterator.hasNext()) {
                        Endpoint min = iterator.next();
                        while (iterator.hasNext()) {
                            Endpoint endpoint = iterator.next();
                            if (min.nowRequestNum * endpoint.weight > endpoint.nowRequestNum * min.weight) {
                                min = endpoint;
                            }
                        }
                        activeEndpoint = min;
                    }
                    Thread.sleep(9);
                } catch (Exception e){
                    log.error("load blance thread catch error", e);
                }
            }
        });
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
        System.out.println("route to "+ activeEndpoint + "\n-----------------------------");
        return activeEndpoint;
    }

}
