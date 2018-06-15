package com.alibaba.dubbo.performance.agent.model;


import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.transport.netty.http.HttpUtils;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.objectPool.AbstractPoolObject;
import com.alibaba.dubbo.performance.agent.util.objectPool.SimpleObjectPool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentRequest extends AbstractRequest {

    private final static Logger log = LoggerFactory.getLogger(AgentRequest.class);

    private ByteBufHolder byteBufHolder;
    private boolean keepAlive = true;

    private static FastThreadLocal<SimpleObjectPool<AgentRequest>> privateAgentRequest = new FastThreadLocal<>();


    public static SimpleObjectPool<AgentRequest> getPool() throws Exception{
        SimpleObjectPool<AgentRequest> pool = privateAgentRequest.get();
        if (pool == null){
            pool = new SimpleObjectPool<>(Const.AGENT_REQUEST_NUM, new AgentRequestFactory());
            privateAgentRequest.set(pool);
        }
        return pool;
    }

    public void returnSelf() throws Exception{
        super.returnSelf();
        getPool().returnObject(this);
    }

    public static AgentRequest getAgentRequest() throws Exception{
        return getPool().borrowObject();
    }

    public void setByteBufHolder(ByteBufHolder byteBufHolder){
        this.byteBufHolder = byteBufHolder;
    }

    public ByteBufHolder getByteBufHolder(){
        return byteBufHolder;
    }


    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }


    public void done(FullHttpResponse rep) throws Exception{
        HttpUtils.response(getCtx(), keepAlive, rep);
        returnSelf();
    }

    public void done(ByteBuf byteBuf) throws Exception {
        FullHttpResponse rep = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf.slice(4, byteBuf.readableBytes()));
        done(rep);
    }

}
