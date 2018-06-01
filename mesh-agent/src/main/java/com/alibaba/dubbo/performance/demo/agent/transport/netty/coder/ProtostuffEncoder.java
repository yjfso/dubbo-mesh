package com.alibaba.dubbo.performance.demo.agent.transport.netty.coder;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.*;
import java.util.List;

public class ProtostuffEncoder extends MessageToByteEncoder {

    private static Closer closer = Closer.create();
    private final Schema schema;

    public ProtostuffEncoder(final Class<?> cls) {
        this.schema = RuntimeSchema.createFrom(cls);
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object message, final ByteBuf out) throws Exception {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
//            closer.register(byteArrayOutputStream);
            serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
//            closer.close();
        }
    }

    public void serialize(OutputStream output, Object object) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            ProtostuffIOUtil.writeTo(output, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }
}
