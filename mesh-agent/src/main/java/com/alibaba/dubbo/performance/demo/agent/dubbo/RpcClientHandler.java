package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;

import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<AgentResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, AgentResponse response) {
        long requestId = response.getRequestId();
        RpcFuture future = AgentRequestHolder.get(requestId);
        if(null != future){
            AgentRequestHolder.remove(requestId);
            future.done(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
