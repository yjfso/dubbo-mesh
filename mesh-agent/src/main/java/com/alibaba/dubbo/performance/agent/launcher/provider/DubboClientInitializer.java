package com.alibaba.dubbo.performance.agent.launcher.provider;


import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class DubboClientInitializer extends ChannelInitializer<SocketChannel> {

    private DubboClient dubboClient;

    public DubboClientInitializer(DubboClient dubboClient){
        this.dubboClient = dubboClient;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new DubboRpcEncoder());
        pipeline.addLast(new DubboRpcDecoder());
        pipeline.addLast(new DubboClientHandler(this.dubboClient));
    }
}