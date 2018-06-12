package com.alibaba.dubbo.performance.agent.launcher.consumer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ConsumerInitializer extends ChannelInitializer<SocketChannel> {

    private Consumer consumer;

    ConsumerInitializer(Consumer consumer){
        this.consumer = consumer;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        pipeline.addLast(new ConsumerHandler(consumer));

    }
}
