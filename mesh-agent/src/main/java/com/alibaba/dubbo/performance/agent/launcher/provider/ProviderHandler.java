package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.launcher.consumer.AgentClient;
import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.DubboRequest;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ChannelWriter;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yinjianfeng on 18/5/27.
 */
@ChannelHandler.Sharable
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(ProviderHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Endpoint endpoint = Provider.dubboClient.getConnectManager().getEndpoint();
        ChannelFuture channelFuture = endpoint.getChannelFuture(ctx);
        DubboRequest dubboRequest = DubboRequest.getDubboRequest();
        dubboRequest.setEndpoint(endpoint);
        dubboRequest.setChannelWriter(ctx);
        ByteBuf byteBuf = (ByteBuf) msg;
        dubboRequest.setAgentRequest(byteBuf);

        try{
            Provider.dubboClient.invoke(dubboRequest, channelFuture);
        } catch (Exception e){
            dubboRequest.returnSelf();
            log.error("provider handler error", e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelWriter.putInstance(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelWriter.removeInstance(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
