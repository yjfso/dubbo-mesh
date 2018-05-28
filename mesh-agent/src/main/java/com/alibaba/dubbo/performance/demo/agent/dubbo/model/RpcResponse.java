package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import java.io.Serializable;

public class RpcResponse implements Serializable {

    private String requestId;
    private byte[] bytes;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
