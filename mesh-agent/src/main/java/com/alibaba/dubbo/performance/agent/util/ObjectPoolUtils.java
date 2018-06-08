package com.alibaba.dubbo.performance.agent.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by yinjianfeng on 18/6/8.
 */
public class ObjectPoolUtils {

    public final static GenericObjectPoolConfig config;

    static {
        config = new GenericObjectPoolConfig();
        config.setMaxIdle(250);
        config.setMaxTotal(250);
        config.setMinIdle(250);
    }
}
