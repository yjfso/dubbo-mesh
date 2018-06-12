package com.alibaba.dubbo.performance.agent.util.objectPool;

import com.alibaba.dubbo.performance.agent.model.AgentRequest;
import com.alibaba.dubbo.performance.agent.model.AgentRequestFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Created by yinjianfeng on 18/6/11.
 */
public class SimpleObjectPool<E> {

    private E[] es;
    private int activeNum;
    private int totalNum;
    private LoopNum borrowNum = new LoopNum(activeNum, totalNum);
    private LoopNum returnNum = new LoopNum(activeNum, totalNum);
    private ObjectFactory<E> objectFactory;

    public SimpleObjectPool(int activeNum, int bufferNum, ObjectFactory<E> objectFactory) throws Exception{
        this.activeNum = activeNum;
        this.totalNum = bufferNum + activeNum;
        this.objectFactory = objectFactory;
        this.init();
    }

    private void init() throws Exception{
        es = (E[])new Object[totalNum];
        for(int i=0; i<totalNum; i++){
            es[i] = objectFactory.create(i);
        }
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

    public E borrowObject() throws Exception{
        int id = borrowNum.fetchNext();
        E e = es[id];
        if (e == null){
            throw new Exception("object pool is not enough");
        }
        return e;
    }

    public void returnObject(E e){
        int id = returnNum.fetchNext();
        objectFactory.returnObject(e, id);
        es[id] = e;
    }

    public static void main(String[] args) throws Exception {

    }
}
