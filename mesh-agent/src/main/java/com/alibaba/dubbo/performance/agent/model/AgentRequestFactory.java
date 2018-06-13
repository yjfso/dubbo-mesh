package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.util.objectPool.ObjectFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by yinjianfeng on 18/6/3.
 */
public class AgentRequestFactory implements ObjectFactory<AgentRequest> {

    @Override
    public AgentRequest create(int id) {
        AgentRequest agentRequest = new AgentRequest();
        agentRequest.setId(id);
        return agentRequest;
    }

    @Override
    public AgentRequest[] createList(int num) {
        return new AgentRequest[num];
    }

    @Override
    public void returnObject(AgentRequest agentRequest, int id) {

    }

}
