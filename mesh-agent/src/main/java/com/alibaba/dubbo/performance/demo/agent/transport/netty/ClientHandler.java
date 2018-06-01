package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        AgentResponse agentResponse = new AgentResponse().fromBytes((byte[]) msg);
        long id = agentResponse.getRequestId();
        RpcFuture future = AgentRequestHolder.get(id);
        if(null != future){
            AgentRequestHolder.remove(id);
            future.done(agentResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
