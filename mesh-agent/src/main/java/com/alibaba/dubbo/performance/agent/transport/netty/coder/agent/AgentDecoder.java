package com.alibaba.dubbo.performance.agent.transport.netty.coder.agent;


import com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo.DubboRpcDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentDecoder extends ByteToMessageDecoder {

    private final static Logger log = LoggerFactory.getLogger(AgentDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) {
        do {
            try {

                log.info("agent decoder got a message");
                int readable = byteBuf.readableBytes();
                if (readable < 4) {
                    break;
                }
                int readerIndex = byteBuf.readerIndex();
                int messageLength = byteBuf.getInt(readerIndex);
                log.info(readerIndex + "|" + messageLength + "|" + readable);
                if (readable < messageLength + 4) {
                    break;
                }
                ByteBuf subBuf = byteBuf.retainedSlice(readerIndex + 4, messageLength);
                log.info("sub refCnt:" + subBuf.refCnt());
                log.info("raw refCnt:" + byteBuf.refCnt());
                out.add(subBuf);
                byteBuf.readerIndex(readerIndex + messageLength + 4);
            } catch (Exception e) {
                throw e;
            }
        } while (byteBuf.isReadable());

    }

}