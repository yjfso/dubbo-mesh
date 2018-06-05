package com.alibaba.dubbo.performance.agent.transport.netty.manager;

import io.netty.bootstrap.Bootstrap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConnectManager {

    Bootstrap bootstrap;

    public Bootstrap getBootstrap() {
        return bootstrap;
    }
}
