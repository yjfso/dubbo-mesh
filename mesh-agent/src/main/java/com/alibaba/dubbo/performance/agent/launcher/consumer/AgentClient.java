package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentClient {

    private static Logger logger = LoggerFactory.getLogger(DubboClient.class);
    private ConnectManager connectManager;
    public static AgentClient INSTANCE;

    public static void init(){
        INSTANCE = new AgentClient();
    }

    public AgentClient()  {
        try{
            this.connectManager = new ClientConnectManager(
                    new AgentClientInitializer(), true
            );
            new EtcdRegistry(System.getProperty("etcd.url"))
                    .watch("com.alibaba.dubbo.performance.demo.provider.IHelloService", connectManager);
        } catch (Exception e){
            logger.error("consumer start error", e);
        }

    }

    public void invoke(AgentRequest agentRequest, ChannelFuture channelFuture) throws Exception {

        ByteBuf buf = agentRequest.getByteBufHolder().content();
        int len = 4 + buf.readableBytes();
        CompositeByteBuf compositeByteBuf = PooledByteBufAllocator.DEFAULT.compositeDirectBuffer();
        compositeByteBuf.writeInt(len);
        compositeByteBuf.writeInt(agentRequest.getId());
        compositeByteBuf.addComponent(true, buf);
        System.out.println(buf.readableBytes());
        System.out.println(compositeByteBuf.readableBytes());
        ReferenceCountUtil.retain(buf);
        Endpoint.writeAndFlush(channelFuture, compositeByteBuf);
    }

    public ConnectManager getConnectManager() {
        return connectManager;
    }
}
