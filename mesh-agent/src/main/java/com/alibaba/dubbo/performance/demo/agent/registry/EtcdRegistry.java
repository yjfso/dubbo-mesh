package com.alibaba.dubbo.performance.demo.agent.registry;

import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.ConnectManager;
import com.alibaba.dubbo.performance.demo.agent.transport.netty.manager.Endpoint;
import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.watch.WatchEvent;
import com.coreos.jetcd.watch.WatchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EtcdRegistry implements IRegistry{
    private Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);
    // 该EtcdRegistry没有使用etcd的Watch机制来监听etcd的事件
    // 添加watch，在本地内存缓存地址列表，可减少网络调用的次数
    // 使用的是简单的随机负载均衡，如果provider性能不一致，随机策略会影响性能

    private final String rootPath = "dubbomesh";
    private Lease lease;
    private KV kv;
    private Watch watch;
    private long leaseId;


    public EtcdRegistry(String registryAddress) {
        Client client = Client.builder().endpoints(registryAddress).build();
        this.lease   = client.getLeaseClient();
        this.kv      = client.getKVClient();
        this.watch   = client.getWatchClient();

        try {
            this.leaseId = lease.grant(30).get().getID();
        } catch (Exception e) {
            e.printStackTrace();
        }
        keepAlive();
    }

    // 向ETCD中注册服务
    public void register(String serviceName,int port) throws Exception {
        // 服务注册的key为:    /dubbomesh/com.some.package.IHelloService/192.168.100.100:2000
        String strKey = MessageFormat.format("/{0}/{1}/{2}:{3}",rootPath,serviceName,IpHelper.getHostIp(),String.valueOf(port));
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString("");     // 目前只需要创建这个key,对应的value暂不使用,先留空
        kv.put(key,val, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        logger.info("Register a new service at:" + strKey);
    }

    // 发送心跳到ETCD,表明该host是活着的
    private void keepAlive(){
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    try {
                        Lease.KeepAliveListener listener = lease.keepAlive(leaseId);
                        listener.listen();
                        logger.info("KeepAlive lease:" + leaseId + "; Hex format:" + Long.toHexString(leaseId));
                    } catch (Exception e) { e.printStackTrace(); }
                }
        );
    }

    public void watch(String serviceName, ConnectManager connectManager) throws Exception{
        this.find(serviceName);
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    String strKey = MessageFormat.format("/{0}/{1}", rootPath, serviceName);
                    ByteSequence key  = ByteSequence.fromString("test1");
                    while (true) {
                        for (WatchEvent event : this.watch.watch(key).listen().getEvents()) {
                            KeyValue kv = event.getKeyValue();
                            String s = kv.getKey().toStringUtf8();
                            int index = s.lastIndexOf("/");
                            String endpointStr = s.substring(index + 1,s.length());

                            String host = endpointStr.split(":")[0];
                            int port = Integer.valueOf(endpointStr.split(":")[1]);
                            Endpoint endpoint = new Endpoint(host,port);
                            switch (event.getEventType()){
                                case PUT:
                                    connectManager.addEndpoint(endpoint);
                                    break;
                                case DELETE:
                                    connectManager.removeEndpoint(endpoint);
                            }

                        }
                    }
                }
        );
    }

    public List<Endpoint> find(String serviceName) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}",rootPath,serviceName);
        ByteSequence key  = ByteSequence.fromString(strKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        List<Endpoint> endpoints = new ArrayList<>();

        for (KeyValue kv : response.getKvs()){
            String s = kv.getKey().toStringUtf8();
            int index = s.lastIndexOf("/");
            String endpointStr = s.substring(index + 1,s.length());

            String host = endpointStr.split(":")[0];
            int port = Integer.valueOf(endpointStr.split(":")[1]);

            endpoints.add(new Endpoint(host,port));
        }
        return endpoints;
    }
}
