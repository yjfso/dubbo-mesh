package com.alibaba.dubbo.performance.demo.agent.transport.netty.coder;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.dyuproject.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProtostuffDecoder extends ByteToMessageDecoder {

    int MESSAGE_LENGTH = 4;
    private static Closer closer = Closer.create();
    private final Schema schema;
    private static Class<?> cls;
    private static Objenesis objenesis = new ObjenesisStd(true);

    public ProtostuffDecoder(final Class<?> cls) {
        this.cls = cls;
        this.schema = RuntimeSchema.createFrom(cls);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < MESSAGE_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();

        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object obj = decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
            }
        }
    }

    private Object decode(byte[] body) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body)) {
            closer.register(byteArrayInputStream);
            Object obj = deserialize(byteArrayInputStream);
            return obj;
        } finally {
//            closer.close();
        }
    }

    public Object deserialize(InputStream input) {
        try {
            Object message = objenesis.newInstance(cls);
            ProtostuffIOUtil.mergeFrom(input, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
