package com.alibaba.dubbo.performance.agent.model.dubbo;

import com.alibaba.dubbo.performance.agent.model.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RpcFuture implements Future<Object> {

    private static final Logger log = LoggerFactory.getLogger(RpcFuture.class);
    private CountDownLatch latch = new CountDownLatch(1);

    private final static byte[] ERROR = "000000000".getBytes();

    private AgentResponse response;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Object get() throws InterruptedException {
         //boolean b = latch.await(100, TimeUnit.MICROSECONDS);
        latch.await();//3, TimeUnit.SECONDS);
        try {
            return response.getBytes();
        }catch (Exception e){
            log.error("get response timeout");
        }
        return ERROR;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean b = latch.await(timeout,unit);
        return response.getBytes();
    }

    public void done(AgentResponse response){
        this.response = response;
        latch.countDown();
    }
}
