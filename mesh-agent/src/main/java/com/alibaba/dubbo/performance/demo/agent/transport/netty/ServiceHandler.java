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
//        ByteBuf result = (ByteBuf) msg;
//        byte[] result1 = new byte[result.readableBytes()];
//        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
//        result.readBytes(result1);
//        String resultStr = new String(result1);
//        // 接收并打印客户端的信息
//        System.out.println("Client said:" + resultStr);
//        // 释放资源，这行很关键
//        result.release();
//
//        // 向客户端发送消息
//        String response = "I am ok!";
//        // 在当前场景下，发送的数据必须转换成ByteBuf数组
//        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
//        encoded.writeBytes(response.getBytes());
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
