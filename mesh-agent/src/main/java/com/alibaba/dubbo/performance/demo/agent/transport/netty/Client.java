package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.transport.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Client {

    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private ConnectManager connectManager;
    public static Client INSTANCE;

    public static void init(){
        INSTANCE = new Client();
    }

    private Client()  {
        try{
            this.connectManager = new ClientConnectManager(
                    new ClientInitializer(this)
            );
            new EtcdRegistry(System.getProperty("etcd.url"))
                    .watch("com.alibaba.dubbo.performance.demo.provider.IHelloService", connectManager);
        } catch (Exception e){
            logger.error("client start error", e);
        }
    }

    public Object invoke(AgentRequest agentRequest) throws Exception {
        Endpoint endpoint = connectManager.getEndpoint();

        RpcFuture future = new RpcFuture();
        AgentRequestHolder.put(agentRequest.getId(), future);

//        endpoint.request();

        endpoint.getChannelManager().getChannel().writeAndFlush(agentRequest);
        Object result = null;
        try {
            result = future.get();
//            endpoint.response();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    ConnectManager getConnectManager() {
        return connectManager;
    }
}
