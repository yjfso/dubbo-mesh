package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;

import com.alibaba.dubbo.performance.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboClientHandler extends SimpleChannelInboundHandler<AgentResponse> {


    private final static Logger log = LoggerFactory.getLogger(DubboClientHandler.class);
    private DubboClient dubboClient;

    public DubboClientHandler(DubboClient dubboClient){
        this.dubboClient = dubboClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getRequestId();
        RpcFuture future = AgentRequestHolder.get(requestId);
        if(null != future){
            future.done(response);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("dubbo client channel inactive");
        dubboClient.connectManager.removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
