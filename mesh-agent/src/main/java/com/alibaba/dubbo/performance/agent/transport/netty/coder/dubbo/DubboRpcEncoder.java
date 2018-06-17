package com.alibaba.dubbo.performance.agent.transport.netty.coder.dubbo;

import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.model.DubboRequest;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;
import io.netty.buffer.*;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.Buffer;

@ChannelHandler.Sharable
public class DubboRpcEncoder extends ChannelOutboundHandlerAdapter {


    public static final DubboRpcEncoder INSTANCE = new DubboRpcEncoder();
    // header length.
    private static final int HEADER_LENGTH = 16;
    // magic header.
    private static final short MAGIC = (short) 0xdabb;
    // message flag.
    private static final byte FLAG_REQUEST = (byte) 0x80;
    private static final byte FLAG_TWOWAY = (byte) 0x40;
    private static final byte FLAG_EVENT = (byte) 0x20;
    private static final byte FASTJSON = 6;


    private static ByteBuf requestHeader;
    private static ByteBuf eventHeader;
    static {
        byte eventH2 = FLAG_REQUEST | FASTJSON | FLAG_EVENT;
        byte requestH2 = FLAG_REQUEST | FASTJSON | FLAG_TWOWAY;

        requestHeader = PooledByteBufAllocator.DEFAULT.directBuffer(HEADER_LENGTH);
        requestHeader.writeLong(0).writeLong(0);
        requestHeader.setShort(0, MAGIC);
        requestHeader.setByte(2, requestH2);

        eventHeader = requestHeader.copy();
        eventHeader.setByte(2, eventH2);

    }
    private ByteBuf getRequestHeader(ChannelHandlerContext ctx){
        return requestHeader.copy();
    }

    private ByteBuf getEventHeader(ChannelHandlerContext ctx){
        return eventHeader.copy();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        DubboRequest req = (DubboRequest)msg;
        ByteBuf result;

        if (req.isEvent()){
            result = getEventHeader(ctx);
        } else {
            CompositeByteBuf requestByteBuf = ctx.alloc().compositeDirectBuffer(2);
            requestByteBuf.addComponent(true, getRequestHeader(ctx));
            requestByteBuf.setInt(8, req.getId());

            encodeRequestData(req.getData(), requestByteBuf);
            req.setData(null);
            int len = requestByteBuf.readableBytes() - HEADER_LENGTH;
            requestByteBuf.setInt(12, len);

            result = requestByteBuf;
        }
        ctx.write(result, promise);
    }


    private void encodeRequestData(Object data, ByteBuf byteBuf) throws Exception {
        RpcInvocation inv = (RpcInvocation)data;

        writeString(byteBuf, Const.DUBBO_VERSION);
        writeString(byteBuf, inv.getInterfaceName());
        writeNull(byteBuf);
        writeString(byteBuf, inv.getMethodName());
        writeString(byteBuf, inv.getParameterTypes());//inv.getParameterTypes());
        writeString(byteBuf, inv.getArguments());
        writeVoidJson(byteBuf);

//        int index = byteBuf.readerIndex();
//        int reaaAble = byteBuf.readableBytes();
//        byte[] result = new byte[reaaAble];
//        byteBuf.readBytes(result);
//        System.out.println(new String(result).replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", ""));
//        byteBuf.readerIndex(index);

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
