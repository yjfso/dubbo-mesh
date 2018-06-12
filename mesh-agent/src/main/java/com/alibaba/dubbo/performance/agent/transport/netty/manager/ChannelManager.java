package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class ChannelManager {

    private final Object lock = new Object();
    private ConnectManager connectManager;
    private ChannelRing channelRing = new ChannelRing();
    private Iterator<Channel> iterator = channelRing.iterator();
    private Endpoint endpoint;
    private Logger logger = LoggerFactory.getLogger(ChannelManager.class);
//    private Channel channel;

    ChannelManager(ConnectManager connectManager, Endpoint endpoint){
        this.connectManager = connectManager;
        this.endpoint = endpoint;
//        addChannels(endpoint.channelNum);
    }

    void removeChannel(Channel channel){
//        this.channel = null;
        channelRing.remove(channel);
        addNewChannel();
    }

    private void addNewChannel(){
        try{
            Channel channel = connectManager.getBootstrap().connect(endpoint.getInetSocketAddress())
                    .sync()
                    .channel();
            channelRing.put(channel);
            connectManager.registerChannel(channel, endpoint);
        } catch (Exception e){
            logger.error("new Channel error", e);
        }

    }

    private void addChannels(int num){
        for (Integer i = 0; i < num; i++) {
            addNewChannel();
        }
    }

    public Channel getChannel() throws Exception{
//        if (channel == null){
//            synchronized (lock){
//                if(channel == null){
//                    channel = connectManager.getBootstrap().connect(endpoint.getInetSocketAddress())
//                            .sync()
//                            .channel();
//                    connectManager.registerChannel(channel, endpoint);
//                }
//            }
//        }
//        return channel;
        if(!iterator.hasNext()){
            synchronized (lock){
                if (!iterator.hasNext()){
                    addChannels(endpoint.channelNum);
                }
            }
            if(!iterator.hasNext()){
                return null;
            }
        }
        return iterator.next();
    }
}
