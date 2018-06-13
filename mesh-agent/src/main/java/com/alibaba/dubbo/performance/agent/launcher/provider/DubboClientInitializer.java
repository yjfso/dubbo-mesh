package com.alibaba.dubbo.performance.agent.launcher.provider;


import com.alibaba.dubbo.performance.agent.launcher.consumer.AgentClientHandler;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentRequestEncoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

public class DubboClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new DubboRpcEncoder());
        pipeline.addLast(new DubboRpcDecoder());
        pipeline.addLast(new DubboClientHandler());
    }
}

