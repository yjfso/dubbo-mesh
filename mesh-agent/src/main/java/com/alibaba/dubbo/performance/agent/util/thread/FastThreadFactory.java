package com.alibaba.dubbo.performance.agent.util.thread;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.InternalThreadLocalMap;

import java.util.concurrent.ThreadFactory;

/**
 * Created by yinjianfeng on 18/6/11.
 */
public class FastThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        FastThreadLocalThread thread = new FastThreadLocalThread(r);
        return thread;
    }
}
