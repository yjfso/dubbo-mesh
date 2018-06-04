package com.alibaba.dubbo.performance.agent;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.launcher.consumer.Consumer;
import com.alibaba.dubbo.performance.agent.model.AgentRequestFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    private final static ObjectPool<AgentRequest> pool = new GenericObjectPool<>(new AgentRequestFactory());

    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
//        AgentRequest agentRequest = new AgentRequest().initData("interfaceName", "method", "parameterTypesString", "parameterTypesString");
//                agentRequest.init();
        AgentRequest agentRequest = pool.borrowObject()
                .initData(interfaceName, method, parameterTypesString, parameter);
        try{
        } finally {
            pool.returnObject(agentRequest);
        }
//        byte[] bytes = (byte[]) Consumer.INSTANCE.invoke(agentRequest);
//        String s = new String(bytes, 8, bytes.length-8);
        return 1;//Integer.valueOf(s);
    }

    public static void main(String[] args) throws Exception {
        long s, e;
        for(int i=0;i<6;i++){
            s = System.nanoTime();
            for (int j=0;j<1000000;j++){
//                AgentRequest agentRequest = new AgentRequest().initData("interfaceName", "method", "parameterTypesString", "parameterTypesString");
//                agentRequest.init();

                AgentRequest agentRequest = pool.borrowObject()
                        .initData1("interfaceName", "method", "parameterTypesString", "parameterTypesString");
                try{
                    System.out.println(pool.getNumActive());
                } finally {
                    pool.returnObject(agentRequest);
                }
            }
            e = System.nanoTime();
            System.out.println(e-s);
        }
    }

}
