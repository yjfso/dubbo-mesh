package com.alibaba.dubbo.performance.demo.agent.transport.model;

import java.io.Serializable;

public class AgentResponse implements AgentSerializable {

    private long requestId;
    private byte[] bytes;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }


    @Override
    public byte[] toBytes() {
        byte[] result = new byte[8+ bytes.length];
        System.arraycopy(Bytes.long2bytes(requestId), 0, result, 0, 8);
        System.arraycopy(bytes, 0, result, 8, bytes.length);
        return result;
    }

    @Override
    public AgentResponse fromBytes(byte[] bytes) {
        byte[] idBytes = new byte[8];
        System.arraycopy(bytes, 0, idBytes , 0,8);
        this.requestId = Bytes.bytes2long(idBytes);
        this.bytes = bytes;
        return this;
    }
}
