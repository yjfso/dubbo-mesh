package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint {
    private final String host;
    private final int port;
    private final AtomicInteger requestNum = new AtomicInteger(0);
    private ChannelManager channelManager;
    int channelNum = 3;
    int weight = 1;

    int nowRequestNum = 0;

    public Endpoint(String host,int port){
        this.host = host;
        this.port = port;
    }

    public void initChannelManager(ConnectManager connectManager){
        if (channelManager == null){
           this.channelManager = new ChannelManager(connectManager, this);
        }
    }

    public ChannelManager getChannelManager(){
        return this.channelManager;
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

    public Endpoint setWeight(int weight) {
        this.weight = weight;
        return this;
    }
}
