package com.alibaba.dubbo.performance.agent.model;



import com.alibaba.dubbo.performance.agent.common.Const;
import com.alibaba.dubbo.performance.agent.transport.netty.http.HttpUtils;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.util.ObjectPoolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentRequest implements AgentSerializable {


    private final static Logger log = LoggerFactory.getLogger(AgentRequest.class);
    public final static ObjectPool<AgentRequest> pool =
            new GenericObjectPool<>(new AgentRequestFactory(), ObjectPoolUtils.getConfig(Const.AGENT_REQUEST_NUM));
    public final static AgentRequest[] requests = new AgentRequest[Const.AGENT_REQUEST_NUM];
    private static AtomicInteger atomicInteger = new AtomicInteger();
    private int id;
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;
    private Endpoint endpoint;
    private ChannelHandlerContext ctx;
    private boolean keepAlive = true;


    public AgentRequest(){
        id = atomicInteger.getAndIncrement();
        requests[id] = this;
    }

    public AgentRequest initRequest(){
        return this;
    }

    public AgentRequest initData(String interfaceName, String method, String parameterTypesString, String parameter){
        this.interfaceName = interfaceName;
        this.method = method;
        this.parameterTypesString = parameterTypesString;
        this.parameter = parameter;
        return this;
    }

    public AgentRequest initData1(String interfaceName, String method, String parameterTypesString, String parameter){
        if (!interfaceName.equals(this.interfaceName)) this.interfaceName = interfaceName;
        if (!method.equals(this.method)) this.method = method;
        if (!parameterTypesString.equals(this.parameterTypesString)) this.parameterTypesString = parameterTypesString;
        if (!parameter.equals(this.parameter)) this.parameter = parameter;
        return this;
    }

    public int getId() {
        return id;
    }

    public static AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public String getParameter() {
        return parameter;
    }

    public boolean isValid(){
        return parameter!=null && parameterTypesString!=null && interfaceName!=null && method!=null;
    }

    public static AgentRequest fromMap(Map<String, String> paramters){
        AgentRequest agentRequest;
        String interfaceName = paramters.get("interface");
        String method = paramters.get("method");
        String parameterTypesString = paramters.get("parameterTypesString");
        String parameter = paramters.get("parameter");
        try{
            agentRequest = pool.borrowObject();
        } catch (Exception e){
            agentRequest = new AgentRequest();//.initRequest();
        }
        return agentRequest.initData(interfaceName, method, parameterTypesString, parameter);
    }
    @Override
    public byte[] toBytes() {
        byte[] data = Bytes.mergeByteWithLength(
                interfaceName.getBytes(),
                method.getBytes(),
                parameterTypesString.getBytes(),
                parameter.getBytes()
        );
        byte[] result = new byte[4 + data.length];
        System.arraycopy(Bytes.int2bytes(id), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, data.length);
        return result;
    }

    @Override
    public AgentRequest fromBytes(byte[] bytes) {
        this.id = Bytes.bytes2int(bytes, 0);
        String[] strings = Bytes.splitByteToStringsByLength(bytes, 4, 8);
        this.interfaceName = strings[0];
        this.method = strings[1];
        this.parameterTypesString = strings[2];
        this.parameter = strings[3];
        return this;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    private void returnSelf() throws Exception{
        endpoint = null;
        ctx = null;
        pool.returnObject(this);
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void done(FullHttpResponse rep) throws Exception{
        HttpUtils.response(ctx, keepAlive,  rep);
        returnSelf();
    }

    public void done(byte[] bytes) throws Exception {
        endpoint.response();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes, 4, bytes.length-4);
        FullHttpResponse rep = new DefaultFullHttpResponse(HTTP_1_1, OK, byteBuf);
        done(rep);
    }
}
