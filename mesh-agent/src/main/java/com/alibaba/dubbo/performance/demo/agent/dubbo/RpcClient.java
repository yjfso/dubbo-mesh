package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.JsonUtils;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Request;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcInvocation;

import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RpcClient {
    private Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private ConnectManager connectManager;

    public RpcClient(){
        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
        this.connectManager = new ClientConnectManager(
                new RpcClientInitializer()
        ).addEndpoint(
                new Endpoint("127.0.0.1", port)
        );
    }

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = connectManager.getEndpoint().getChannel();

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        Request request = new Request();
        request.setVersion("2.0.0");
        request.setTwoWay(true);
        request.setData(invocation);

        logger.info("requestId=" + request.getId());

        RpcFuture future = new RpcFuture();
        AgentRequestHolder.put(request.getId(),future);

        channel.writeAndFlush(request);

        Object result = null;
        try {
            result = future.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
