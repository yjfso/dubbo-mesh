package com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {

    private final static Logger log = LoggerFactory.getLogger(DubboRpcDecoder.class);
    private static final int HEADER_LENGTH = 16;

    private static final byte FLAG_EVENT = (byte) 0x20;


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
//        try {
            do {
                Object msg = decode2(byteBuf, channelHandlerContext);
                if (msg == null) {
                    break;
                }
                list.add(msg);
            } while (byteBuf.isReadable());
//        } finally {
//            if (byteBuf.isReadable()) {
//                byteBuf.discardReadBytes();
//            }
//        }

        //list.add(decode2(byteBuf));
    }

    private Object decode2(ByteBuf byteBuf, ChannelHandlerContext channelHandlerContext){
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return null;
        }

        int savedReaderIndex = byteBuf.readerIndex();
        int len = byteBuf.getInt(savedReaderIndex + 12);

        if (readable < len + HEADER_LENGTH) {
            return null;
        }

        byte event = byteBuf.getByte(savedReaderIndex + 2);
        Object object = null;
        if ((event & FLAG_EVENT) != 32){
            CompositeByteBuf compositeByteBuf = channelHandlerContext.alloc().compositeDirectBuffer(3);
            ByteBuf idBuf = byteBuf.slice(savedReaderIndex + 8, 4);
            ByteBuf contentBuf = byteBuf.slice(savedReaderIndex + HEADER_LENGTH + 2, len - 3);

            compositeByteBuf.addComponents(true, idBuf, contentBuf);
            byteBuf.retain();
            byteBuf.retain();
            object = compositeByteBuf;
        }

        byteBuf.readerIndex(savedReaderIndex + len + HEADER_LENGTH);

        return object;
    }

}
