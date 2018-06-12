package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

public class AgentEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        AgentRequest agentRequest = (AgentRequest) message;
        ByteBufHolder byteBufHolder = agentRequest.getByteBufHolder();
        try {
            ByteBuf byteBuf = byteBufHolder.content();
            int length = 4 + byteBuf.readableBytes();
            out.writeInt(length);
            int id = agentRequest.getId();
            out.writeInt(id);
            out.writeBytes(byteBuf);
//            byte[] body = (byte[]) message;//((AgentSerializable) message).toBytes();
//            int dataLength = body.length;
//            out.writeInt(dataLength);
//            out.writeBytes(body);
//            out
        } catch (Exception e){
            throw e;
        } finally {
            ReferenceCountUtil.release(byteBufHolder);
        }
    }
}