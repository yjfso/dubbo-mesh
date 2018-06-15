package com.alibaba.dubbo.performance.agent.launcher.provider;



import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class DubboClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(DubboRpcEncoder.INSTANCE);
        pipeline.addLast(new DubboRpcDecoder());
        pipeline.addLast(DubboClientHandler.INSTANCE);
    }
}

