package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.ChannelWriter;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.objectPool.AbstractPoolObject;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest extends AbstractPoolObject {

    protected ChannelWriter channelWriter;
    private Endpoint endpoint;

    public ChannelHandlerContext getCtx() {
        return channelWriter.getCtx();
    }

    public ChannelWriter getChannelWriter() {
        return channelWriter;
    }

    public void setChannelWriter(ChannelHandlerContext ctx) {
        this.channelWriter = ChannelWriter.INSTANCES.get(ctx);
    }

    protected void returnSelf() throws Exception{
        channelWriter = null;
        if (endpoint != null){
            endpoint.returnChannel();
            endpoint = null;
        }
    }

    public void setEndpoint(Endpoint endpoint){
        this.endpoint = endpoint;
    }

}
