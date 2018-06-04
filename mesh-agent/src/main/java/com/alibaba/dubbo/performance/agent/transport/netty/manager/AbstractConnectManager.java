package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConnectManager implements ConnectManager {

    Bootstrap bootstrap;

    List<Endpoint> endpoints = new ArrayList<>();

    int i;

    public Endpoint getEndpoint() throws Exception {
//        return endpoints.get(0);
//        if (endpoints.isEmpty()){
//            throw new Exception("lack of endpoint");
//        }
//
        Iterator<Endpoint> iterator = endpoints.iterator();
        if(!iterator.hasNext()){
            throw new Exception("lack of endpoint");
        }
        Endpoint min = iterator.next();
        while (iterator.hasNext()){
            Endpoint endpoint = iterator.next();
            if (min.nowRequestNum * endpoint.weight > endpoint.nowRequestNum * min.weight){
                min = endpoint;
            }
        }
//        System.out.println("route to "+ min + "\n-----------------------------");
        return min;
    }

    abstract void initBootstrap();

    @Override
    public Bootstrap getBootstrap() {
        return bootstrap;
    }
}
