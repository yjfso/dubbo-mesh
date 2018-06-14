package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class Endpoint {

    private final static Logger log = LoggerFactory.getLogger(Endpoint.class);
    private final String host;
    private final int port;
    int nowRequestNum = 0;
    private final AtomicInteger totalNum = new AtomicInteger();
    private final AtomicInteger requestNum = new AtomicInteger();
    int channelNum = 4;
    private FastThreadLocal<ChannelFuture> threadChannel = new FastThreadLocal<>();
    private ConnectManager connectManager;

    int weight = 0;
//    private FixedChannelPool fixedChannelPool;

    public Endpoint(String host,int port){
        this.host = host;
        this.port = port;
    }

    void initChannelManager(ConnectManager connectManager){
        this.connectManager = connectManager;
//        connectManager.getBootstrap().clone().group()
//        fixedChannelPool = new FixedChannelPool(
//                connectManager.getBootstrap().remoteAddress(getInetSocketAddress()),
//                connectManager.getHandler(),
//                channelNum
//        );
    }

    public ChannelFuture getChannelFuture(ChannelHandlerContext ctx){
        if (weight>0){
            nowRequestNum = requestNum.incrementAndGet();
        }
        totalNum.getAndIncrement();
        if(threadChannel.get()==null){
            try{
                ChannelFuture future = connectManager.getBootstrap().clone()
                        .group(ctx.channel().eventLoop()).connect(getInetSocketAddress());
//                        .sync()
//                        .channel();
                threadChannel.set(future);
            } catch (Exception e){
                log.error("create channel error", e);
            }
        }
        return threadChannel.get();
    }

    public void returnChannel(){
        if (weight>0){
            nowRequestNum = requestNum.decrementAndGet();
        }
//        fixedChannelPool.release(channel);
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

}
