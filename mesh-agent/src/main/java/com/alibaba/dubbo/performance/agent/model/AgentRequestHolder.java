package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;

import java.util.concurrent.ConcurrentHashMap;

public class AgentRequestHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<Long,RpcFuture> processingRpc = new ConcurrentHashMap<>();

    public static void put(Long requestId,RpcFuture rpcFuture){
        processingRpc.put(requestId,rpcFuture);
    }

    public static RpcFuture get(Long requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(Long requestId){
        processingRpc.remove(requestId);
    }
}
