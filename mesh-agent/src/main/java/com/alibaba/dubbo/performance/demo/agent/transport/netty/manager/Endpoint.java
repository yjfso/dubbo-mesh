package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint {
    private final String host;
    private final int port;
    private final AtomicInteger requestNum = new AtomicInteger(0);

    private Object lock = new Object();
    private Bootstrap bootstrap;
    private ChannelRing channelRing = new ChannelRing();
    private Iterator<Channel> iterator = channelRing.iterator();

    public Endpoint(String host,int port){
        this.host = host;
        this.port = port;
    }

    public Endpoint request(){
        requestNum.incrementAndGet();
        return this;
    }
    public Endpoint response(){
        requestNum.decrementAndGet();
        return this;
    }

    public Integer getRequestNum(){
        return requestNum.get();
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

    public void setBootstrap(Bootstrap bootstrap){
        this.bootstrap = bootstrap;
    }
    public Channel getChannel() throws Exception{
        if (!iterator.hasNext()){
            synchronized (lock){
                if (!iterator.hasNext()){
                    for (Integer i=0; i<10; i++){
                        channelRing.put(
                                bootstrap.connect(new InetSocketAddress(this.getHost(), this.getPort()))
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
