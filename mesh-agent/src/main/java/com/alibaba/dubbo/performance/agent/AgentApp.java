package com.alibaba.dubbo.performance.agent;

import com.alibaba.dubbo.performance.agent.launcher.consumer.AgentClient;
import com.alibaba.dubbo.performance.agent.launcher.provider.Provider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgentApp {

    public static void main(String[] args) throws Exception{
        String type = System.getProperty("type");   // 获取type参数
        if ("provider".equals(type)){
            Provider.init();
        } else{
            AgentClient.init();
            SpringApplication.run(AgentApp.class,args);
        }
    }

}
