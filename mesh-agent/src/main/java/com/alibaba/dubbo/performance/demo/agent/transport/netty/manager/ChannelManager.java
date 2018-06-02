package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Iterator;

public class ChannelManager {

    private final Object lock = new Object();
    private ConnectManager connectManager;
    private ChannelRing channelRing = new ChannelRing();
    private Iterator<Channel> iterator = channelRing.iterator();
    private Endpoint endpoint;

    ChannelManager(ConnectManager connectManager, Endpoint endpoint){
        this.connectManager = connectManager;
        this.endpoint = endpoint;
    }

    public void removeChannel(Channel channel){
        channelRing.remove(channel);
    }

    public Channel getChannel() throws Exception{
        if (!iterator.hasNext()){
            synchronized (lock){
                if (!iterator.hasNext()){
                    for (Integer i=0; i<5; i++){
                        channelRing.put(
                                connectManager.getBootstrap().connect(endpoint.getInetSocketAddress())
                                        .sync()
                                        .channel()
                        );
                    }
                }
            }
        }
        return iterator.next();
    }
}
