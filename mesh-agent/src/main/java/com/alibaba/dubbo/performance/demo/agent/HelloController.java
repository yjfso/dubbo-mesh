package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    public HelloController() throws Exception {
    }

    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        AgentRequest agentRequest = new AgentRequest(interfaceName, method, parameterTypesString, parameter);
        byte[] bytes = (byte[]) Client.SingletonHolder.INSTANCE.invoke(agentRequest);
        String s = new String(bytes, 8, bytes.length-8);
        return Integer.valueOf(s);
    }

}
