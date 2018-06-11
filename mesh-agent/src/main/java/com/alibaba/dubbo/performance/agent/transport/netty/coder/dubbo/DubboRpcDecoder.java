package com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo;

import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;

    protected static final byte FLAG_EVENT = (byte) 0x20;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                Object msg;
                try {
                    msg = decode2(byteBuf);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == DecodeResult.NEED_MORE_INPUT) {
                    byteBuf.readerIndex(savedReaderIndex);
                    break;
                }

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }

        //list.add(decode2(byteBuf));
    }

    public enum DecodeResult {
        NEED_MORE_INPUT, SKIP_INPUT
    }

    private Object decode2(ByteBuf byteBuf){

        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        byte[] header = new byte[HEADER_LENGTH];
        byteBuf.readBytes(header);
        byte[] dataLen = Arrays.copyOfRange(header,12,16);
        int len = Bytes.bytes2int(dataLen);
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }

//
        byte[] response = new byte[4 + len - 3];
        System.arraycopy(header, 8, response, 0, 4);
        int readerIndex = byteBuf.readerIndex();
        byteBuf.readerIndex(readerIndex + 2);
        byteBuf.readBytes(response, 4, len - 3);
        byteBuf.readerIndex(readerIndex + len);


        return response;
    }
}
