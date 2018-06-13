package com.alibaba.dubbo.performance.agent.model;


import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.model.dubbo.RequestFactory;
import com.alibaba.dubbo.performance.agent.util.ObjectPoolUtils;
import com.alibaba.dubbo.performance.agent.util.objectPool.SimpleObjectPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DubboRequest extends AbstractRequest {

    private final static Logger log = LoggerFactory.getLogger(DubboRequest.class);
    private byte[] agentRequest;
    private boolean twoWay = true;
    private boolean event = false;
    private Object mData;

    private static FastThreadLocal<SimpleObjectPool<DubboRequest>> privateAgentRequest = new FastThreadLocal<>();


    public static SimpleObjectPool<DubboRequest> getPool() throws Exception{
        SimpleObjectPool<DubboRequest> pool = privateAgentRequest.get();
        if (pool == null){
            pool = new SimpleObjectPool<>(Const.AGENT_REQUEST_NUM, new DubboRequestFactory());
            privateAgentRequest.set(pool);
        }
        return pool;
    }

    public void returnSelf() throws Exception{
        super.returnSelf();
        agentRequest = null;
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

    public void setAgentRequest(byte[] bytes){
        this.agentRequest = bytes;
    }

    public void done(byte[] result) throws Exception {
        System.arraycopy(agentRequest, 0, result, 0, 4);
        getCtx().writeAndFlush(result);
        returnSelf();
    }

}
