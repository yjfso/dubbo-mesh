package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class AgentEncoder extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int readableBytes = byteBuf.readableBytes();
        CompositeByteBuf result = ctx.alloc().compositeDirectBuffer(2);
        result.capacity(4);
        result.writeInt(readableBytes);
        result.addComponent(true, byteBuf);
//        byteBuf.retain();

        ctx.write(result, promise);
    }
}
