package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

public class ClientConnectManager extends AbstractConnectManager implements ConnectManager {

    private final static Logger log = LoggerFactory.getLogger(ClientConnectManager.class);

    private ChannelInitializer channelInitializer;
    private List<Endpoint> endpoints;
    private Endpoint activeEndpoint;
    private boolean mutiEndpoint;


    public ClientConnectManager(ChannelInitializer channelInitializer, boolean mutiEndpoint){
        this.mutiEndpoint = mutiEndpoint;
        this.channelInitializer = channelInitializer;
        if (mutiEndpoint){
            this.initEndpoints();
        }
        this.initBootstrap();
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
//                .group(eventLoopGroup.next())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_RCVBUF, 1024 * 32)
                .option(ChannelOption.SO_SNDBUF, 1024 * 32)
                .channel(Const.SOCKET_CHANNEL)
                .handler(channelInitializer);
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

}
