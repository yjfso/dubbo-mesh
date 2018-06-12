package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint {
    private final String host;
    private final int port;
    int nowRequestNum = 0;
    private final AtomicInteger totalNum = new AtomicInteger();
    private final AtomicInteger requestNum = new AtomicInteger();
    int channelNum = 4;

    int weight = 0;
    private FixedChannelPool fixedChannelPool;

    public Endpoint(String host,int port){
        this.host = host;
        this.port = port;
    }

    void initChannelManager(ConnectManager connectManager){
        fixedChannelPool = new FixedChannelPool(
                connectManager.getBootstrap().remoteAddress(getInetSocketAddress()),
                connectManager.getHandler(),
                channelNum
        );
    }

    public Future<Channel> getChannelFuture(){
        if (weight>0){
            nowRequestNum = requestNum.incrementAndGet();
        }
        totalNum.getAndIncrement();
        return fixedChannelPool.acquire();
    }

    public void returnChannel(Channel channel){
        if (weight>0){
            nowRequestNum = requestNum.decrementAndGet();
        }
        fixedChannelPool.release(channel);
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
        return host + ":" + port + "|" + nowRequestNum + "|" + totalNum.get();
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

    public void writeAndFlush(Object object){
        getChannelFuture().addListener((FutureListener<Channel>) f1 -> {
            if (f1.isSuccess()) {
                Channel ch = f1.getNow();
                ch.writeAndFlush(object);
                // Release back to pool
                returnChannel(ch);
            }
        });
    }
}
