package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.model.dubbo.RpcFuture;
import com.alibaba.dubbo.performance.agent.model.AgentRequestHolder;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Consumer {

    private static Logger logger = LoggerFactory.getLogger(DubboClient.class);
    private ConnectManager connectManager;
    public static Consumer INSTANCE;

    public static void init(){
        INSTANCE = new Consumer();
    }

    private Consumer()  {
        try{
            this.connectManager = new ClientConnectManager(
                    new ConsumerInitializer(this)
            );
            new EtcdRegistry(System.getProperty("etcd.url"))
                    .watch("com.alibaba.dubbo.performance.demo.provider.IHelloService", connectManager);
        } catch (Exception e){
            logger.error("consumer start error", e);
        }
    }

    public Object invoke(AgentRequest agentRequest) throws Exception {

        Endpoint endpoint = connectManager.getEndpoint();


        Channel channel = endpoint.getChannelManager().getChannel();
        if (channel == null){
            return null;
        }
        channel.writeAndFlush(agentRequest);

        RpcFuture future = new RpcFuture();
        AgentRequestHolder.put(agentRequest.getId(), future);
        endpoint.request();
        Object result = null;
        try {
            result = future.get();
            AgentRequestHolder.remove(agentRequest.getId());
            endpoint.response();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ConnectManager getConnectManager() {
        return connectManager;
    }
}
