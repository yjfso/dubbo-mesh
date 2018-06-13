package com.alibaba.dubbo.performance.agent.util.objectPool;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by yinjianfeng on 18/6/11.
 */
public class SimpleObjectPool<E extends PoolObject> {

    private E[] es;
    private int activeNum;
    private int totalNum;
    private int[] availableIds;
    private int fetchIndex = 0;

    private ObjectFactory<E> objectFactory;

    public SimpleObjectPool(int activeNum, ObjectFactory<E> objectFactory) throws Exception{
        this.totalNum = activeNum;
        this.objectFactory = objectFactory;
        this.init();
    }
    public SimpleObjectPool(int activeNum, int bufferNum, ObjectFactory<E> objectFactory) throws Exception{
        this.activeNum = activeNum;
        this.totalNum = bufferNum + activeNum;
        this.objectFactory = objectFactory;
        this.init();
    }

    private void init() throws Exception{
        availableIds = new int[totalNum];
        es = objectFactory.createList(totalNum);
        for(int i=0; i<totalNum; i++){
            availableIds[i] = i;
            es[i] = objectFactory.create(i);
        }
    }

    private boolean gc(){
        fetchIndex = 0;
        boolean hasGc = false;
        int nowIndex = 0;
        for (int i = 0; i < totalNum; i++) {
            if(es[i].isAvailable()){
                hasGc = true;
                availableIds[nowIndex++] = i;
            }
        }
        if (nowIndex < totalNum){
            availableIds[nowIndex] = -1;
        }
        return hasGc;
    }

    public int fetchNextId() throws Exception{
        if (fetchIndex<totalNum && availableIds[fetchIndex]!=-1){
            return availableIds[fetchIndex++];
        }
        if(gc()){
            return availableIds[fetchIndex++];
        }
        throw new Exception("lack object");
    }

    class LoopNum{
        private int active;
        private int max;
        private AtomicInteger atomicInteger = new AtomicInteger();
        private Lock lock = new ReentrantLock();

        LoopNum(int active, int max){
            this.active = active;
            this.max = max;
        }

        int get(){
            return atomicInteger.get();
        }

        int fetchNext(){
            int id = atomicInteger.getAndIncrement();
            if (id>active){
                if(lock.tryLock()){
                    atomicInteger.set(0);
                    lock.unlock();
                }
                if (id>max){
                    id = atomicInteger.getAndIncrement();
                }
            }
            return id;
        }
    }

    public E get(int id){
        return es[id];
    }

    public E borrowObject() throws Exception{
        int id = fetchNextId();
        E e = es[id];
        e.setAvailable(false);
        return e;
    }

    public void returnObject(E e){
        e.setAvailable(true);
    }

}
