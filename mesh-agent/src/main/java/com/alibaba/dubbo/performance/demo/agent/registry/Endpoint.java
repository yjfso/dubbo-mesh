package com.alibaba.dubbo.performance.demo.agent.registry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint {
    private final String host;
    private final int port;
    private final AtomicInteger requestNum = new AtomicInteger(0);
    private Channel channel;
    private Object lock = new Object();

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

    public Channel getChannel(Bootstrap bootstrap) throws Exception{
        if (channel == null){
            synchronized (lock){
                if (channel == null){
                    channel = bootstrap.connect(new InetSocketAddress(this.getHost(), this.getPort())).sync().channel();
                }
            }
        }
        return this.channel;
    }
}
