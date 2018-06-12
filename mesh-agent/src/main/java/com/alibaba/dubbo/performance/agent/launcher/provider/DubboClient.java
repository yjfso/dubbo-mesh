package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.Bytes;
import com.alibaba.dubbo.performance.agent.model.dubbo.HTTPDecoder;
import com.alibaba.dubbo.performance.agent.model.dubbo.Request;
import com.alibaba.dubbo.performance.agent.model.dubbo.RequestFactory;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.JsonUtils;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import com.alibaba.dubbo.performance.agent.util.ObjectPoolUtils;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AsciiString;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DubboClient {
    private Logger logger = LoggerFactory.getLogger(DubboClient.class);

    ConnectManager connectManager;


    public DubboClient(){
        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
        this.connectManager = new ClientConnectManager(
                new DubboClientInitializer(), Provider.workerGroup, false
        ).addEndpoint(
                new Endpoint("127.0.0.1", port)
        );
    }

    public void invoke(byte[] bytes, ChannelHandlerContext ctx) throws Exception {
        Map<String, byte[]> pars = HTTPDecoder.decode(bytes, 4);

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(pars.get("method"));
        invocation.setInterfaceName(pars.get("interface"));
        invocation.setParameterTypes(pars.get("parameterTypesString"));    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
        invocation.setArguments(pars.get("parameter"));

        Request request = Request.getRequest();
        request.setAgentRequest(bytes);
        request.setResponseData(ctx);
//        new Request();
//        request.setTwoWay(true);
        request.setData(invocation);

        connectManager.getEndpoint().writeAndFlush(request);

    }
}
