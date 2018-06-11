package com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo;

import com.alibaba.dubbo.performance.agent.util.Bytes;
import com.alibaba.dubbo.performance.agent.util.JsonUtils;
import com.alibaba.dubbo.performance.agent.model.dubbo.Request;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DubboRpcEncoder extends MessageToByteEncoder{


    // header length.
    protected static final int HEADER_LENGTH = 16;
    // magic header.
    protected static final short MAGIC = (short) 0xdabb;
    // message flag.
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;
    private final static byte[] headerBase = new byte[HEADER_LENGTH];

    static {
        Bytes.short2bytes(MAGIC, headerBase);
        headerBase[2] = (byte) (FLAG_REQUEST | 6);
    }

    private byte[] getNewHeader(){
        byte[] header = new byte[HEADER_LENGTH];
        System.arraycopy(headerBase, 0, header, 0, 3);
        return header;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buffer) throws Exception {
        Request req = (Request)msg;

        // header.
        byte[] header = getNewHeader();

        int savedWriteIndex = buffer.writerIndex();
        int len = 0;
        if (req.isTwoWay()) header[2] |= FLAG_TWOWAY;
        if (req.isEvent()){
            header[2] |= FLAG_EVENT;
        } else {
            // set request id.
            Bytes.long2bytes(req.getId(), header, 4);
            // encode request data.
            buffer.writerIndex(savedWriteIndex + HEADER_LENGTH);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            encodeRequestData(bos, req.getData());

            len = bos.size();
            buffer.writeBytes(bos.toByteArray());
            Bytes.int2bytes(len, header, 12);

        }

        // write
        buffer.writerIndex(savedWriteIndex);
        buffer.writeBytes(header); // write header.
        buffer.writerIndex(savedWriteIndex + HEADER_LENGTH + len);
    }


    public void encodeRequestData(OutputStream out, Object data) throws Exception {
        RpcInvocation inv = (RpcInvocation)data;

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
//        out.write();

        JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
        JsonUtils.writeObject(inv.getAttachment("path"), writer);
        JsonUtils.writeObject(inv.getAttachment("version"), writer);
        JsonUtils.writeObject(inv.getMethodName(), writer);
        JsonUtils.writeObject(inv.getParameterTypes(), writer);

        JsonUtils.writeObject(inv.getArguments(), writer);
        JsonUtils.writeObject(inv.getAttachments(), writer);
    }

}
