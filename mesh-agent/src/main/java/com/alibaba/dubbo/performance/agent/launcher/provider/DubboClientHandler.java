package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.DubboRequest;

import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class DubboClientHandler extends ChannelInboundHandlerAdapter {


    private final static Logger log = LoggerFactory.getLogger(DubboClientHandler.class);
    public final static DubboClientHandler INSTANCE = new DubboClientHandler();

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object response) {
        try{
            if (response==null){
                DubboRequest dubboRequest = new DubboRequest();
                dubboRequest.setEvent(true);
                channelHandlerContext.writeAndFlush(dubboRequest, channelHandlerContext.voidPromise());
            } else {
                ByteBuf byteBuf = (ByteBuf)response;

                int requestId = byteBuf.getInt(0);
                DubboRequest dubboRequest = DubboRequest.getPool().get(requestId);
                if(null != dubboRequest){
                    dubboRequest.done(byteBuf);
                }
            }
        }
        catch (Exception e){
            log.error("dubbo read error", e);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("dubbo client userEventTriggered");
        if (evt instanceof IdleStateEvent) {
            System.out.println("sent heartbeat in userEventTriggered");
            DubboRequest dubboRequest = new DubboRequest();
            dubboRequest.setTwoWay(false);
            dubboRequest.setEvent(true);
            ctx.writeAndFlush(dubboRequest, ctx.voidPromise());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("dubbo client channel inactive");
//        dubboClient.connectManager.removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
