package com.alibaba.dubbo.performance.agent.model;


import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class AgentRequest implements AgentSerializable {

    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;


    public AgentRequest(){
    }

    public void init(){
        id = atomicLong.getAndIncrement();
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

    public long getId() {
        return id;
    }

    public static AtomicLong getAtomicLong() {
        return atomicLong;
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

    @Override
    public byte[] toBytes() {
        byte[] data = Bytes.mergeByteWithLength(
                interfaceName.getBytes(),
                method.getBytes(),
                parameterTypesString.getBytes(),
                parameter.getBytes()
        );
        byte[] result = new byte[8+ data.length];
        System.arraycopy(Bytes.long2bytes(id), 0, result, 0, 8);
        System.arraycopy(data, 0, result, 8, data.length);
        return result;
    }

    @Override
    public AgentRequest fromBytes(byte[] bytes) {
        byte[] idBytes = new byte[8];
        System.arraycopy(bytes, 0, idBytes , 0,8);
        this.id = Bytes.bytes2long(idBytes);
        String[] strings = Bytes.splitByteToStringsByLength(bytes, 4, 8);
        this.interfaceName = strings[0];
        this.method = strings[1];
        this.parameterTypesString = strings[2];
        this.parameter = strings[3];
        return this;
    }
}
