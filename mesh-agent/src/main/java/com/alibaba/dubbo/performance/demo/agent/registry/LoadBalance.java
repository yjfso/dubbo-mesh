package com.alibaba.dubbo.performance.demo.agent.registry;

import java.util.List;

public class LoadBalance {

    private static List<Endpoint> endpoints = null;

    static {
        try{
            endpoints = EtcdRegistry.registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
        } catch (Exception e){

        }
    }

    public static Endpoint getEndpoint(){
        Integer minNum = 9999;
        Endpoint endpoint = null;
        for (Endpoint item : endpoints) {
            int requestNum = item.getRequestNum();
            if( requestNum < minNum){
                endpoint = item;
            }
        }
        return endpoint;
    }
}
