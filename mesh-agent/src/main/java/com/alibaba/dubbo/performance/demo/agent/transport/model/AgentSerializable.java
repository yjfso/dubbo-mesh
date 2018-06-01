package com.alibaba.dubbo.performance.demo.agent.transport.model;

public interface AgentSerializable {

    byte[] toBytes();

    AgentSerializable fromBytes(byte[] bytes);

}
