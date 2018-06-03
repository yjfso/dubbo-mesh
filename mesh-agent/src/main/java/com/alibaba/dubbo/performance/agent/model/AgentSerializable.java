package com.alibaba.dubbo.performance.agent.model;

public interface AgentSerializable {

    byte[] toBytes();

    AgentSerializable fromBytes(byte[] bytes);

}
