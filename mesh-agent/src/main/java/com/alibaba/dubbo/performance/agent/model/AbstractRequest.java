package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.objectPool.AbstractPoolObject;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequest extends AbstractPoolObject {

    private ChannelHandlerContext ctx;
    private Endpoint endpoint;
    private List<String> logg = new ArrayList<>();

    public ChannelHandlerContext getCtx() {
        if (ctx == null){

        }
        return ctx;
    }

    public void addLog(String s){
        logg.add(s);
    }
    public void printLog(){
        StringBuffer s = new StringBuffer();
        s.append(getId() + "----------------\n");
        logg.forEach(
                item -> {
                    s.append(item);
                    s.append("\n");
                }
        );
        s.append("--------------------");
        System.out.println(s.toString());
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
