package com.alibaba.dubbo.performance.agent.model.dubbo;

import com.alibaba.dubbo.performance.agent.model.DubboRequest;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Created by yinjianfeng on 18/6/3.
 */
public class RequestFactory extends BasePooledObjectFactory<DubboRequest>{


    @Override
    public DubboRequest create() throws Exception {
        return new DubboRequest();
    }

    @Override
    public PooledObject<DubboRequest> wrap(DubboRequest dubboRequest) {
        return new DefaultPooledObject<>(dubboRequest);
    }

//    public void activateObject(PooledObject<DubboRequest> p) throws Exception {
//        p.getObject().init();
//    }
}
