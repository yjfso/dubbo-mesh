package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Endpoint {
    private final String host;
    private final int port;
    private final AtomicInteger requestNum = new AtomicInteger(0);

    private volatile int nowRequestNum = 0;
    private volatile Channel channel;

    private Object lock = new Object();
    private ConnectManager connectManager;
    private ChannelRing channelRing = new ChannelRing();
    private Iterator<Channel> iterator = channelRing.iterator();

    public Endpoint(String host,int port){
        this.host = host;
        this.port = port;
    }

    public Endpoint request(){
        nowRequestNum = requestNum.incrementAndGet();
        return this;
    }
    public Endpoint response(){
        nowRequestNum = requestNum.decrementAndGet();
        return this;
    }

    public Integer getRequestNum(){
        return nowRequestNum;
    }

    public String getHost() {
        return host;
    }

    public InetSocketAddress getInetSocketAddress(){
        return new InetSocketAddress(host, port);
    }

    public int getPort() {
        return port;
    }

    public String toString(){
        return host + ":" + port;
    }

    public boolean equals(Object o){
        if (!(o instanceof Endpoint)){
            return false;
        }
        Endpoint other = (Endpoint) o;
        return other.host.equals(this.host) && other.port == this.port;
    }

    public int hashCode(){
        return host.hashCode() + port;
    }

    public void setConnectManager(ConnectManager connectManager) {
        this.connectManager = connectManager;
    }

    public void removeChannel(){
        channel = null;
    }

    public Channel getChannel() throws Exception{
//        if (channel == null){
//            synchronized (lock){
//                if (channel == null){
//                    channel = connectManager.getBootstrap().connect(new InetSocketAddress(this.getHost(), this.getPort()))
//                            .sync()
//                            .channel();
//                    connectManager.registerChannel(channel, this);
//                }
//            }
//        }
//        return channel;
        if (!iterator.hasNext()){
            synchronized (lock){
                if (!iterator.hasNext()){
                    for (Integer i=0; i<5; i++){
                        channelRing.put(
                                connectManager.getBootstrap().connect(new InetSocketAddress(this.getHost(), this.getPort()))
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
