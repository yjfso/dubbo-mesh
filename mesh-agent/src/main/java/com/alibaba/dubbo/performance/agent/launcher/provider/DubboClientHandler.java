package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.dubbo.Request;

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
        Provider.INSTANCE.providerExecutor.submit(
                ()->{
                    int requestId = Bytes.bytes2int(response, 0);
                    Request request = Request.requests[requestId];
                    if(null != request){
                        request.done(response);
                    }
                }
        );
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("dubbo client userEventTriggered");
        if (evt instanceof IdleStateEvent) {
            Request request = new Request().init();
            request.setTwoWay(false);
            request.setEvent(true);
            ctx.writeAndFlush(request);
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
