package com.alibaba.dubbo.performance.agent.util.objectPool;

/**
 * Created by yinjianfeng on 18/6/13.
 */
public interface PoolObject {

    void setId(int id);

    int getId();

    boolean isAvailable();

    void setAvailable(boolean available);

}
