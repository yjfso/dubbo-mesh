package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.model.AgentSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class AgentEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        try {
            byte[] body = (byte[]) message;//((AgentSerializable) message).toBytes();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } catch (Exception e){
            throw e;
        }
    }
}