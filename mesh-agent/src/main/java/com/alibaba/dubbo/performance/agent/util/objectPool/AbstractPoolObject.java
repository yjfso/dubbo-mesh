package com.alibaba.dubbo.performance.agent.util.objectPool;

public abstract class AbstractPoolObject implements PoolObject {

    private int id;
    private boolean available;

    public boolean isAvailable(){
        return available;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }


}
