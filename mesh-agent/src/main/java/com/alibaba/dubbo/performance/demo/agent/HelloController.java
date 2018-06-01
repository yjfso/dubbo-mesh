package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);

    Client client = new Client();
//    private OkHttpClient httpClient = new OkHttpClient();
//    private RpcClient rpcClient = new RpcClient();

    public HelloController() throws Exception {
    }

    @RequestMapping(value = "")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception {
        return consumer(interfaceName,method,parameterTypesString,parameter);
//        String type = System.getProperty("type");   // 获取type参数
//        if ("consumer".equals(type)){
//            return consumer(interfaceName,method,parameterTypesString,parameter);
//        }
//        else if ("provider".equals(type)){
//            return provider(interfaceName,method,parameterTypesString,parameter);
//        }else {
//            return "Environment variable type is needed to set to provider or consumer.";
//        }
    }

//    public byte[] provider(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {
//
//        Object result = rpcClient.invoke(interfaceName,method,parameterTypesString,parameter);
//        return (byte[]) result;
//    }

    public Integer consumer(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {
        AgentRequest agentRequest = new AgentRequest(interfaceName, method, parameterTypesString, parameter);
        byte[] bytes = (byte[]) client.invoke(agentRequest);
        String s = new String(bytes, 8, bytes.length-8);
        return Integer.valueOf(s);
//        // 简单的负载均衡，随机取一个
//        Endpoint endpoint = LoadBalance.getEndpoint();
//        endpoint.agentRequest();
//        String url =  "http://" + endpoint.getHost() + ":" + endpoint.getPort();
//
//        RequestBody requestBody = new FormBody.Builder()
//                .add("interface",interfaceName)
//                .add("method",method)
//                .add("parameterTypesString",parameterTypesString)
//                .add("parameter",parameter)
//                .build();
//
//        AgentRequest agentRequest = new AgentRequest.Builder()
//                .url(url)
//                .post(requestBody)
//                .build();
//
//        try (Response response = httpClient.newCall(agentRequest).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            byte[] bytes = response.body().bytes();
//            String s = new String(bytes);
//            return Integer.valueOf(s);
//        } finally {
//            endpoint.response();
//        }
    }
}
