package com.alibaba.dubbo.performance.agent.model;


import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.launcher.consumer.ConsumerHandler;
import com.alibaba.dubbo.performance.agent.util.ByteBufUtil;
import com.alibaba.dubbo.performance.agent.util.objectPool.SimpleObjectPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DubboRequest extends AbstractRequest {

    private final static Logger log = LoggerFactory.getLogger(DubboRequest.class);
    private int agentRequestId;
    private byte[] agentRequest;
    private boolean twoWay = true;
    private boolean event = false;
    private Object mData;

    private static FastThreadLocal<SimpleObjectPool<DubboRequest>> privateDubboRequest = new FastThreadLocal<>();


    public static SimpleObjectPool<DubboRequest> getPool() throws Exception{
        SimpleObjectPool<DubboRequest> pool = privateDubboRequest.get();
        if (pool == null){
            pool = new SimpleObjectPool<>(Const.DUBBO_REQUEST_NUM, new DubboRequestFactory());
            privateDubboRequest.set(pool);
        }
        return pool;
    }

    public void returnSelf() throws Exception{
        super.returnSelf();
        agentRequest = null;
        agentRequestId = -1;
        getPool().returnObject(this);
    }

    public static DubboRequest getDubboRequest() throws Exception{
        return getPool().borrowObject();
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

    public void setAgentRequest(ByteBuf byteBuf){
        agentRequestId = byteBuf.readInt();
        int length = byteBuf.readableBytes();
        this.agentRequest = new byte[length];
        byteBuf.readBytes(this.agentRequest);
        ReferenceCountUtil.release(byteBuf);
    }

    public void done(ByteBuf byteBuf) throws Exception {

        byteBuf.setInt(0, agentRequestId);

        getCtx().writeAndFlush(byteBuf);
        returnSelf();
    }

    public byte[] getAgentRequest() {
        return agentRequest;
    }
}
