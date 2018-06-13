package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentRequestEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;

public class AgentClientChannelPoolHandler implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentRequestEncoder());
        pipeline.addLast(new AgentClientHandler());
    }
}
