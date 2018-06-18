package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import com.alibaba.dubbo.performance.agent.common.Const;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChannelFutureWriter {

    private final static FastThreadLocal<Map<ChannelFuture, ChannelFutureWriter>> INSTANCES = new FastThreadLocal<>();
    private ChannelFuture channelFuture;
    private int pending = 0;
    private final static Map<ChannelFuture, ChannelFutureWriter> channelWriterMap = new HashMap<>();
    private final static Object lock = new Object();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(
                () -> channelWriterMap.values().stream().filter(
                        item -> item.pending>0
                ).forEach(ChannelFutureWriter::flushDelay),
                Const.SMART_WRITER_INTERVAL,
                Const.SMART_WRITER_INTERVAL,
                TimeUnit.MILLISECONDS
        );
    }

    private ChannelFutureWriter(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public void smartWrite(Object object){
        if(channelFuture.isDone()){
            channelFuture.channel().write(object, channelFuture.channel().voidPromise());
        } else{
            channelFuture.addListener(
                    new ChannelFutureListener(){
                        @Override
                        public void operationComplete(ChannelFuture cf) throws Exception {
                            cf.channel().write(object, cf.channel().voidPromise());
                            channelFuture.removeListener(this);
                        }
                    }
            );
        }
        if (pending ++ >= 30){
            pending = 0;
            channelFuture.channel().flush();
        }
    }

    private void flushDelay(){
        channelFuture.channel().eventLoop().submit(this::flush);
    }

    private void flush(){
        if (pending > 0){
            pending = 0;
            channelFuture.channel().flush();
        }
    }

    public ChannelFuture getCtx() {
        return channelFuture;
    }

    public void setCtx(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public static void removeInstance(ChannelFuture channelFuture) {
        INSTANCES.get().remove(channelFuture);
        synchronized (lock){
            channelWriterMap.remove(channelFuture);
        }
    }
    public static void putInstance(ChannelFuture ctx){
        Map<ChannelFuture, ChannelFutureWriter> instances = INSTANCES.get();
        if (instances == null) {
            instances = new HashMap<>();
            INSTANCES.set(instances);
        }
        ChannelFutureWriter channelWriter = new ChannelFutureWriter(ctx);
        synchronized (lock){
            channelWriterMap.put(ctx, channelWriter);
        }
        instances.put(ctx, channelWriter);
    }

    public static ChannelFutureWriter getInstance(ChannelFuture ctx){
        return INSTANCES.get().get(ctx);
    }

}
