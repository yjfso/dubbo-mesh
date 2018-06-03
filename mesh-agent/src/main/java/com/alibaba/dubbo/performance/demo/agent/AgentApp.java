package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.transport.netty.Client;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {

    public static void main(String[] args) throws Exception{
        String type = System.getProperty("type");   // 获取type参数
        if ("provider".equals(type)){
            Server.init();
        } else{
            Client.init();
            SpringApplication.run(AgentApp.class,args);
        }
    }
}
