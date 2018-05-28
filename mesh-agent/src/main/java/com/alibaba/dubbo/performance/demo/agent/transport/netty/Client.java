package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.LoadBalance;
import com.alibaba.dubbo.performance.demo.agent.transport.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.model.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Client {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    private Bootstrap bootstrap;


    public Client() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ClientInitializer());
    }

    public Object invoke(Request request) throws Exception {
        Endpoint endpoint = LoadBalance.getEndpoint();
        Channel channel = endpoint.getChannel(bootstrap);

        RpcFuture future = new RpcFuture();
        RequestHolder.put(String.valueOf(request.getId()),future);

        endpoint.request();
        channel.writeAndFlush(request);
        Object result = null;
        try {
            result = future.get();
            endpoint.response();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
