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

public class DubboClientInitializer implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new DubboRpcEncoder());
        pipeline.addLast(new DubboRpcDecoder());
        pipeline.addLast(new DubboClientHandler());
    }
}
