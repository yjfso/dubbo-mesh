package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

public class AgentResponseEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        byte[] body = (byte[]) message;//((AgentSerializable) message).toBytes();
        int dataLength = body.length;
        out.writeInt(dataLength);
        out.writeBytes(body);
    }
}