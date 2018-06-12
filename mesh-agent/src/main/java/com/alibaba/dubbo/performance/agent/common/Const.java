package com.alibaba.dubbo.performance.agent.common;

/**
 * Created by yinjianfeng on 18/6/10.
 */
public class Const {

    public final static byte CR = 13;
    public final static byte QUOTA = 34;
    public final static byte PERCENT = 37;
    public final static byte[] DUBBO_VERSION = "2.0.1".getBytes();
    public final static byte[] NULL = "null".getBytes();
    public final static byte[] VOID_JSON = "{}".getBytes();

    public final static int LOAD_BALANCE_REFRESH_TIME = 5;

    public final static int AGENT_REQUEST_NUM = 650;
    public final static int DUBBO_REQUEST_NUM = 200;

    public final static int MAX_DUBBO_REQUEST = 210;
    public final static int CONSUMER_THREAD_NUM = 600;
}