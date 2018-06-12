package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
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
    private ChannelPoolHandler handler;
//    private Map<Channel, Endpoint> channelEndpointMap = new ConcurrentHashMap<>();

    private List<Endpoint> endpoints;
    private Endpoint activeEndpoint;
    private boolean mutiEndpoint;


    public ClientConnectManager(ChannelPoolHandler poolHandler, boolean mutiEndpoint){
        this.mutiEndpoint = mutiEndpoint;
        eventLoopGroup = new NioEventLoopGroup(2);
        this.handler = poolHandler;
        if (mutiEndpoint){
            this.initEndpoints();
        }
        this.initBootstrap();
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.TCP_NODELAY, false)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);
//                .handler(handler);
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
                            int itemRequest = endpoint.nowRequestNum;
                            if ((min.nowRequestNum * endpoint.weight > itemRequest * min.weight) && itemRequest <= Const.MAX_DUBBO_REQUEST) {
                                min = endpoint;
                            }
                        }
                        activeEndpoint = min;
                    }
                    Thread.sleep(Const.LOAD_BALANCE_REFRESH_TIME);
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
        endpoint.initChannelManager(this);
        if(!mutiEndpoint){
            activeEndpoint = endpoint;
        } else{
            synchronized (this){
                this.endpoints.add(endpoint);
            }
        }
        return this;
    }

//    public void registerChannel(Channel channel, Endpoint endpoint){
//        channelEndpointMap.put(channel, endpoint);
//    }

//    public void removeChannel(Channel channel) {
//        Endpoint endpoint = channelEndpointMap.get(channel);
//        if (endpoint!=null){
//            endpoint.getChannelManager().removeChannel(channel);
//            channelEndpointMap.remove(channel);
//        }
//    }

    public Endpoint getEndpoint() throws Exception {
        return activeEndpoint;
    }

    public ChannelPoolHandler getHandler(){
        return this.handler;
    }

}
