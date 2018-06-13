package com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo;

import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.model.DubboRequest;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

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
        DubboRequest req = (DubboRequest)msg;

        // header.
        byte[] header = getNewHeader();

        int len = 0;
        if (req.isTwoWay()) header[2] |= FLAG_TWOWAY;
        if (req.isEvent()){
            header[2] |= FLAG_EVENT;
            buffer.writeBytes(header);
        } else {
            // set request id.
            Bytes.int2bytes(req.getId(), header, 8);

            ByteBuf byteBuf = encodeRequestData(req.getData());

            len = byteBuf.readableBytes();

            Bytes.int2bytes(len, header, 12);
            buffer.writeBytes(header);
            buffer.writeBytes(byteBuf);
        }
    }


    public ByteBuf encodeRequestData(Object data) throws Exception {
        RpcInvocation inv = (RpcInvocation)data;

        ByteBuf byteBuf = Unpooled.buffer();
        writeString(byteBuf, Const.DUBBO_VERSION);
        writeString(byteBuf, inv.getInterfaceName());
        writeNull(byteBuf);
        writeString(byteBuf, inv.getMethodName());
        writeString(byteBuf, inv.getParameterTypes());//inv.getParameterTypes());
        writeString(byteBuf, inv.getArguments());
        writeVoidJson(byteBuf);

        return byteBuf;
//        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
////        out.write();
//
//        JsonUtils.writeObject(inv.getAttachment("dubbo", "2.0.1"), writer);
//        JsonUtils.writeObject(inv.getInterfaceName(), writer);
//        JsonUtils.writeObject(inv.getAttachment("version"), writer);
//        JsonUtils.writeObject(inv.getMethodName(), writer);
//        JsonUtils.writeObject(inv.getParameterTypes(), writer);
//
//        JsonUtils.writeObject(inv.getArguments(), writer);
//        JsonUtils.writeObject(inv.getAttachments(), writer);
    }

    private void writeNull(ByteBuf byteBuf){
        byteBuf.writeBytes(Const.NULL);
        byteBuf.writeByte(Const.CR);
    }
    private void writeVoidJson(ByteBuf byteBuf){
        byteBuf.writeBytes(Const.VOID_JSON);
        byteBuf.writeByte(Const.CR);
    }

    private void writeString(ByteBuf byteBuf, byte[] bytes){
        byteBuf.writeByte(Const.QUOTA);
        byteBuf.writeBytes(bytes);
        byteBuf.writeByte(Const.QUOTA);
        byteBuf.writeByte(Const.CR);
    }

}
