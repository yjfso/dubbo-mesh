package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {

    private RpcClient rpcClient = new RpcClient();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        byte[] bytes = (byte[]) msg;
        AgentRequest request = new AgentRequest().fromBytes(bytes);
        Object result = rpcClient.invoke(request.getInterfaceName(),
                request.getMethod(), request.getParameterTypesString() ,request.getParameter());

        AgentResponse response = new AgentResponse();
        response.setRequestId(request.getId());
        response.setBytes((byte[]) result);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
}
