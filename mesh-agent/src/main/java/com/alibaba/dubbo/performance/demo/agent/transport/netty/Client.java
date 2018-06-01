package com.alibaba.dubbo.performance.demo.agent.transport.netty;

import com.alibaba.dubbo.performance.demo.agent.dubbo.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.transport.model.*;
import com.alibaba.dubbo.performance.demo.agent.transport.model.AgentRequest;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ClientConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;


/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Client {

    private ConnectManager connectManager;

    public Client() throws Exception {
        this.connectManager = new ClientConnectManager(
                new ClientInitializer()
        ).setEndPoints(
                EtcdRegistry.registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService")
        );
    }

    public Object invoke(AgentRequest agentRequest) throws Exception {
        Endpoint endpoint = connectManager.getEndpoint();

        RpcFuture future = new RpcFuture();
        AgentRequestHolder.put(agentRequest.getId(), future);

//        endpoint.request();

        endpoint.getChannel().writeAndFlush(agentRequest);
        Object result = null;
        try {
            result = future.get();
//            endpoint.response();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
