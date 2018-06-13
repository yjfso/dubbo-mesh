package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentRequestEncoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

public class AgentClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("decoder", new AgentDecoder());
//        pipeline.addLast("encoder", new AgentRequestEncoder());
        pipeline.addLast(new AgentClientHandler());
    }
}
