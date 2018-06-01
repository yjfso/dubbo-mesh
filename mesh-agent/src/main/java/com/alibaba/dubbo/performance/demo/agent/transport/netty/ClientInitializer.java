package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.transport.netty.coder.AgentDecoder;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.coder.AgentEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private Client client;

    public ClientInitializer(Client client){
        this.client = client;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
//        pipeline.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//        //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
//        pipeline.addLast(new ObjectEncoder());
//        pipeline.addLast(new ProtostuffDecoder())
//                .addLast(new ProtostuffEncoder(AgentRequest.class))
//                ;

        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentEncoder());
        pipeline.addLast(new ClientHandler(client));
    }
}
