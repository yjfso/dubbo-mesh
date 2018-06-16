package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChannelWriter {

    private final static FastThreadLocal<Map<ChannelHandlerContext, ChannelWriter>> INSTANCES = new FastThreadLocal<>();
    private ChannelHandlerContext ctx;
    private int pending = 0;
    private final static Map<ChannelHandlerContext, ChannelWriter> channelWriterMap = new HashMap<>();
    private final static Object lock = new Object();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                ()-> channelWriterMap.values().stream().filter(
                        item -> item.pending>0
                ).forEach(ChannelWriter::flushDelay),
                Const.SMART_WRITER_INTERVAL,
                Const.SMART_WRITER_INTERVAL,
                TimeUnit.MILLISECONDS
        );
    }

    private ChannelWriter(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void smartWrite(ByteBuf byteBuf){
        ctx.write(byteBuf, ctx.voidPromise());
        if (pending ++ >= Const.SMART_WRITER_MAX_BUF){
            pending = 0;
            ctx.flush();
        }
    }

    private void flushDelay(){
        ctx.executor().submit(this::flush);
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
        synchronized (lock){
            channelWriterMap.remove(ctx);
        }
    }
    public static void putInstance(ChannelHandlerContext ctx){
        Map<ChannelHandlerContext, ChannelWriter> instances = INSTANCES.get();
        if (instances == null) {
            instances = new HashMap<>();
            INSTANCES.set(instances);
        }
        ChannelWriter channelWriter = new ChannelWriter(ctx);
        synchronized (lock){
            channelWriterMap.put(ctx, channelWriter);
        }
        instances.put(ctx, channelWriter);
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
