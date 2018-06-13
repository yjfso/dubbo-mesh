package com.alibaba.dubbo.performance.agent.launcher.consumer;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.agent.launcher.provider.DubboClient;
import com.alibaba.dubbo.performance.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentClient {

    private static Logger logger = LoggerFactory.getLogger(DubboClient.class);
    private ConnectManager connectManager;
    public static AgentClient INSTANCE;

    public static void init(){
        INSTANCE = new AgentClient();
    }

    public AgentClient()  {
        try{
            this.connectManager = new ClientConnectManager(
                    new AgentClientInitializer(), true
            );
            new EtcdRegistry(System.getProperty("etcd.url"))
                    .watch("com.alibaba.dubbo.performance.demo.provider.IHelloService", connectManager);
        } catch (Exception e){
            logger.error("consumer start error", e);
        }

    }

    public void invoke(AgentRequest agentRequest) throws Exception {

        Endpoint endpoint = connectManager.getEndpoint();
        if (endpoint==null){
            throw new Exception("lack endpoint");
        }
        logger.info("route to " + endpoint);
        agentRequest.setEndpoint(endpoint);

        endpoint.writeAndFlush(agentRequest.getCtx(), agentRequest);
    }

}
