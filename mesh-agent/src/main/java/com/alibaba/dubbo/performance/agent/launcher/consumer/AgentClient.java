package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentClient {

    private static Logger logger = LoggerFactory.getLogger(DubboClient.class);
    private ConnectManager connectManager;
    public static AgentClient INSTANCE;
    final static Map<Long,AgentRequest> processingRpc = new ConcurrentHashMap<>(500);

    public static void init(){
        INSTANCE = new AgentClient();
    }

    public AgentClient()  {
        try{
            this.connectManager = new ClientConnectManager(
                    new AgentClientInitializer(this), true
            );
            new EtcdRegistry(System.getProperty("etcd.url"))
                    .watch("com.alibaba.dubbo.performance.demo.provider.IHelloService", connectManager);
        } catch (Exception e){
            logger.error("consumer start error", e);
        }
    }

    public boolean invoke(AgentRequest agentRequest) throws Exception {

        Endpoint endpoint = connectManager.getEndpoint();
        logger.info("route to " + endpoint);

        Channel channel = endpoint.getChannelManager().getChannel();
        if (channel == null){
            return false;
        }
        endpoint.request();
        agentRequest.setEndpoint(endpoint);
        processingRpc.put(agentRequest.getId(), agentRequest);
        channel.writeAndFlush(agentRequest.toBytes());
        return true;

//        RpcFuture future = new RpcFuture();
//        AgentRequestHolder.put(agentRequest.getId(), future);
//        Object result = null;
//        try {
//            result = future.get();
//            AgentRequestHolder.remove(agentRequest.getId());
//            endpoint.response();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return result;
    }

    public ConnectManager getConnectManager() {
        return connectManager;
    }
}
