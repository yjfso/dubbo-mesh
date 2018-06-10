package com.alibaba.dubbo.performance.agent.launcher.provider;

import com.alibaba.dubbo.performance.agent.model.Bytes;
import com.alibaba.dubbo.performance.agent.model.dubbo.Request;
import com.alibaba.dubbo.performance.agent.model.dubbo.RequestFactory;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.JsonUtils;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcInvocation;

import com.alibaba.dubbo.performance.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import com.alibaba.dubbo.performance.agent.util.ObjectPoolUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DubboClient {
    private Logger logger = LoggerFactory.getLogger(DubboClient.class);
    final Map<Long, Request> processingRpc = new ConcurrentHashMap<>(200);

    ConnectManager connectManager;
    public final static ObjectPool<Request> pool = new GenericObjectPool<>(new RequestFactory(), ObjectPoolUtils.getConfig(230));

    public DubboClient(){
        int port = Integer.valueOf(System.getProperty("dubbo.protocol.port"));
        this.connectManager = new ClientConnectManager(
                new DubboClientInitializer(this), false
        ).addEndpoint(
                new Endpoint("127.0.0.1", port)
        );
    }

    public void invoke(byte[] bytes, ChannelHandlerContext ctx) throws Exception {
        long requestId = Bytes.bytes2long(bytes);
        String[] strings = Bytes.splitByteToStringsByLength(bytes, 4, 8);
        String interfaceName = strings[0];
        String method = strings[1];
        String parameterTypesString = strings[2];
        String parameter = strings[3];

        Channel channel = connectManager.getEndpoint().getChannelManager().getChannel();

        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(method);
        invocation.setAttachment("path", interfaceName);
        invocation.setParameterTypes(parameterTypesString);    // Dubbo内部用"Ljava/lang/String"来表示参数类型是String

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        JsonUtils.writeObject(parameter, writer);
        invocation.setArguments(out.toByteArray());

        Request request = pool.borrowObject();
        request.setId(requestId);
        request.setResponseData(ctx);
//        new Request();
//        request.setTwoWay(true);
        request.setData(invocation);

//        RpcFuture future = new RpcFuture();
//        AgentRequestHolder.put(request.getId(),future);
        processingRpc.put(requestId, request);

        channel.writeAndFlush(request);

//        Object result = null;
//        try {
//            result = future.get();
//            AgentRequestHolder.remove(request.getId());
//        }catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            pool.returnObject(request);
//        }
//        return result;
    }
}
