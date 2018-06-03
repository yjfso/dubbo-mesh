package com.alibaba.dubbo.performance.agent.transport.netty.coder;


import com.alibaba.dubbo.performance.agent.model.AgentSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ByteEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        byte[] body = (byte[]) message;
        int dataLength = body.length;
        out.writeInt(dataLength);
        out.writeBytes(body);
    }
}