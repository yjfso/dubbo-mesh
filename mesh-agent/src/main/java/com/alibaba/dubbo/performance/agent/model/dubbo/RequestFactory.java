package com.alibaba.dubbo.performance.agent.model.dubbo;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by yinjianfeng on 18/6/3.
 */
public class RequestFactory extends BasePooledObjectFactory<Request>{


    @Override
    public Request create() throws Exception {
        return new Request();
    }

    @Override
    public PooledObject<Request> wrap(Request request) {
        return new DefaultPooledObject<>(request);
    }

    public void activateObject(PooledObject<Request> p) throws Exception {
        p.getObject().init();
    }
}
