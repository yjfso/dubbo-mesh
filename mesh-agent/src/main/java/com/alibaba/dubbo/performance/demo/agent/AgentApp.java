package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {

    static IRegistry etcdRegistry = EtcdRegistry.registry;

    public static void main(String[] args) throws Exception{
        String type = System.getProperty("type");   // 获取type参数
        if ("provider".equals(type)){
            (new Server()).init();
        } else{
            SpringApplication.run(AgentApp.class,args);
        }
    }
}
