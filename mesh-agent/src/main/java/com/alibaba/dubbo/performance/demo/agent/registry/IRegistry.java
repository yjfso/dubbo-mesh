package com.alibaba.dubbo.performance.demo.agent.registry;

import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;

import java.util.List;

public interface IRegistry {

    // 注册服务
    void register(String serviceName, int port) throws Exception;

    void watch(String serviceName, ConnectManager connectManager) throws Exception;
}
