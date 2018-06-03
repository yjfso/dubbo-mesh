package com.alibaba.dubbo.performance.agent.registry;

import com.alibaba.dubbo.performance.agent.transport.netty.manager.Endpoint;
import com.alibaba.dubbo.performance.agent.transport.netty.manager.ConnectManager;
import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.coreos.jetcd.watch.WatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.Executors;

public class EtcdRegistry implements IRegistry{
    private Logger logger = LoggerFactory.getLogger(EtcdRegistry.class);

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
    public void register(String serviceName, int port, int weight) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}/{2}:{3}",rootPath,serviceName,IpHelper.getHostIp(),String.valueOf(port));
        ByteSequence key = ByteSequence.fromString(strKey);
        ByteSequence val = ByteSequence.fromString(String.valueOf(weight));
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
        this.find(serviceName, connectManager);
        Executors.newSingleThreadExecutor().submit(
                () -> {
                    String strKey = MessageFormat.format("/{0}/{1}", rootPath, serviceName);
                    ByteSequence key  = ByteSequence.fromString(strKey);
                    while (true) {
                        for (WatchEvent event : this.watch.watch(key, WatchOption.newBuilder().withPrefix(key).build()).listen().getEvents()) {
                            KeyValue kv = event.getKeyValue();
                            Endpoint endpoint = analysisKv(kv);

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

    private void find(String serviceName, ConnectManager connectManager) throws Exception {
        String strKey = MessageFormat.format("/{0}/{1}",rootPath,serviceName);
        ByteSequence key  = ByteSequence.fromString(strKey);
        GetResponse response = kv.get(key, GetOption.newBuilder().withPrefix(key).build()).get();

        for (KeyValue kv : response.getKvs()){
            Endpoint endpoint = analysisKv(kv);
            connectManager.addEndpoint(endpoint);
        }
    }

    private Endpoint analysisKv(KeyValue kv){
        String s = kv.getKey().toStringUtf8();
        int index = s.lastIndexOf("/");
        String endpointStr = s.substring(index + 1, s.length());

        String host = endpointStr.split(":")[0];
        int port = Integer.valueOf(endpointStr.split(":")[1]);

        String weightStr = kv.getValue().toStringUtf8();
        return new Endpoint(host, port).setWeight(Integer.valueOf(weightStr));
    }
}
