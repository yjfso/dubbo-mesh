package com.alibaba.dubbo.performance.demo.agent.transport.netty.manager;


public interface ConnectManager {

    Endpoint getEndpoint() throws Exception;
}
