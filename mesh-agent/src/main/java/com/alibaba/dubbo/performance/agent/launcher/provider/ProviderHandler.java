package com.alibaba.dubbo.performance.agent.launcher.provider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    private Provider provider;
    private final static Logger log = LoggerFactory.getLogger(ProviderHandler.class);

    ProviderHandler(Provider provider){
        this.provider = provider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        provider.providerExecutor.submit(()->{
            try{
                byte[] bytes = (byte[]) msg;
                Provider.dubboClient.invoke(bytes, ctx);

            } catch (Exception e){
                log.error("provider handler error", e);
            }
        });

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

//    @Override
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
//    }
}
