package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class AgentRequestEncoder extends MessageToMessageEncoder<ByteBuf> {

//    @Override
//    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
//        AgentRequest agentRequest = (AgentRequest) message;
//        ByteBufHolder byteBufHolder = agentRequest.getByteBufHolder();
//        try {
//            ByteBuf byteBuf = byteBufHolder.content();
//            int length = 4 + byteBuf.readableBytes();
//            out.writeInt(length);
//            int id = agentRequest.getId();
//            out.writeInt(id);
//            out.writeBytes(byteBuf);
//        } finally {
//            ReferenceCountUtil.release(byteBufHolder);
//        }
//    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        ReferenceCountUtil.retain(byteBuf);
        list.add(byteBuf);
    }
}