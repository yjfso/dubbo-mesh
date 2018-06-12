package com.alibaba.dubbo.performance.agent.util.objectPool;

import org.apache.commons.pool2.BasePooledObjectFactory;

/**
 * Created by yinjianfeng on 18/6/11.
 */
public class SimpleObjectPool<E> {

    private E[] es;
    private int activeNum;
    private int borrowIndex = 0;
    private int returnIndex = 0;
    private ObjectFactory<E> objectFactory;

    public SimpleObjectPool(int activeNum, ObjectFactory<E> objectFactory) throws Exception{
        this.activeNum = activeNum;
        this.objectFactory = objectFactory;
        this.init();
    }

    private void init() throws Exception{
        for(int i=0; i<activeNum; i++){
            es[i] = objectFactory.create();
        }
    }

    public E borrowObject() throws Exception{
        if (borrowIndex >= activeNum){
            borrowIndex = 0;
        }
        E e = es[borrowIndex];
        if (e == null){
            return objectFactory.create();
        }
        es[borrowIndex++] = null;
        return e;
    }

    public void returnObject(E e){
        if (returnIndex >= activeNum){
            returnIndex = 0;
        }
        es[returnIndex++] = e;
    }
}
