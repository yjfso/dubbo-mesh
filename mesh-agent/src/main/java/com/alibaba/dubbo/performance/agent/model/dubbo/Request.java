package com.alibaba.dubbo.performance.agent.model.dubbo;


import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class Request {
//    private static AtomicLong atomicLong = new AtomicLong();

    private final static Logger log = LoggerFactory.getLogger(Request.class);
    private long id;
    private boolean twoWay = true;
    private boolean event = false;
    private ChannelHandlerContext ctx;

    private Object mData;

    public Request(){

    }

    public Request init(){
//        id = atomicLong.getAndIncrement();
        this.setTwoWay(true);
        return this;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

    private void release(){
        try{
            ctx = null;
            DubboClient.pool.returnObject(this);
        } catch (Exception e){
            log.error("return request error", e);
        }
    }

    public void setResponseData(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }

    public void done(byte[] result){
//        int length = result.length;
//        byte[] response = new byte[8 + length];
//        Bytes.long2bytes(id, response, 0);
//        System.arraycopy(source, 0, response, 0, 8);
//        System.arraycopy(result, 0, response, 8, length);
        ctx.writeAndFlush(result);
        release();
    }

}
