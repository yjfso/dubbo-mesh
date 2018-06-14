package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ProviderInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentEncoder());
        pipeline.addLast(new ProviderHandler());

    }
}
