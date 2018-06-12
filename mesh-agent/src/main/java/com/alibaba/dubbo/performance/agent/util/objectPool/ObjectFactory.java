package com.alibaba.dubbo.performance.agent.util.objectPool;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;

/**
 * Created by yinjianfeng on 18/6/11.
 */
public interface ObjectFactory<E> {

    E create() throws Exception;

}
