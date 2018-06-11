package com.alibaba.dubbo.performance.agent.model.dubbo;


import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.util.Bytes;
import com.alibaba.dubbo.performance.agent.util.ObjectPoolUtils;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Request {
//    private static AtomicLong atomicLong = new AtomicLong();

    public final static ObjectPool<Request> pool = new GenericObjectPool<>(new RequestFactory(), ObjectPoolUtils.getConfig(Const.DUBBO_REQUEST_NUM));
    public final static Request[] requests = new Request[Const.DUBBO_REQUEST_NUM];

    private final static AtomicInteger atomicInteger = new AtomicInteger();
    private final static Logger log = LoggerFactory.getLogger(Request.class);
    private int id;
    private byte[] agentRequest;
    private boolean twoWay = true;
    private boolean event = false;
    private ChannelHandlerContext ctx;

    private Object mData;

    public Request(){
        id = atomicInteger.getAndIncrement();
        requests[id] = this;
    }

    public Request init(){
//        id = atomicLong.getAndIncrement();
//        this.setTwoWay(true);
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public static Request getRequest() throws Exception{
        return pool.borrowObject();
    }
    public void setAgentRequest(byte[] bytes){
        this.agentRequest = bytes;
    }

    private void release(){
        try{
            agentRequest = null;
            ctx = null;
            pool.returnObject(this);
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
        System.arraycopy(agentRequest, 0, result, 0, 4);
        ctx.writeAndFlush(result);
        release();
    }

}
