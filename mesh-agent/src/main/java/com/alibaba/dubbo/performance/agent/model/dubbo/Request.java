package com.alibaba.dubbo.performance.agent.model.dubbo;


import java.util.concurrent.atomic.AtomicLong;

public class Request {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private boolean twoWay = true;
    private boolean event = false;

    private Object mData;

    public Request(){
        id = atomicLong.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public Object getData() {
        return mData;
    }

    public void setData(Object msg) {
        mData = msg;
    }

}
