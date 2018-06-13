package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.DubboRequest;

import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboClientHandler extends SimpleChannelInboundHandler<byte[]> {


    private final static Logger log = LoggerFactory.getLogger(DubboClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] response) {
        try{
            int requestId = Bytes.bytes2int(response, 0);
            if (requestId==-1){
                DubboRequest dubboRequest = new DubboRequest();
                dubboRequest.setTwoWay(false);
                dubboRequest.setEvent(true);
                channelHandlerContext.writeAndFlush(dubboRequest);
            } else {
                DubboRequest dubboRequest = DubboRequest.getPool().get(requestId);
                if(null != dubboRequest){
                    dubboRequest.done(response);
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
            ctx.writeAndFlush(dubboRequest);
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
