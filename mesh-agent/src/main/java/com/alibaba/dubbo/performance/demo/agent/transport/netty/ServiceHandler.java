package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.dubbo.performance.demo.agent.transport.model.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ServiceHandler extends ChannelInboundHandlerAdapter {

    private RpcClient rpcClient = new RpcClient();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        Request request = (Request) msg;

        Object result = rpcClient.invoke(request.getInterfaceName(),
                request.getMethod(), request.getParameterTypesString() ,request.getParameter());

        RpcResponse response = new RpcResponse();
        response.setRequestId(String.valueOf(request.getId()));
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
