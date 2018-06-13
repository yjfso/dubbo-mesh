package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.objectPool.AbstractPoolObject;
import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractRequest extends AbstractPoolObject {

    private ChannelHandlerContext ctx;
    private Endpoint endpoint;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    protected void returnSelf() throws Exception{
        setCtx(null);
        if (endpoint != null){
            endpoint.returnChannel();
            endpoint = null;
        }
    }

    public void setEndpoint(Endpoint endpoint){
        this.endpoint = endpoint;
    }

}
