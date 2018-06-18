package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.DubboRequest;
import com.alibaba.dubbo.performance.agent.model.dubbo.HTTPDecoder;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.*;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DubboClient {

    private final static Logger log = LoggerFactory.getLogger(DubboClient.class);

    ConnectManager connectManager;


    public DubboClient(){
        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
        this.connectManager = new ClientConnectManager(
                new DubboClientInitializer(), false
        ).addEndpoint(
                new Endpoint("127.0.0.1", port)
        );
    }

    public void invoke(DubboRequest dubboRequest, ChannelFuture channelFuture) throws Exception {
        byte[] byteBuf = dubboRequest.getAgentRequest();
        Map<String, byte[]> pars = HTTPDecoder.decode(byteBuf, 0);

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(pars.get("method"));
        invocation.setInterfaceName(pars.get("interface"));
        invocation.setParameterTypes(pars.get("parameterTypesString"));    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
        invocation.setArguments(pars.get("parameter"));

        dubboRequest.setData(invocation);

//        dubboRequest.getChannelWriter().smartWrite(dubboRequest);

        ChannelFutureWriter.getInstance(channelFuture).smartWrite(dubboRequest);
//        ChannelWriter.writeAndFlush(channelFuture, dubboRequest);

    }

    public ConnectManager getConnectManager() {
        return connectManager;
    }
}
