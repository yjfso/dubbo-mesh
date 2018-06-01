package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractConnectManager implements ConnectManager {

    Bootstrap bootstrap;

    List<Endpoint> endpoints;

    public Endpoint getEndpoint() throws Exception {
//        return endpoints.get(0);
//        if (endpoints.isEmpty()){
//            throw new Exception("lack of endpoint");
//        }
//
        Integer minNum = 9999;
        Endpoint endpoint = null;
        for (Endpoint item : endpoints) {
            int requestNum = item.getRequestNum();
            if( requestNum < minNum){
                minNum = requestNum;
                endpoint = item;
            }
        }
        return endpoint;
    }

    abstract void initBootstrap();


}
