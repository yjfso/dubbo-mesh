package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChannelWriter {

    public final static FastThreadLocal<Map<ChannelHandlerContext, ChannelWriter>> INSTANCES = new FastThreadLocal<>();
    private ChannelHandlerContext ctx;
    private final static int MAX_BUF = 300;
    private volatile int pending = 0;
    private Runnable runnable = this::flush;

    private ChannelWriter(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void smartWrite(ByteBuf byteBuf){
        ctx.write(byteBuf, ctx.voidPromise());
        ctx.executor().schedule(runnable, 20, TimeUnit.MILLISECONDS);
        if (pending ++ >= MAX_BUF){
            flush();
        }
    }

    private void flush(){
        if (pending > 0){
            pending = 0;
            ctx.flush();
        }
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public static void removeInstance(ChannelHandlerContext ctx) {
        INSTANCES.get().remove(ctx);
    }
    public static void putInstance(ChannelHandlerContext ctx){
        Map<ChannelHandlerContext, ChannelWriter> instances = INSTANCES.get();
        if (instances == null) {
            instances = new HashMap<>();
            INSTANCES.set(instances);
        }
        instances.put(ctx, new ChannelWriter(ctx));
    }

    public static ChannelWriter getInstance(ChannelHandlerContext ctx){
        return INSTANCES.get().get(ctx);
    }
    public static void writeAndFlush(ChannelFuture channelFuture, Object object){
        if(channelFuture.isDone()){
            channelFuture.channel().writeAndFlush(object, channelFuture.channel().voidPromise());
        } else{
            channelFuture.addListener(
                    new ChannelFutureListener(){
                        @Override
                        public void operationComplete(ChannelFuture cf) throws Exception {
                            cf.channel().writeAndFlush(object, cf.channel().voidPromise());
                            channelFuture.removeListener(this);
                        }
                    }
            );
        }
    }
}
