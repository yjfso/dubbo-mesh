package com.alibaba.dubbo.performance.agent.model;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by yinjianfeng on 18/6/3.
 */
public class AgentRequestFactory extends BasePooledObjectFactory<AgentRequest>{


    @Override
    public AgentRequest create() throws Exception {
        return new AgentRequest();
    }

    @Override
    public PooledObject<AgentRequest> wrap(AgentRequest agentRequest) {
        return new DefaultPooledObject<>(agentRequest);
    }

    public void activateObject(PooledObject<AgentRequest> p) throws Exception {
//        p.getObject().init();
    }
}
