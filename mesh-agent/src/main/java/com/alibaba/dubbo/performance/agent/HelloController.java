package com.alibaba.dubbo.performance.agent;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.launcher.consumer.AgentClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {


    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        AgentRequest agentRequest = (new AgentRequest().initRequest())
                .initData(interfaceName, method, parameterTypesString, parameter);
        byte[] bytes = (byte[]) AgentClient.INSTANCE.invoke(agentRequest);
        String s = new String(bytes, 8, bytes.length-8);
        return Integer.valueOf(s);
    }

}
