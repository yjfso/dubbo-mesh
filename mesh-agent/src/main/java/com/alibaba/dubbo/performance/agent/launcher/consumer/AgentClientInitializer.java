package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentRequestEncoder;
import com.alibaba.dubbo.performance.agent.transport.netty.coder.agent.AgentDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;

public class AgentClientInitializer implements ChannelPoolHandler {

    private AgentClient agentClient;

    public AgentClientInitializer(AgentClient agentClient){
        this.agentClient = agentClient;
    }

//    @Override
//    protected void initChannel(SocketChannel socketChannel) {
//        ChannelPipeline pipeline = socketChannel.pipeline();
////        pipeline.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
////        //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
////        pipeline.addLast(new ObjectEncoder());
////        pipeline.addLast(new ProtostuffDecoder())
////                .addLast(new ProtostuffEncoder(AgentRequest.class))
////                ;
//
//        pipeline.addLast("decoder", new AgentDecoder());
//        pipeline.addLast("encoder", new AgentRequestEncoder());
//        pipeline.addLast(new AgentClientHandler(agentClient));
//    }
    @Override
    public void channelReleased(Channel channel) throws Exception {

    }

    @Override
    public void channelAcquired(Channel channel) throws Exception {

    }

    @Override
    public void channelCreated(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
//        pipeline.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//        //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
//        pipeline.addLast(new ObjectEncoder());
//        pipeline.addLast(new ProtostuffDecoder())
//                .addLast(new ProtostuffEncoder(AgentRequest.class))
//                ;

        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentRequestEncoder());
        pipeline.addLast(new AgentClientHandler(agentClient));
    }
}
