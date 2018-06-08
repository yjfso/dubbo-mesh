package com.alibaba.dubbo.performance.agent.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by yinjianfeng on 18/6/8.
 */
public class ObjectPoolUtils {


    public static GenericObjectPoolConfig getConfig(int num){
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(num);
        config.setMaxTotal(num);
        config.setMinIdle(num);
        return config;
    }
}
