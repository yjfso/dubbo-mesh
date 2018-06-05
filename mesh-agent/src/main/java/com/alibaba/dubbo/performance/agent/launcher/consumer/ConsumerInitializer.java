package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.launcher.provider.Provider;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ConsumerInitializer extends ChannelInitializer<SocketChannel> {

    private Provider provider;

    ConsumerInitializer(Provider provider){
        this.provider = provider;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentEncoder());
        pipeline.addLast(new ConsumerHandler(provider));

    }
}
