package com.alibaba.dubbo.performance.agent;

import com.alibaba.dubbo.performance.agent.launcher.consumer.Consumer;
import com.alibaba.dubbo.performance.agent.launcher.provider.Provider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.IntStream;

@SpringBootApplication
public class AgentApp {

    public static void main(String[] args) throws Exception{
        String type = System.getProperty("type");   // 获取type参数
        if ("provider".equals(type)){
            Provider.init();
        } else{
            Consumer.init();
            SpringApplication.run(AgentApp.class,args);
        }
    }

}
