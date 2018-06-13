package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        try {
            do {
                try {
                    byteBuf.markReaderIndex();
                    int readable = byteBuf.readableBytes();
                    if (readable < 4) {
                        byte[] b=  new byte[readable];
                        byteBuf.readBytes(b);
                        System.out.println(b);
                        byteBuf.resetReaderIndex();
                        break;
                    }

                    int messageLength = byteBuf.readInt();

                    if (readable < messageLength + 4) {
                        byteBuf.resetReaderIndex();
                        break;
                    }

                    byte[] messageBody = new byte[messageLength];
                    byteBuf.readBytes(messageBody);
                    out.add(messageBody);
                } catch (Exception e) {
                    throw e;
                }

            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }
    }

}