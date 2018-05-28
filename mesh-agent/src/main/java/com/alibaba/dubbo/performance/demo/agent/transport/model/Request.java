package com.alibaba.dubbo.performance.demo.agent.transport.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by yinjianfeng on 18/5/27.
 */
public class Request implements Serializable {

    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;


    public Request(String interfaceName, String method, String parameterTypesString, String parameter){
        id = atomicLong.getAndIncrement();
        this.interfaceName = interfaceName;
        this.method = method;
        this.parameterTypesString = parameterTypesString;
        this.parameter = parameter;
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
}
