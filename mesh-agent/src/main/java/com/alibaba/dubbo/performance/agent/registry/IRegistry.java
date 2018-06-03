package com.alibaba.dubbo.performance.agent.registry;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;

public interface IRegistry {

    // 注册服务
    void register(String serviceName, int port, int weight) throws Exception;

    void watch(String serviceName, ConnectManager connectManager) throws Exception;
}
