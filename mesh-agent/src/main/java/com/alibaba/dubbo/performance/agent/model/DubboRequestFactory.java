package com.alibaba.dubbo.performance.agent.model;

import com.alibaba.dubbo.performance.agent.util.objectPool.ObjectFactory;

/**
 * Created by yinjianfeng on 18/6/3.
 */
public class DubboRequestFactory implements ObjectFactory<DubboRequest> {

    @Override
    public DubboRequest create(int id) {
        DubboRequest dubboRequest = new DubboRequest();
        dubboRequest.setId(id);
        return dubboRequest;
    }

    @Override
    public DubboRequest[] createList(int num) {
        return new DubboRequest[num];
    }

    @Override
    public void returnObject(DubboRequest dubboRequest, int id) {

    }

}
