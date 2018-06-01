package com.alibaba.dubbo.performance.demo.agent.transport.netty.coder;


import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AgentEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        byte[] body = ((AgentSerializable) message).toBytes();
        int dataLength = body.length;
        out.writeInt(dataLength);
        out.writeBytes(body);
    }
}