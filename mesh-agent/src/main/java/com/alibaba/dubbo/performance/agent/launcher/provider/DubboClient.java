package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.dubbo.Request;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.JsonUtils;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;

import com.alibaba.dubbo.performance.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class DubboClient {
    private Logger logger = LoggerFactory.getLogger(DubboClient.class);

    private ConnectManager connectManager;

    public DubboClient(){
        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
        this.connectManager = new ClientConnectManager(
                new DubboClientInitializer()
        ).addEndpoint(
                new Endpoint("127.0.0.1", port)
        );
    }

    public Object invoke(String interfaceName, String method, String parameterTypesString, String parameter) throws Exception {

        Channel channel = connectManager.getEndpoint().getChannelManager().getChannel();

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
