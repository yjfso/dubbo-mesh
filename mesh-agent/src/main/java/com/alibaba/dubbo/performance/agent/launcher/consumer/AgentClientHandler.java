package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;
import com.alibaba.dubbo.performance.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AgentClientHandler extends ChannelInboundHandlerAdapter {

    private AgentClient agentClient;

    public AgentClientHandler(AgentClient agentClient){
        this.agentClient = agentClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        AgentResponse agentResponse = new AgentResponse().fromBytes((byte[]) msg);
        long id = agentResponse.getRequestId();
        RpcFuture future = AgentRequestHolder.get(id);
        if(null != future){
            future.done(agentResponse);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        agentClient.getConnectManager().removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
