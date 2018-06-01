package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.transport.netty.coder.AgentDecoder;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.coder.AgentEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ServiceInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//        pipeline.addLast(new ObjectDecoder(1024*1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//        //添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
//        pipeline.addLast(new ObjectEncoder());
//        pipeline.addLast("decoder", new StringDecoder());
//        pipeline.addLast("encoder", new StringEncoder());
//        pipeline.addLast(new ProtostuffDecoder(AgentRequest.class))
//                .addLast(new ProtostuffEncoder())
//                ;
        pipeline.addLast("decoder", new AgentDecoder());
        pipeline.addLast("encoder", new AgentEncoder());
        pipeline.addLast(new ServiceHandler());

    }
}
