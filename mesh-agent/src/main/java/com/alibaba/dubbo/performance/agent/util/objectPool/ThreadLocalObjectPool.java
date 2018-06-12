package com.alibaba.dubbo.performance.agent.util.objectPool;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ThreadLocalObjectPool<E extends PoolObject> {

    private final static AtomicInteger threadNo = new AtomicInteger();

    private E[] es;
    private FastThreadLocal<ThreadES> privateES = new FastThreadLocal<>();
    private int threadNum;
    private int privateNum;
    private int bufferNum;
    private ObjectFactory<E> objectFactory;

    class ThreadES{
        int[] availableIds;
        int index = 0;
        int no;

        ThreadES(int no){
            this.no = no;
            availableIds = new int[privateNum];
            IntStream.range(0, threadNum).forEach(
                    item -> availableIds[item] = no * threadNum + item
            );
        }
        boolean gc(){
            boolean hasGc = false;
            int nowIndex = 0;
            for (int i = 0; i < threadNum; i++) {
                int esIndex = no * threadNum + i;
                if(es[esIndex].isAvailable()){
                    hasGc = true;
                    availableIds[nowIndex++] = esIndex;
                }
            }
            if (nowIndex+1<threadNum){
                availableIds[nowIndex] = -1;
            }
            return hasGc;
        }

        public int fetchNext(){
            if (availableIds[index]!=-1 && index<privateNum){
                return availableIds[index++];
            }
            if(gc()){
                return fetchNext();
            }
            return 0;
        }
    }

    public ThreadLocalObjectPool(int threadNum, int privateNum, int bufferNum, ObjectFactory<E> objectFactory){
        this.threadNum = threadNum;
        this.privateNum = privateNum;
        this.bufferNum = bufferNum;
        this.objectFactory = objectFactory;
    }

    private void init(){
        int totalNum = threadNum * privateNum + bufferNum;
        es = (E[])new Object[totalNum];
        for(int i=0; i<totalNum; i++){
            es[i] = objectFactory.create(i);
        }
    }

    private ThreadES initThreadLocal(){
        int no = threadNo.getAndIncrement();
        ThreadES threadES = new ThreadES(no);
        privateES.set(threadES);
        return threadES;
    }

    public E borrowObject() throws Exception{
        ThreadES threadES = privateES.get();
        if (threadES == null){
            threadES = initThreadLocal();
        }
        int id = threadES.fetchNext();
        E e = es[id];
        if (e == null){
            throw new Exception("object pool is not enough");
        }
        return e;
    }

}
